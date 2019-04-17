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
    class DH {
        @Expose
        @SerializedName("x")
        public String x;
        @Expose
        @SerializedName("y")
        public String y;

        DH(String x, String y){
            this.x = x;
            this.y = y;
        }
    }

    class Merger {
        @Expose
        @SerializedName("id")
        public String id;
        @Expose
        @SerializedName("cert")
        public String cert;
        @Expose
        @SerializedName("sig")
        public String sig; // signature with Signer's nonce, Merger's nonce, dh2, time

        Merger(String id, String cert, String sig){
            this.id = id;
            this.cert = cert;
            this.sig = sig;
        }
    }
    @Expose
    @SerializedName("time")
    private String time;

    @Expose
    @SerializedName("dh")
    private DH dh;

    @Expose
    @SerializedName("merger")
    private Merger merger;

    public UnpackMsgResponse2(byte[] bytes) {
        parse(bytes); // parse the whole message
        bodyFromJson(body);
        setSenderValidity();
    }
    public String getTime() { return time; }

    public String getmID() {
        return merger.id;
    }

    public String getCert() {
        return merger.cert;
    }

    public String getDhPubKeyX() {
        return dh.x;
    }

    public String getDhPubKeyY() {
        return dh.y;
    }

    public String getSig() {
        return merger.sig;
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

        this.merger = msgResponse2.merger;
        this.dh = msgResponse2.dh;
    }

    @Override
    void setSenderValidity() {
        this.senderValidity = header.getSender().equals(merger.id);
    }
}
