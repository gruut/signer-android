package com.gruutnetworks.gruutsigner.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Title: Response1 to Challenge
 * Description: Merger's response to identity verification request from Signer
 * Message Type: 0x56
 */
public class MessageResponse1 {
    @SerializedName("sender")
    private String sender;
    @SerializedName("time")
    private String time;
    @SerializedName("cert")
    private String cert;
    @SerializedName("sN")
    private String signerNonce;
    @SerializedName("dhpk")
    private String dhPubKey;
    @SerializedName("sig")
    private String sig; // signature with signer's nonce, merger's nonce, dh1, time

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

    public String getSignerNonce() {
        return signerNonce;
    }

    public void setSignerNonce(String signerNonce) {
        this.signerNonce = signerNonce;
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

    public byte[] getJson() {
        Gson gson = new Gson();
        return gson.toJson(this).getBytes();
    }
}
