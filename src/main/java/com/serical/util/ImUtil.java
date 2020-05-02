package com.serical.util;

import cn.hutool.core.date.DateUtil;
import com.serical.client.im.ClientContext;
import com.serical.common.ImConstants;
import com.serical.common.ImMessage;
import com.serical.common.MessageType;
import io.netty.channel.ChannelHandlerContext;

/**
 * im工具类
 *
 * @author serical 2020-05-02 04:24:46
 */
public class ImUtil {

    /**
     * 客户端发送消息
     *
     * @param message 消息
     */
    public static void sendMessage(ImMessage message) {
        ClientContext.getCurrentUser().getChannelFuture().channel().writeAndFlush(message);
    }

    /**
     * 服务端发送成功消息
     *
     * @param ctx      连接
     * @param receiver 接收方
     * @param message  文本消息
     */
    public static void sendSuccessTextMessage(ChannelHandlerContext ctx, String receiver, String message) {
        sendMessage(ctx, receiver, 0L, MessageType.TEXT_MESSAGE, message);
    }

    /**
     * 服务端发送失败消息
     *
     * @param ctx      连接
     * @param receiver 接收方
     * @param message  文本消息
     */
    public static void sendErrorTextMessage(ChannelHandlerContext ctx, String receiver, String message) {
        sendMessage(ctx, receiver, -1L, MessageType.TEXT_MESSAGE, message);
    }

    /**
     * 服务端发送消息
     *
     * @param ctx      连接
     * @param receiver 接收方
     * @param code     状态码
     * @param message  文本消息
     */
    public static void sendMessage(ChannelHandlerContext ctx, String receiver, long code, MessageType messageType, String message) {
        ctx.writeAndFlush(ImMessage.builder()
                .sender(ImConstants.defaultServerUid)
                .receiver(receiver)
                .messageType(messageType)
                .code(code)
                .message(message)
                .createTime(DateUtil.date())
                .build());
    }
}
