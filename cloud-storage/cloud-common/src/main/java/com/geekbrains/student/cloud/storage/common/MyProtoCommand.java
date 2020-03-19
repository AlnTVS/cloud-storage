package com.geekbrains.student.cloud.storage.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;

public class MyProtoCommand extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte typePocket = buf.readByte();
        byte[] bytes = new byte[buf.readableBytes()];
        int i = 0;
        while ( buf.readableBytes() > 0) {
            bytes[i++] = buf.readByte();
        }
        buf.release();
        switch (typePocket){
            case((byte)'C'): // комманда
                commandHandler(bytes);
                break;
            case((byte)'I'): // информация для начала загрузки файла
                break;
            case((byte)'D'): // непосредствено скачивание файла
                break;
        }
    }

    private void commandHandler(byte[] bytes) throws UnsupportedEncodingException {
        String str = new String(bytes,"UTF-8");
            System.out.println(str);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
