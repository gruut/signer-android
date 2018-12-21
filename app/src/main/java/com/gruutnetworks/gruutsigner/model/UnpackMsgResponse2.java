package com.gruutnetworks.gruutsigner.model;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Title: Response2 to Challenge
 * Description: Signer's response to Response1 from Merger
 * Message Type: 0x57
 */
public class UnpackMsgResponse2 extends MsgUnpacker {
    @Expose
    @SerializedName("mID")
    private String mID;
    @Expose
    @SerializedName("time")
    private String time;
    @Expose
    @SerializedName("cert")
    private String cert;
    @Expose
    @SerializedName("dhx")
    private String dhPubKeyX;
    @Expose
    @SerializedName("dhy")
    private String dhPubKeyY;
    @Expose
    @SerializedName("sig")
    private String sig; // signature with Signer's nonce, Merger's nonce, dh2, time

    public UnpackMsgResponse2(byte[] bytes) {
        parse(bytes); // parse the whole message
        bodyFromJson(body);
    }

    public String getmID() {
        return mID;
    }

    public String getTime() {
        return time;
    }

    public String getCert() {
        return cert;
    }

    public String getDhPubKeyX() {
        return dhPubKeyX;
    }

    public String getDhPubKeyY() {
        return dhPubKeyY;
    }

    public String getSig() {
        return sig;
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

        UnpackMsgResponse2 msgResponse2 = gson.fromJson(new String(bodyBytes) , UnpackMsgResponse2.class);

        this.mID = msgResponse2.mID;
        this.time = msgResponse2.time;
        this.cert = msgResponse2.cert;
        this.dhPubKeyX = msgResponse2.dhPubKeyX;
        this.dhPubKeyY = msgResponse2.dhPubKeyY;
        this.sig = msgResponse2.sig;
    }
}
