package com.geekbrains.student.cloud.storage.server;

import com.geekbrains.student.cloud.storage.common.Callback;
import com.geekbrains.student.cloud.storage.common.MyProtoClientHandler;

public class SNetwork {
    private MyProtoClientHandler myBlock;

    public SNetwork(MyProtoClientHandler myBlock) {
        this.myBlock = myBlock;
    }

    private void linkCallback() {
        myBlock.callRequestAuth = new Callback() {
            @Override
            public void сallback(Object... o) {
                String[] tokens = ((String)o[0]).split("\\s",2);

            }
        };
    }
}
