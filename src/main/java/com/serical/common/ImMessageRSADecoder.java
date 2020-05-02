package com.serical.common;

import com.serical.client.im.ClientContext;
import com.serical.util.RSAUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.security.PrivateKey;
import java.util.List;

public class ImMessageRSADecoder extends MessageToMessageDecoder<ImMessage> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ImMessage message, List<Object> out) throws Exception {
        // 点对点消息使用当前用户私钥解密
        if (message.getMessageType() == MessageType.TEXT_MESSAGE) {
            final PrivateKey privateKey = ClientContext.getCurrentUser().getPrivateKey();
            message.setMessage(RSAUtil.decrypt(message.getMessage() + "", privateKey));
        }

        out.add(message);
    }
}