package com.geekbrains.student.cloud.storage.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Vector;



public class Controller {
    private Vector<File> fileList = new Vector<>();
    String[] sizes = {"B","KB","MB","GB","TB","PB"};

    @FXML
    ListView clientsFilesName, clientsFilesSize;

    public void setDragMove(MouseEvent mouseEvent) {
        System.out.println("DRAG DETECTED!!!!");
    }


    public void addFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(new Stage());
        fileList.add(file);
        updateFileListView();
    }

    private void updateFileListView() {
        clientsFilesSize.getItems().clear();
        clientsFilesName.getItems().clear();
        for (File o : fileList) {
            clientsFilesName.getItems().add(o.getName());
            clientsFilesSize.getItems().add(normilizeSize(o.length())
            );
        }
    }

    private String normilizeSize(Long length) {
        byte stage = 0;
        Float sizeOfFile = (float)length;
        while (sizeOfFile >= 1024 && stage < (sizes.length-1)) {
            sizeOfFile /= 1024;
            stage++;
        }
        String str = sizeOfFile.toString() + " " + sizes[stage];
        return str;
    }

}
