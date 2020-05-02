package com.serical.client.im;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.serical.client.gui.App;
import com.serical.common.ImConstants;
import com.serical.common.ImMessage;
import com.serical.common.ImUser;
import com.serical.common.MessageType;
import com.serical.util.FxUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SecureChatClientHandler extends SimpleChannelInboundHandler<ImMessage> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ImMessage msg) throws Exception {
        System.err.println(msg);

        // 与服务端建立连接后发送注册请求
        if (msg.getMessageType() == MessageType.CONNECT) {
            // 发送注册与获取在线用户消息
            sendRegisterMessage(ctx);
            sendOnlineMessage(ctx);
        } else if (msg.getMessageType() == MessageType.ONLINE) {
            handlerOnline(msg);
        } else if (msg.getMessageType() == MessageType.TEXT_MESSAGE) {
            handlerTextMessage(msg);
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
            App.getMessageController().refreshUserList(imUsers);
        });
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
}