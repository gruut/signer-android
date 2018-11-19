package com.gruutnetworks.gruutsigner.model;

import com.google.gson.annotations.SerializedName;

/**
 * Title: Success in Key Exchange
 * Description: Success in Diff-Hellman Key Exchange
 * Message Type: 0x58
 */
public class MessageSuccess {
    @SerializedName("sender")
    private String sender;
    @SerializedName("time")
    private String time;
    @SerializedName("val")
    private boolean val;
}
