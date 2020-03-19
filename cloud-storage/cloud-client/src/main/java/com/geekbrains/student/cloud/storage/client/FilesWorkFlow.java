package com.geekbrains.student.cloud.storage.client;

import java.nio.file.*;
import java.util.Vector;

public class FilesWorkFlow {
    private Path path;
    private Files file;

    public FilesWorkFlow() {
        path = Paths.get("ClientsFiles");

    }
}
