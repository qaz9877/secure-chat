package com.serical.client.gui;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.serical.client.im.ClientContext;
import com.serical.common.ImConstants;
import com.serical.common.ImMessage;
import com.serical.common.ImUser;
import com.serical.common.MessageType;
import com.serical.util.FxUtil;
import com.serical.util.ImUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MessageController {

    private ImUser selectUser;

    @FXML
    private ListView<String> userListView;

    @FXML
    private TextArea textArea;

    @FXML
    private VBox showMessage;

    @FXML
    private ScrollPane scrollMessage;

    @FXML
    private Label chooseWho;

    @FXML
    private Label systemMessage;

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

    /**
     * 刷新用户列表
     *
     * @param userList 在线用户
     */
    public void refreshUserList(List<ImUser> userList) {
        userListView.getItems().clear();
        userList.forEach(user -> Optional.ofNullable(user.getUserName())
                .filter(StrUtil::isNotBlank)
                .ifPresent(v -> userListView.getItems().add(v)));
        userListView.setOnMouseClicked(event -> {
            final int index = userListView.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                this.selectUser = userList.get(index);
                chooseWho.setText("您[" + ClientContext.getCurrentUser().getUserName() + "] 正在与 ["
                        + this.selectUser.getUserName() + "] 对线中。。。。。。");

                // 请求对方公钥
                final ImUser currentUser = ClientContext.getCurrentUser();
                final ImMessage imMessage = ImMessage.builder()
                        .sender(currentUser.getUid())
                        .receiver(selectUser.getUid())
                        .messageType(MessageType.REQUEST_PUBLIC_KEY)
                        .createTime(DateUtil.date())
                        .build();
                // 发送消息
                ImUtil.sendMessage(imMessage);
            }
        });
    }

    /**
     * 发送消息
     *
     * @param keyEvent 事件
     */
    public void sendMessage(KeyEvent keyEvent) {
        if (KeyCode.ENTER == keyEvent.getCode()) {
            final String message = textArea.getText();
            if (StrUtil.isBlank(message)) {
                return;
            }

            if (null == selectUser || StrUtil.isBlank(selectUser.getUid())) {
                FxUtil.alert(Alert.AlertType.ERROR, "你他吗倒是选个人再聊天啊🙄");
                return;
            }

            // 组装消息体
            final ImUser currentUser = ClientContext.getCurrentUser();
            final ImMessage imMessage = ImMessage.builder()
                    .sender(currentUser.getUid())
                    .senderName(currentUser.getUserName())
                    .receiver(selectUser.getUid())
                    .messageType(MessageType.TEXT_MESSAGE)
                    .message(message)
                    .createTime(DateUtil.date())
                    .build();

            // append自己发的消息
            appendMessage(imMessage);

            // 发送消息
            ImUtil.sendMessage(imMessage);

            textArea.clear();
        }
    }

    /**
     * 追加消息
     *
     * @param message 消息
     */
    public void appendMessage(ImMessage message) {
        final String senderName = message.getSender().equals(ImConstants.defaultServerUid) ?
                ImConstants.systemName : message.getSenderName();
        showMessage.getChildren().add(new Label("[" + senderName + "] "
                + DateUtil.formatDateTime(message.getCreateTime())
                + " : " + message.getMessage()));
        scrollMessage.vvalueProperty().bind(showMessage.heightProperty());
    }

    /**
     * 显示系统消息
     *
     * @param message 消息
     */
    public void showSystemMessage(ImMessage message) {
        systemMessage.setText("[系统通知]" + message.getMessage() + "");
    }
}