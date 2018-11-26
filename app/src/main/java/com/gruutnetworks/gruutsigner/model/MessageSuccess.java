package com.gruutnetworks.gruutsigner.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Title: Success in Key Exchange
 * Description: Success in Diff-Hellman Key Exchange
 * Message Type: 0x58
 */
public class MessageSuccess {
    @SerializedName("sender")
    private String sender;
    @SerializedName("time")
    private String time;
    @SerializedName("val")
    private boolean val;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isVal() {
        return val;
    }

    public void setVal(boolean val) {
        this.val = val;
    }

    public byte[] getJson() {
        Gson gson = new Gson();
        return gson.toJson(this).getBytes();
    }
}
