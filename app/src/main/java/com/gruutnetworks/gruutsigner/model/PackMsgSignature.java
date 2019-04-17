package com.gruutnetworks.gruutsigner.model;

import com.gruutnetworks.gruutsigner.util.Base58;
import android.util.Base64;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static com.gruutnetworks.gruutsigner.model.MsgHeader.MSG_HEADER_LEN;

/**
 * Title: Signer's Signature
 * Description: Signer's signature to Merger
 * Message Type: 0xB3
 */

class Block {
    @Expose
    @SerializedName("id")
    public String id;

    Block(String block_id){
        this.id = block_id;
    }
}

class Signer {
    @Expose
    @SerializedName("id")
    public String id;

    @Expose
    @SerializedName("sig")
    public String sig;

    Signer(String id, String sig){
        this.id = id;
        this.sig = sig;
    }
}

public class PackMsgSignature extends MsgPacker {
    @Expose(serialize = false)
    private String headerLocalChainId;

    @Expose
    @SerializedName("block")
    private Block block;
    @Expose
    @SerializedName("signer")
    private Signer signer;

    public PackMsgSignature(String sid, String bid, String sig) {
        this.block = new Block(bid);
        this.signer = new Signer(sid, sig);

        setHeader();
    }

    public PackMsgSignature(String headerLocalChainId, String sid, String bid, String sig) {
        this.headerLocalChainId = headerLocalChainId;
        this.block = new Block(bid);
        this.signer = new Signer(sid, sig);

        setHeader();
    }

    @Override
    void setHeader() {
        if (headerLocalChainId != null) {
            this.header = new MsgHeader.Builder()
                    .setMsgType(TypeMsg.MSG_SSIG.getType())
                    .setMacType(TypeMac.HMAC_SHA256.getType())
                    .setSerializationType(TypeComp.LZ4.getType())
                    .setTotalLen(MSG_HEADER_LEN + getCompressedJsonLen())
                    .setSender(Base58.decode(signer.id))
                    .setLocalChainId(headerLocalChainId.getBytes())
                    .build();
        } else {
            this.header = new MsgHeader.Builder()
                    .setMsgType(TypeMsg.MSG_SSIG.getType())
                    .setMacType(TypeMac.HMAC_SHA256.getType())
                    .setSerializationType(TypeComp.LZ4.getType())
                    .setTotalLen(MSG_HEADER_LEN + getCompressedJsonLen())
                    .setSender(Base58.decode(signer.id))
                    .build();
        }
    }

    @Override
    public void setDestinationId(String id) {
        this.destinationId = id;
    }

    @Override
    byte[] bodyToJson() {
        // Super class는 제외하고 serialize
        Gson gson = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass().equals(MsgPacker.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();
        return gson.toJson(PackMsgSignature.this).getBytes();
    }
}
