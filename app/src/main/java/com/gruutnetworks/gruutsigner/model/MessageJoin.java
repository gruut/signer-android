package com.gruutnetworks.gruutsigner.model;

import com.google.gson.annotations.SerializedName;

/**
 * Title: Join
 * Description: Signer's network participation request
 * Message Type: 0x54
 */
public class MessageJoin {
    @SerializedName("sender")
    String sender;
    @SerializedName("time")
    String time;
    @SerializedName("sN")
    String signerNonce;

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

    public String getSignerNonce() {
        return signerNonce;
    }

    public void setSignerNonce(String signerNonce) {
        this.signerNonce = signerNonce;
    }
}
