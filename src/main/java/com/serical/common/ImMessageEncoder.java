package com.serical.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.jackson.dataformat.MessagePackFactory;

public class ImMessageEncoder extends MessageToByteEncoder<ImMessage> {

    private final ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());

    @Override
    protected void encode(ChannelHandlerContext ctx, ImMessage msg, ByteBuf out) throws Exception {
        // 加密
        if (msg.getMessageType() == MessageType.TEXT_MESSAGE) {
            // TODO RSA加密消息体
        }

        // encode
        out.writeBytes(objectMapper.writeValueAsBytes(msg));
    }
}
