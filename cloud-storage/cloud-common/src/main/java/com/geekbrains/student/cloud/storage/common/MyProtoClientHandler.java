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


    private static Callback callRequestFile;
    private static Callback callRequestFileList;
    private static Callback callRequestAuth;
    private static Callback callSendFileData;
    private static Callback callSendFileList;
    private static Callback callSendAuthReplay;

    static {
        Callback empty = args -> {
        };
        callRequestFile = empty;
        callRequestFileList = empty;
        callRequestAuth = empty;
        callSendFileData = empty;
        callSendFileList = empty;
        callSendAuthReplay = empty;
    }

    public static void setCallRequestFile(Callback callRequestFile) {
        MyProtoClientHandler.callRequestFile = callRequestFile;
    }
    public static void setCallRequestFileList(Callback callRequestFileList) {
        MyProtoClientHandler.callRequestFileList = callRequestFileList;
    }
    public static void setCallRequestAuth(Callback callRequestAuth) {
        MyProtoClientHandler.callRequestAuth = callRequestAuth;
    }
    public static void setCallSendFileData(Callback callSendFileData) {
        MyProtoClientHandler.callSendFileData = callSendFileData;
    }
    public static void setCallSendFileList(Callback callSendFileList) {
        MyProtoClientHandler.callSendFileList = callSendFileList;
    }
    public static void setCallSendAuthReplay(Callback callSendAuthReplay) {
        MyProtoClientHandler.callSendAuthReplay = callSendAuthReplay;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        while (buf.readableBytes() > 0) {
            if (state == State.IDLE) {
                if (START_BYTE == buf.readByte()) {
                    state = State.POCKET_SIZE;
                }
            } else if (state == State.POCKET_SIZE) {
                if (buf.readableBytes() >= 4) {
                    pocketSize = buf.readInt();
                    state = State.READ_CMD;
                }
            } else if (state == State.READ_CMD) {
                if (buf.readableBytes() >= 8) {
                    buf.readBytes(cmd);
                    strCmd = new String(cmd, "UTF-8");
                    state = State.READ_INFO;
                    pocketSize -= 8;
                }
            } else if (state == State.READ_INFO) {
                if (buf.readableBytes() >= 0 && readBytes < pocketSize) {
                    info.append(((char)buf.readByte()));
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
