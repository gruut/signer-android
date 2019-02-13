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
    @SerializedName("mID")
    private String mID;
    @Expose
    @SerializedName("time")
    private String time;
    @Expose
    @SerializedName("mN")
    private String mergerNonce;

    public UnpackMsgChallenge(byte[] bytes) {
        parse(bytes); // parse the whole message
        bodyFromJson(body);
        setSenderValidity();
    }

    public String getmID() {
        return mID;
    }

    public String getMergerNonce() {
        return mergerNonce;
    }

    public String getTime() {
        return time;
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

        this.mID = msgChallenge.mID;
        this.time = msgChallenge.time;
        this.mergerNonce = msgChallenge.mergerNonce;
    }

    @Override
    void setSenderValidity() {
        this.senderValidity = header.getSender().equals(mID);
    }
}
