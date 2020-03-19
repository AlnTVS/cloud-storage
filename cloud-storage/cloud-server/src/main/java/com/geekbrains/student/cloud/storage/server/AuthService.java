package com.geekbrains.student.cloud.storage.server;

public interface AuthService {
    String getNicknameByLoginAndPassword(String login, String password);
}
