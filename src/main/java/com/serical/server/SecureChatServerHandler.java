package com.serical.server;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.serical.common.ImConstants;
import com.serical.common.ImMessage;
import com.serical.common.ImUser;
import com.serical.common.MessageType;
import com.serical.util.ImUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SecureChatServerHandler extends SimpleChannelInboundHandler<ImMessage> {

    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        // Once session is secured, send a greeting and register the channel to the global channel
        // list so the channel received the messages from others.
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                (GenericFutureListener<Future<Channel>>) future -> {
                    // 首次建立连接, 未设置receiver
                    ctx.writeAndFlush(ImMessage.builder()
                            .sender(ImConstants.defaultServerUid)
                            .messageType(MessageType.CONNECT)
                            .message("Welcome to " + InetAddress.getLocalHost().getHostName() + " secure chat service!"
                                    + " Your session is protected by " +
                                    ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() +
                                    " cipher suite.")
                            .createTime(DateUtil.date())
                            .build()
                    );
                    channels.add(ctx.channel());
                });
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ImMessage msg) throws Exception {
        if (msg.getMessageType() == MessageType.REGISTER) {
            handlerRegister(ctx, msg);
        } else if (msg.getMessageType() == MessageType.ONLINE) {
            handlerOnline(ctx, msg);
        } else if (msg.getMessageType() == MessageType.TEXT_MESSAGE) {
            handlerTextMessage(ctx, msg);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 查找下线的连接对象
        Map.Entry<String, ChannelHandlerContext> removeEntry = null;
        for (Map.Entry<String, ChannelHandlerContext> entry : ServerContext.getOnlineChannels().entrySet()) {
            if (ctx.equals(entry.getValue())) {
                removeEntry = entry;
                break;
            }
        }

        if (null == removeEntry) {
            return;
        }

        // 从在线连接池中移除下线用户连接
        ServerContext.getOnlineChannels().remove(removeEntry.getKey());
        // 从在线用户信息中移除该用户
        final String userName = ServerContext.getOnlineUsers().get(removeEntry.getKey());
        ServerContext.getOnlineUsers().remove(removeEntry.getKey());

        // 循环发送下线通知, 刷新每个客户端用户列表
        ServerContext.getOnlineChannels().forEach((k, v) -> {
            // 发送下线系统通知
            ImUtil.sendMessage(v, k, 0L, MessageType.SYSTEM_MESSAGE, "用户[" + userName + "]下线了, 好聚好散🤚");

            // 服务端主动更新每个客户端的用户列表
            refreshClientOnlineUser(v, k);
        });
    }

    /**
     * 处理注册请求
     *
     * @param ctx 连接
     * @param msg 客户端发来的消息
     */
    private void handlerRegister(ChannelHandlerContext ctx, ImMessage msg) {
        // 记录用户信息与连接信息
        ServerContext.getOnlineUsers().put(msg.getSender(), msg.getMessage() + "");
        ServerContext.getOnlineChannels().put(msg.getSender(), ctx);

        // 通知所有人当前用户上线了
        ServerContext.getOnlineChannels().forEach((k, v) -> {
                    // 发送上线文本通知
                    ImUtil.sendMessage(v, k, 0L, MessageType.SYSTEM_MESSAGE, "用户[" + msg.getMessage() + "]上线了, 快去搞他😍");

                    // 服务端主动更新每个客户端的用户列表
                    refreshClientOnlineUser(v, k);
                }
        );
    }

    /**
     * 处理查询在线请求
     *
     * @param ctx 连接
     * @param msg 客户端发来的消息
     */
    private void handlerOnline(ChannelHandlerContext ctx, ImMessage msg) {
        refreshClientOnlineUser(ctx, msg.getSender());
    }

    /**
     * 服务端主动刷新在线用户的用户列表
     *
     * @param ctx      连接
     * @param receiver 接收方
     */
    private void refreshClientOnlineUser(ChannelHandlerContext ctx, String receiver) {
        final List<ImUser> userList = ServerContext.getOnlineUsers().entrySet().stream().map(entry -> ImUser.builder()
                .uid(entry.getKey()).userName(entry.getValue()).build()
        ).collect(Collectors.toList());
        final ImMessage message = ImMessage.builder()
                .sender(ImConstants.defaultServerUid)
                .receiver(receiver)
                .messageType(MessageType.ONLINE)
                .message(userList)
                .createTime(DateUtil.date())
                .build();
        ctx.writeAndFlush(message);
    }

    /**
     * 处理发送文本消息请求
     *
     * @param ctx 发送方连接
     * @param msg 客户端发来的消息
     */
    private void handlerTextMessage(ChannelHandlerContext ctx, ImMessage msg) {
        // 检查接收方uid
        if (StrUtil.isBlank(msg.getReceiver())) {
            ImUtil.sendErrorTextMessage(ctx, msg.getSender(), "消息格式错误,对方uid不存在");
            return;
        }

        // 检查接收方连接
        final ChannelHandlerContext receiverChannel = ServerContext.getOnlineChannels().get(msg.getReceiver());
        if (null == receiverChannel) {
            ImUtil.sendErrorTextMessage(ctx, msg.getSender(), "消息格式错误,对方已下线");
            return;
        }

        // 获取最新的用户名
        msg.setCode(0L);
        msg.setSenderName(ServerContext.getOnlineUsers().get(msg.getSender()));

        // 发送消息
        receiverChannel.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}