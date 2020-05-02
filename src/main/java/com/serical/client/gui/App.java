package com.serical.client.gui;

import com.serical.common.ImConstants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    private static LoginController loginController;
    private static MessageController messageController;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"));
        stage.setTitle(ImConstants.systemName);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        final Window window = scene.getWindow();
        window.sizeToScene();
        window.centerOnScreen();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        final Parent load = fxmlLoader.load();
        final Object controller = fxmlLoader.getController();
        if (controller instanceof LoginController) {
            loginController = (LoginController) controller;
        }
        if (controller instanceof MessageController) {
            messageController = (MessageController) controller;
        }
        return load;
    }

    public static LoginController getLoginController() {
        return loginController;
    }

    public static MessageController getMessageController() {
        return messageController;
    }

    public static void main(String[] args) {
        launch();
    }
}