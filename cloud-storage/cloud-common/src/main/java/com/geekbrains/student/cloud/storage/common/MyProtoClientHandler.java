package com.geekbrains.student.cloud.storage.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MyProtoClientHandler extends ChannelInboundHandlerAdapter {
    private enum State {
        IDLE, POCKET_SIZE, READ_MSG
    }

    private State state = State.IDLE;
    private int pocketSize;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        while (buf.readableBytes() > 0) {
            // Проверяем стартовый бит
            if (state == State.IDLE) {
                if (buf.readableBytes() > 0) {
                    if (buf.readByte() == (byte) 'S') {
                        state = State.POCKET_SIZE;
                    } else {
                        System.out.println("Received msg is not correct. Start-bit is wrong!");
                        ctx.writeAndFlush("Received msg is not correct. Start-bit is wrong!".getBytes());
                    }
                }
            }
            // Получаем размер входящего сообщения
            if (state == State.POCKET_SIZE) {
                if (buf.readableBytes() >= 4) {
                    pocketSize = buf.readInt();
                    if(pocketSize == 0) {
                        ctx.writeAndFlush("NullSizePocket!".getBytes("UTF-8"));
                        state = State.IDLE;
                    } else {
                        pocketSize = 5; // заглушка на размер читаемых байт
                        state = State.READ_MSG;
                    }
                }
            }
            //  Отправляем содержимое сообщения дальше по конвееру в виде массива байтов
            if (state == State.READ_MSG) {
                if (buf.readableBytes() >= pocketSize) {
                    ctx.fireChannelRead(buf.readBytes(pocketSize));
                    state = State.IDLE;
                }
            }
        }
        if (buf.readableBytes() == 0) {
            buf.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
