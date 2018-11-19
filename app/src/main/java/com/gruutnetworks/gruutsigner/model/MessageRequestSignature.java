package com.gruutnetworks.gruutsigner.model;

import com.google.gson.annotations.SerializedName;

/**
 * Title: Partial Block
 * Description: Temporary block for Merger to request Signer to sign
 * Message Type: 0xB2
 */
public class MessageRequestSignature {
    @SerializedName("time")
    String time;
    @SerializedName("mID")
    String mID;
    @SerializedName("cID")
    String chainId;
    @SerializedName("hgt")
    String blockHeight;
    @SerializedName("txrt")
    String transaction;
}
