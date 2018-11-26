package com.gruutnetworks.gruutsigner.model;

import com.google.gson.annotations.SerializedName;

/**
 * Title: Approval for Joining
 * Description: Merger approves Signer's participation
 * Message Type: 0x59
 */
public class MessageAccept {
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
}
