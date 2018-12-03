package com.gruutnetworks.gruutsigner.model;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Title: Approval for Joining
 * Description: Merger approves Signer's participation
 * Message Type: 0x59
 */
public class UnpackMsgAccept extends MsgUnpacker {
    @Expose
    @SerializedName("sender")
    private String sender;
    @Expose
    @SerializedName("time")
    private String time;
    @Expose
    @SerializedName("val")
    private boolean val;

    public UnpackMsgAccept(byte[] bytes) {
        parse(bytes);
        bodyFromJson(body);
    }

    public boolean isVal() {
        return val;
    }

    @Override
    void bodyFromJson(byte[] bodyBytes) {
        // Super class는 제외하고 deserialize
        Gson gson = new GsonBuilder().addDeserializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass().equals(MsgUnpacker.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();

        UnpackMsgAccept msgAccept = gson.fromJson(new String(bodyBytes), UnpackMsgAccept.class);

        this.sender = msgAccept.sender;
        this.time = msgAccept.time;
        this.val = msgAccept.val;
    }
}
