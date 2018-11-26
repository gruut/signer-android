package com.gruutnetworks.gruutsigner.model;

import com.google.gson.annotations.SerializedName;

/**
 * Title: Partial Block
 * Description: Temporary block for Merger to request Signer to sign
 * Message Type: 0xB2
 */
public class MessageRequestSignature {
    @SerializedName("time")
    private String time;
    @SerializedName("mID")
    private String mID;
    @SerializedName("cID")
    private String chainId;
    @SerializedName("hgt")
    private String blockHeight;
    @SerializedName("txrt")
    private String transaction;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getmID() {
        return mID;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public String getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(String blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }
}
