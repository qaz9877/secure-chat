package com.serical.common;

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
        final ImMessage message = objectMapper.readValue(bytes, ImMessage.class);
        out.add(message);
    }
}