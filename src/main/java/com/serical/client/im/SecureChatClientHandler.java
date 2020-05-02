package com.serical.client.im;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.serical.client.gui.App;
import com.serical.common.ImConstants;
import com.serical.common.ImMessage;
import com.serical.common.ImUser;
import com.serical.common.MessageType;
import com.serical.util.FxUtil;
import com.serical.util.ImUtil;
import com.serical.util.RSAUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;

public class SecureChatClientHandler extends SimpleChannelInboundHandler<ImMessage> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ImMessage msg) throws Exception {
        final ImUser currentUser = ClientContext.getCurrentUser();
        System.err.println(currentUser.getUid() + ":" + currentUser.getUserName() + " message: " + msg);

        // 与服务端建立连接后发送注册请求
        if (msg.getMessageType() == MessageType.CONNECT) {
            // 发送注册与获取在线用户消息
            sendRegisterMessage(ctx);
            sendOnlineMessage(ctx);
        } else if (msg.getMessageType() == MessageType.ONLINE) {
            handlerOnline(msg);
        } else if (msg.getMessageType() == MessageType.REQUEST_PUBLIC_KEY) {
            handlerRequestPublicKey(msg);
        } else if (msg.getMessageType() == MessageType.RESPONSE_PUBLIC_KEY) {
            handlerResponsePublicKey(msg);
        } else if (msg.getMessageType() == MessageType.TEXT_MESSAGE) {
            handlerTextMessage(msg);
        } else if (msg.getMessageType() == MessageType.SYSTEM_MESSAGE) {
            handlerSystemMessage(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 发送注册消息
     */
    private void sendRegisterMessage(ChannelHandlerContext ctx) {
        final ImUser currentUser = ClientContext.getCurrentUser();
        final ImMessage message = ImMessage.builder()
                .sender(currentUser.getUid())
                .receiver(ImConstants.defaultServerUid)
                .messageType(MessageType.REGISTER)
                .message(currentUser.getUserName())
                .createTime(DateUtil.date())
                .build();
        ctx.writeAndFlush(message);
    }

    /**
     * 发送获取在线用户消息
     */
    private void sendOnlineMessage(ChannelHandlerContext ctx) {
        final ImUser currentUser = ClientContext.getCurrentUser();
        final ImMessage message = ImMessage.builder()
                .sender(currentUser.getUid())
                .receiver(ImConstants.defaultServerUid)
                .messageType(MessageType.ONLINE)
                .createTime(DateUtil.date())
                .build();
        ctx.writeAndFlush(message);
    }


    /**
     * 显示在线用户列表
     *
     * @param msg 服务端返回的用户信息列表
     */
    @SuppressWarnings("unchecked")
    private void handlerOnline(ImMessage msg) {
        List<LinkedHashMap<String, String>> userList = (List<LinkedHashMap<String, String>>) msg.getMessage();
        if (CollUtil.isEmpty(userList)) {
            return;
        }

        // java.lang.IllegalStateException: Not on FX application thread;
        Platform.runLater(() -> {
            List<ImUser> imUsers = new ArrayList<>();
            userList.forEach(user -> imUsers.add(ImUser.builder().uid(user.get("uid")).userName(user.get("userName")).build()));
            App.getMessageController().refreshUserList(imUsers.stream()
                    .filter(v -> !v.getUid().equals(ClientContext.getCurrentUser().getUid()))
                    .collect(Collectors.toList())
            );
        });
    }

    /**
     * 发送自己的公钥给对方
     *
     * @param msg 消息
     */
    private void handlerRequestPublicKey(ImMessage msg) {
        // 请求对方公钥
        final ImUser currentUser = ClientContext.getCurrentUser();
        final ImMessage imMessage = ImMessage.builder()
                .sender(currentUser.getUid())
                .receiver(msg.getSender())
                .messageType(MessageType.RESPONSE_PUBLIC_KEY)
                .message(Base64.getEncoder().encodeToString(currentUser.getPublicKey().getEncoded()))
                .createTime(DateUtil.date())
                .build();
        // 发送消息
        ImUtil.sendMessage(imMessage);
    }

    /**
     * 保存对方发来的公钥
     *
     * @param msg 消息
     */
    private void handlerResponsePublicKey(ImMessage msg) {
        if (msg.getCode() == 0L) {
            Optional.ofNullable(msg.getMessage()).map(Object::toString).filter(StrUtil::isNotBlank).ifPresent(key -> {
                try {
                    final PublicKey publicKey = RSAUtil.getPublicKey(key);
                    ClientContext.getUserPublicKey().put(msg.getSender(), publicKey);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * 处理别人发过来的消息
     *
     * @param msg 消息
     */
    private void handlerTextMessage(ImMessage msg) {
        if (msg.getCode() == 0L) {
            Platform.runLater(() -> App.getMessageController().appendMessage(msg));
        } else {
            FxUtil.alert(Alert.AlertType.ERROR, msg.getMessage() + "");
        }
    }

    /**
     * 处理服务端发过来的系统通知消息
     *
     * @param msg 消息
     */
    private void handlerSystemMessage(ImMessage msg) {
        if (msg.getCode() == 0L) {
            Platform.runLater(() -> App.getMessageController().showSystemMessage(msg));
        }
    }
}