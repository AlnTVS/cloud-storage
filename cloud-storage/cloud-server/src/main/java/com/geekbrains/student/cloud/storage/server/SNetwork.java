package com.geekbrains.student.cloud.storage.server;

import com.geekbrains.student.cloud.storage.common.Callback;
import com.geekbrains.student.cloud.storage.common.MyProtoClientHandler;
import com.geekbrains.student.cloud.storage.common.MyProtoSender;
import io.netty.channel.Channel;


public class SNetwork {
    private AuthService authService;
    private Channel ch;
    private String nickname = null;
    private final String authOk = MyProtoClientHandler.cmdProto.SEND_AUTH_REPLAY.getCmdType() + "1";
    private final String authNOk = MyProtoClientHandler.cmdProto.SEND_AUTH_REPLAY.getCmdType() + "0";

    public SNetwork(AuthService authService, Channel ch) {
        this.authService = authService;
        this.ch = ch;
    }

    public void setChannel(Channel ch) {
        this.ch = ch;
    }

    public void linkCallback() {
        MyProtoClientHandler.setCallRequestAuth(o -> {
            String[] tokens = o[0].toString().split("\\s", 2);
            nickname = authService.getNicknameByLoginAndPassword(tokens[0], tokens[1]);
            if (nickname != null) {
                System.out.println(nickname);
                MyProtoSender.sendCmd(authOk, ch, future -> {
                    if (!future.isSuccess()) {
                        future.cause().printStackTrace();
                    }
                    if (future.isSuccess()) {
                        System.out.println("Подтверждения авторизации отправлено!");
                    }
                });
            } else {
                MyProtoSender.sendCmd(authNOk, ch, future -> {
                    if (!future.isSuccess()) {
                        future.cause().printStackTrace();
                    }
                    if (future.isSuccess()) {
                        System.out.println("Отказ в авторизации отправлен!");
                    }
                });
            }
        });
    }
}

