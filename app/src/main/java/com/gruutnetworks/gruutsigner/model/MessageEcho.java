package com.gruutnetworks.gruutsigner.model;

import com.google.gson.annotations.SerializedName;

/**
 * Title: Echo
 * Description: Echo Send to Maintain Network Between Nodes
 * Message Type: 0x5A
 */
public class MessageEcho {
    @SerializedName("sender")
    private String sender;
    @SerializedName("time")
    private String time;

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
}
