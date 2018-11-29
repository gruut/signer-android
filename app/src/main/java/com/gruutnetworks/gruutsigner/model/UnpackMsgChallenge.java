package com.gruutnetworks.gruutsigner.model;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Title: Challenge for Join
 * Description: Requesting identity verification from Merger to Signer
 * Message Type: 0x55
 */
public class UnpackMsgChallenge extends MsgUnpacker {
    @Expose
    @SerializedName("sender")
    private String sender;
    @Expose
    @SerializedName("time")
    private String time;
    @Expose
    @SerializedName("mN")
    private String mergerNonce;

    public UnpackMsgChallenge(byte[] bytes) {
        parse(bytes); // parse the whole message
        bodyFromJson(body);
    }

    public String getMergerNonce() {
        return mergerNonce;
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

        UnpackMsgChallenge msgChallenge = gson.fromJson(new String(bodyBytes), UnpackMsgChallenge.class);

        this.sender = msgChallenge.sender;
        this.time = msgChallenge.time;
        this.mergerNonce = msgChallenge.mergerNonce;
    }
}
