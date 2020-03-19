package com.geekbrains.student.cloud.storage.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

public class AuthBlock extends ChannelInboundHandlerAdapter implements AuthService {
    private boolean auth = false;
    private String nickname;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        // A - команда на авторизацию
        if (buf.readableBytes() > 0) {
            if (auth) {
                ctx.fireChannelRead(buf);
            } else if (buf.readByte() == (byte) 'A') { // заглушка
                System.out.println("Auth ok");
                auth = true;
                ctx.writeAndFlush("Registration successful!".getBytes("UTF-8"));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        return null;
    }
}
