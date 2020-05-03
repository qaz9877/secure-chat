package com.serical.client.service;

import cn.hutool.core.util.IdUtil;
import com.serical.client.im.ClientContext;
import com.serical.client.im.SecureChatClientInitializer;
import com.serical.common.ImUser;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * 客户端服务
 *
 * @author serical 2020-05-01 17:57:21
 */
public class ClientService {

    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8992"));

    /**
     * 初始化登录用户
     *
     * @param userName 用户名
     */
    public static void initImUser(String userName) throws NoSuchAlgorithmException, SSLException, InterruptedException {
        final String uid = IdUtil.fastSimpleUUID();
        final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        final KeyPair keyPair = generator.genKeyPair();

        // 初始化用户
        final ImUser user = ImUser.builder()
                .uid(uid)
                .userName(userName)
                .publicKey(keyPair.getPublic())
                .privateKey(keyPair.getPrivate())
                .build();

        // 保存用户信息到上下文中
        ClientContext.setCurrentUser(user);

        // 初始化连接
        initChannel(user);
    }

    /**
     * 初始化管道
     *
     * @param user 当前登录用户
     */
    private static void initChannel(ImUser user) throws InterruptedException, SSLException {
        // Configure SSL.
        final SslContext sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new SecureChatClientInitializer(sslCtx));

        // Start the connection attempt.
        final ChannelFuture channelFuture = b.connect(HOST, PORT).sync();
        // channelFuture.channel().closeFuture().sync();

        user.setChannelFuture(channelFuture);
    }
}
