package com.serical.client.gui;

import cn.hutool.core.util.StrUtil;
import com.serical.client.service.ClientService;
import com.serical.util.FxUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class LoginController {

    @FXML
    private TextField textField;

    @FXML
    private void switchToSecondary() throws IOException, InterruptedException, NoSuchAlgorithmException {
        final String userName = textField.getText();
        if (StrUtil.isBlank(userName)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            FxUtil.alert(Alert.AlertType.ERROR, "用户名倒是填一个啊");
            alert.showAndWait();
            return;
        }

        App.setRoot("secondary");

        // 初始化用户
        ClientService.initImUser(userName);
    }

    /**
     * 输入用户名登录
     *
     * @param keyEvent 事件
     */
    public void login(KeyEvent keyEvent) throws InterruptedException, NoSuchAlgorithmException, IOException {
        if (KeyCode.ENTER == keyEvent.getCode()) {
            switchToSecondary();
        }
    }
}
