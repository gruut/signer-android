package com.gruutnetworks.gruutsigner.model;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Title: Partial Block
 * Description: Temporary block for Merger to request Signer to sign
 * Message Type: 0xB2
 */
public class UnpackMsgRequestSignature extends MsgUnpacker {
    @Expose
    @SerializedName("time")
    private String time;
    @Expose
    @SerializedName("mID")
    private String mID;
    @Expose
    @SerializedName("cID")
    private String chainId;
    @Expose
    @SerializedName("hgt")
    private String blockHeight;
    @Expose
    @SerializedName("txrt")
    private String transaction;

    public UnpackMsgRequestSignature(byte[] bytes) {
        parse(bytes); // parse the whole message
        bodyFromJson(body);
        setSenderValidity();
    }

    public String getTime() {
        return time;
    }

    public String getmID() {
        return mID;
    }

    public String getChainId() {
        return chainId;
    }

    public String getBlockHeight() {
        return blockHeight;
    }

    public String getTransaction() {
        return transaction;
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

        UnpackMsgRequestSignature msgRequestSignature = gson.fromJson(new String(bodyBytes), UnpackMsgRequestSignature.class);

        this.time = msgRequestSignature.time;
        this.mID = msgRequestSignature.mID;
        this.chainId = msgRequestSignature.chainId;
        this.blockHeight = msgRequestSignature.blockHeight;
        this.transaction = msgRequestSignature.transaction;
    }

    @Override
    void setSenderValidity() {
        this.senderValidity = header.getSender().equals(mID);
    }
}
