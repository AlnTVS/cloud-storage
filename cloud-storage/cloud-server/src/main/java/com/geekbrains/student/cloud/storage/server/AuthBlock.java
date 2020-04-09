package com.geekbrains.student.cloud.storage.server;

import io.netty.channel.ChannelInboundHandlerAdapter;

public class AuthBlock implements AuthService {
    private boolean auth = false;
    private String nickname;


    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        
    }
}
