package com.geekbrains.student.cloud.storage.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MyProtoSender {

    public static void sendFile(Path path, Channel channel, ChannelFutureListener finishlistener) throws IOException {

        ByteBuf buf = null;
    }

    public static void sendFile(Path path, Channel channel, ChannelFutureListener finishlistener, int partNum) throws IOException {

    }

    public static void sendCmd(String msg, Channel channel, ChannelFutureListener finishlistener) {
        ByteBuf buf = null;
        buf = ByteBufAllocator.DEFAULT.directBuffer(1);
        buf.writeByte(MyProtoClientHandler.START_BYTE);
        channel.writeAndFlush(buf);

        buf = ByteBufAllocator.DEFAULT.directBuffer(4);
        buf.writeInt(msg.length());
        channel.writeAndFlush(buf);

        buf = ByteBufAllocator.DEFAULT.directBuffer(msg.length());
        try {
            buf.writeBytes(msg.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        channel.writeAndFlush(buf);
    }


}
