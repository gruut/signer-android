package com.gruutnetworks.gruutsigner.gruut;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UnpackMsgAccept extends MsgUnpacker {
    @Expose
    @SerializedName("sender")
    private String sender;
    @Expose
    @SerializedName("time")
    private String time;
    @Expose
    @SerializedName("val")
    private boolean val;

    public UnpackMsgAccept(byte[] bytes) {
        parse(bytes);
        bodyFromJson(body);
    }

    public boolean isVal() {
        return val;
    }

    @Override
    void bodyFromJson(byte[] bodyBytes) {
        Gson gson = new Gson();
        UnpackMsgAccept msgAccept = gson.fromJson(new String(bodyBytes), UnpackMsgAccept.class);

        this.sender = msgAccept.sender;
        this.time = msgAccept.time;
        this.val = msgAccept.val;
    }
}
