package com.gruutnetworks.gruutsigner.model;

import com.google.gson.annotations.SerializedName;

/**
 * Title: Signer's Signature
 * Description: Signer's signature to Merger
 * Message Type: 0xB3
 */
public class MessageSignature {
    @SerializedName("sID")
    String sid;
    @SerializedName("time")
    String time;
    @SerializedName("sig")
    String signature;
}
