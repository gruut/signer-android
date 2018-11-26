package com.gruutnetworks.gruutsigner.model;

import com.google.gson.annotations.SerializedName;

/**
 * Title: Challenge for Join
 * Description: Requesting identity verification from Merger to Signer
 * Message Type: 0x55
 */
public class MessageChallenge {
    @SerializedName("sender")
    private String sender;
    @SerializedName("time")
    private String time;
    @SerializedName("mN")
    private String mergerNonce;

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

    public String getMergerNonce() {
        return mergerNonce;
    }

    public void setMergerNonce(String mergerNonce) {
        this.mergerNonce = mergerNonce;
    }
}
