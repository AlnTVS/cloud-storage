package com.geekbrains.student.cloud.storage.client;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class AuthSceneController implements Initializable {

    @FXML
    VBox authScene;

    @FXML
    TextField login;

    @FXML
    PasswordField password;

    @FXML
    HBox mainHBox;

    public void authRequest(ActionEvent actionEvent) {
        Network.getInstance().sendAuth(login.getText(), password.getText());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
