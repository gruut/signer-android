package com.gruutnetworks.gruutsigner.model;

import com.google.gson.annotations.SerializedName;

/**
 * Title: Response2 to Challenge
 * Description: Signer's response to Response1 from Merger
 * Message Type: 0x57
 */
public class MessageResponse2 {
    @SerializedName("sender")
    private String sender;
    @SerializedName("time")
    private String time;
    @SerializedName("cert")
    private String cert;
    @SerializedName("dhpk")
    private String dhPubKey;
    @SerializedName("sig")
    private String sig; // signature with Signer's nonce, Merger's nonce, dh2, time

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

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }

    public String getDhPubKey() {
        return dhPubKey;
    }

    public void setDhPubKey(String dhPubKey) {
        this.dhPubKey = dhPubKey;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }
}
