package com.geekbrains.student.cloud.storage.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

import static java.lang.Thread.sleep;

public class Controller implements Initializable {

    @FXML
    ListView<String> ClientFilesName, ClientFilesSize, ServerFilesName, ServerFilesSize;

    private static String[] sizes = {"B", "KB", "MB", "GB", "TB", "PB"};

    private String normilizeSize(Long length) {
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
        try {
            ClientFilesName.getItems().clear();
            ClientFilesSize.getItems().clear();
            ServerFilesName.getItems().clear();
            ServerFilesName.getItems().clear();
            Files.list(Paths.get("ClientsFiles")).map(p -> p.getFileName().toString()).forEach(o -> ClientFilesName.getItems().add(o));
            Files.list(Paths.get("ClientsFiles")).map(p -> p.toFile()).forEach(o -> ClientFilesSize.getItems().add(normilizeSize(o.length())));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getServerFilesList() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CountDownLatch networkStarter = new CountDownLatch(1);
        new Thread(() -> Network.getInstance().start(networkStarter)).start();
        try {
            networkStarter.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
