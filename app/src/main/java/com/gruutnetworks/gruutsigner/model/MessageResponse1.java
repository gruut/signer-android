package com.gruutnetworks.gruutsigner.model;

import com.google.gson.annotations.SerializedName;

/**
 * Title: Response to Challenge
 * Description: Merger's response to identity verification request from Signer
 * Message Type: 0x56
 */
public class MessageResponse1 {
    @SerializedName("sender")
    private String sender;
    @SerializedName("time")
    private String time;
    @SerializedName("sig1")
    private String sig;
    @SerializedName("Ns")
    private String nonceSigner;
    @SerializedName("dh1")
    private String dh1;

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

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public String getNonceSigner() {
        return nonceSigner;
    }

    public void setNonceSigner(String nonceSigner) {
        this.nonceSigner = nonceSigner;
    }

    public String getDh1() {
        return dh1;
    }

    public void setDh1(String dh1) {
        this.dh1 = dh1;
    }
}
