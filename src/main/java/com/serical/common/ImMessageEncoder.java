package com.serical.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serical.client.im.ClientContext;
import com.serical.util.RSAUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.security.PublicKey;

public class ImMessageEncoder extends MessageToByteEncoder<ImMessage> {

    private final ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());

    @Override
    protected void encode(ChannelHandlerContext ctx, ImMessage msg, ByteBuf out) throws Exception {
        // 点对点消息使用接收方公钥加密
        if (msg.getMessageType() == MessageType.TEXT_MESSAGE) {
            final PublicKey publicKey = ClientContext.getUserPublicKey().get(msg.getReceiver());
            if (null != publicKey) {
                msg.setMessage(RSAUtil.encrypt(msg.getMessage() + "", publicKey));
            }
        }

        out.writeBytes(objectMapper.writeValueAsBytes(msg));
    }
}
