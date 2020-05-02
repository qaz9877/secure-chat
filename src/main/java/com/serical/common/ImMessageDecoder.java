package com.serical.common;

import cn.hutool.core.util.ArrayUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.util.List;

public class ImMessageDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        // decode
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        final ImMessage message = objectMapper.readValue(ArrayUtil.addAll(bytes, new byte[]{10}), ImMessage.class);

        // 解密
        if (message.getMessageType() == MessageType.TEXT_MESSAGE) {
            // TODO RSA解密消息体
        }

        out.add(message);
    }
}