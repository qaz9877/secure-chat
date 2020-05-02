package com.serical.server;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端上下文
 *
 * @author serical 2020-05-01 14:07:14
 */
public class ServerContext {

    /**
     * 在线用户连接
     */
    private final static ConcurrentHashMap<String, ChannelHandlerContext> onlineChannels = new ConcurrentHashMap<>();

    /**
     * 在线用户信息
     */
    private final static ConcurrentHashMap<String, String> onlineUsers = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, ChannelHandlerContext> getOnlineChannels() {
        return onlineChannels;
    }

    public static ConcurrentHashMap<String, String> getOnlineUsers() {
        return onlineUsers;
    }
}
