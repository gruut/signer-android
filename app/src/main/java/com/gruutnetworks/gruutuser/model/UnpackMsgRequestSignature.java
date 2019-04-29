package com.gruutnetworks.gruutuser.model;

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
    class Block {
        @Expose
        @SerializedName("id")
        public String id;
        @Expose
        @SerializedName("time")
        public String time;
        @Expose
        @SerializedName("world")
        public String world;
        @Expose
        @SerializedName("chain")
        public String chain;
        @Expose
        @SerializedName("height")
        public String height;
        @Expose
        @SerializedName("pid")
        public String pid;
        @Expose
        @SerializedName("txroot")
        public String txroot;
        @Expose
        @SerializedName("usroot")
        public String usroot;
        @Expose
        @SerializedName("csroot")
        public String csroot;
    }

    class Producer {
        @Expose
        @SerializedName("id")
        public String id;
        @Expose
        @SerializedName("sig")
        public String sig;
    }

    @Expose
    @SerializedName("block")
    private Block block;
    @Expose
    @SerializedName("producer")
    private Producer producer;

    public UnpackMsgRequestSignature(byte[] bytes) {
        parse(bytes); // parse the whole message
        bodyFromJson(body);
        setSenderValidity();
    }

    public String getTime() {
        return block.time;
    }

    public String getmID() {
        return producer.id;
    }

    public String getChainId() {
        return block.chain;
    }

    public String getBlockHeight() {
        return block.height;
    }

    public String getBlockId() { return block.id; }
    public String getTxRoot() { return block.txroot;}
    public String getUsRoot() { return block.usroot;}
    public String getCsRoot() { return block.csroot;}

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

        this.block = msgRequestSignature.block;
        this.producer = msgRequestSignature.producer;
    }

    @Override
    void setSenderValidity() {
        this.senderValidity = header.getSender().equals(producer.id);
    }
}
