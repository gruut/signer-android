package com.gruutnetworks.gruutsigner.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Title: Join
 * Description: Signer's network participation request
 * Message Type: 0x54
 */
public class MessageJoin {
    @SerializedName("sender")
    private String sender;
    @SerializedName("time")
    private String time;
    @SerializedName("ver")
    private String ver;
    @SerializedName("cID")
    private String localChainId;

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

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getLocalChainId() {
        return localChainId;
    }

    public void setLocalChainId(String localChainId) {
        this.localChainId = localChainId;
    }

    public byte[] getJson() {
        Gson gson = new Gson();
        return gson.toJson(this).getBytes();
    }
}
