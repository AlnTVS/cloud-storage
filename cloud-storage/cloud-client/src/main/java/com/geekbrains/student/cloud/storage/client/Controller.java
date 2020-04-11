package com.geekbrains.student.cloud.storage.client;

import com.geekbrains.student.cloud.storage.common.MyProtoClientHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import static java.lang.Thread.sleep;

public class Controller implements Initializable {

    @FXML
    ListView<String> ClientFilesName, ClientFilesSize, ServerFilesName, ServerFilesSize;

    private static boolean authenticated = false;

    private static String[] sizes = {"B", "KB", "MB", "GB", "TB", "PB"};

    private static String normilizeSize(Long length) {
        byte stage = 0;
        Float sizeOfFile = (float) length;
        while (sizeOfFile >= 1024 && stage < (sizes.length - 1)) {
            sizeOfFile /= 1024;
            stage++;
        }
        sizeOfFile -= (sizeOfFile % 0.01f);
        String str = sizeOfFile.toString() + " " + sizes[stage];
        return str;
    }


    @FXML
    private void refreshFileList() {
        if (authenticated) {
            try {
                ClientFilesName.getItems().clear();
                ClientFilesSize.getItems().clear();
                Files.list(Paths.get("ClientsFiles")).map(p -> p.getFileName().toString()).forEach(o -> ClientFilesName.getItems().add(o));
                Files.list(Paths.get("ClientsFiles")).map(p -> p.toFile()).forEach(o -> ClientFilesSize.getItems().add(normilizeSize(o.length())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            CountDownLatch networkStarter = new CountDownLatch(1);
            new Thread(() -> Network.getInstance().start(networkStarter)).start();
            networkStarter.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        linkCallback();
    }


    @FXML
    private void authOnServer(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/authScene.fxml"));
            Parent root = loader.load();
            stage.setTitle("Autorization");
            stage.setScene(new Scene(root, 400, 250));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void linkCallback() {
        MyProtoClientHandler.setCallSendAuthReplay(o -> {
            String tokens = o[0].toString();
            System.out.println(tokens);
            if (tokens.equals("1")) {
                Controller.setAuthenticated(true);
            }
        });
    }

    public static void setAuthenticated(boolean authenticated) {
        Controller.authenticated = authenticated;
    }
}
