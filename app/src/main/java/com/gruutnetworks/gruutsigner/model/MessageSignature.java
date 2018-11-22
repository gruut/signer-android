package com.gruutnetworks.gruutsigner.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Title: Signer's Signature
 * Description: Signer's signature to Merger
 * Message Type: 0xB3
 */
public class MessageSignature {
    @SerializedName("sID")
    private String sid;
    @SerializedName("time")
    private String time;
    @SerializedName("sig")
    private String signature;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public byte[] getJson() {
        Gson gson = new Gson();
        return gson.toJson(this).getBytes();
    }
}
