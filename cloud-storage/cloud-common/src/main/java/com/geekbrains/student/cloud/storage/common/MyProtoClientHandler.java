package com.geekbrains.student.cloud.storage.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.util.ArrayList;

public class MyProtoClientHandler extends ChannelInboundHandlerAdapter {
    private enum State {
        IDLE,
        READ_CMD,
        POCKET_SIZE,
        READ_INFO;

    }

    public enum cmdProto {
        // (byte - startbyte)S (cmd)REQFLIST
        // S REQFLIST
        // or
        // S FILE_SND 6 f.txt 7 ABCDEFG
        REQUEST_FILE("REQ_FILE"), // Запрос на скачивание файла (имя файла)
        SEND_FILE_DATA("FILE_SND"), // Передача одной из частей файла (имя, данные, //номер части, всего частей)
        REQUEST_FILES_LIST("REQFLIST"), // Запрос на список файлов
        SEND_FILE_LIST("FILELIST"), // Список
        REQUSET_AUTH("AUTH_REQ"), // Запрос авторизации
        SEND_AUTH_REPLAY("AUTH_REP");
        //REPLAY_AUTH("AUTH_REP"); // Подтверждение/отказ в авторизации

        private String cmdType;

        cmdProto(String cmdType) {
            this.cmdType = cmdType;
        }

        public String getCmdType() {
            return cmdType;
        }

    }

    public static final byte START_BYTE = (byte) 'S';
    public static final byte STOP_BYTE = (byte) 'E';
    private State state = State.IDLE;
    private int pocketSize = 0;
    private int readBytes = 0;
    StringBuilder info = new StringBuilder();
    private byte[] cmd = new byte[8];
    private String strCmd;

    public Callback callRequestFile;
    public Callback callRequestFileList;
    public Callback callRequestAuth;
    public Callback callSendFileData;
    public Callback callSendFileList;
    public Callback callSendAuthReplay;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        while (true) {
            if (state == State.IDLE) {
                if (START_BYTE == buf.readByte()) {
                    state = State.READ_CMD;
                }
            }
            if (state == State.READ_CMD) {
                if (buf.readableBytes() >= 8) {
                    buf.readBytes(cmd);
                    strCmd = cmd.toString();
                    state = State.POCKET_SIZE;
                }
            }
            if (state == State.POCKET_SIZE) {
                if (buf.readableBytes() >= 4) {
                    pocketSize = buf.readInt();
                    state = State.READ_CMD;
                }
            }
            if (state == State.READ_INFO) {
                if (buf.readableBytes() >= 0 && readBytes < pocketSize) {
                    info.append(buf.readByte());
                    readBytes++;
                }
                if (readBytes == pocketSize) {
                    if (strCmd.equals(cmdProto.REQUEST_FILE.getCmdType())) {
                        callRequestFile.сallback(info.toString());
                    } else if (strCmd.equals(cmdProto.REQUEST_FILES_LIST.getCmdType())) {
                        callRequestFileList.сallback(info.toString());
                    } else if (strCmd.equals(cmdProto.REQUSET_AUTH.getCmdType())) {
                        callRequestAuth.сallback(info.toString());
                    } else if (strCmd.equals(cmdProto.SEND_FILE_DATA.getCmdType())) {
                        callSendFileData.сallback(info.toString());
                    } else if (strCmd.equals(cmdProto.REQUEST_FILES_LIST.getCmdType())) {
                        callRequestFileList.сallback(info.toString());
                    } else if (strCmd.equals(cmdProto.SEND_AUTH_REPLAY.getCmdType())) {
                        callSendAuthReplay.сallback(info.toString());
                    } else if (strCmd.equals(cmdProto.SEND_FILE_LIST.getCmdType())) {
                        callSendFileList.сallback(info.toString());
                    }
                    buf.release();
                    readBytes = 0;
                    pocketSize = 0;
                    info.delete(0, Integer.MAX_VALUE);
                    state = State.IDLE;
                }
            }
        }
    }
}
