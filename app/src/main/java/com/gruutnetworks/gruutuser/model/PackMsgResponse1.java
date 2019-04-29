package com.gruutnetworks.gruutuser.model;

import com.gruutnetworks.gruutuser.util.Base58;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static com.gruutnetworks.gruutuser.model.MsgHeader.MSG_HEADER_LEN;

/**
 * Title: Response1 to Challenge
 * Description: Merger's response to identity verification request from Signer
 * Message Type: 0x56
 */

public class PackMsgResponse1 extends MsgPacker {
    @Expose(serialize = false)
    private String headerLocalChainId;

    class DH {
        @Expose
        @SerializedName("x")
        public String x;   // HEX
        @Expose
        @SerializedName("y")
        public String y;   // HEX

        DH(String x , String y){
            this.x = x;
            this.y = y;
        }
    }

    class User {
        @Expose
        @SerializedName("id")
        public String id;

        @Expose
        @SerializedName("pk")
        public String pk;

        @Expose
        @SerializedName("sig")
        public String sig; // signature with signer's nonce, merger's nonce, dhx, dhy, time

        User(String id, String pk, String sig){
            this.id = id;
            this.pk = pk;
            this.sig = sig;
        }
    }
    @Expose
    @SerializedName("time")
    private String time;    // UNIX timestamp

    @Expose
    @SerializedName("sn")
    private String signerNonce; // 256bit random nonce

    @Expose
    @SerializedName("dh")
    private DH dh;

    @Expose
    @SerializedName("user")
    private User user;

    public PackMsgResponse1(String sID, String time, String cert, String signerNonce, String dhPubKeyX, String dhPubKeyY, String sig) {
        this.time = time;
        this.signerNonce = signerNonce;
        this.dh = new DH(dhPubKeyX, dhPubKeyY);
        this.user = new User(sID, cert, sig);

        setHeader();
    }

    public PackMsgResponse1(String headerLocalChainId, String sID, String time, String cert, String signerNonce, String dhPubKeyX, String dhPubKeyY, String sig) {
        this.headerLocalChainId = headerLocalChainId;
        this.time = time;
        this.signerNonce = signerNonce;
        this.dh = new DH(dhPubKeyX, dhPubKeyY);
        this.user = new User(sID, cert, sig);

        setHeader();
    }

    @Override
    void setHeader() {
        if (headerLocalChainId != null) {
            this.header = new MsgHeader.Builder()
                    .setMsgType(TypeMsg.MSG_RESPONSE_1.getType())
                    .setSerializationType(TypeComp.LZ4.getType())
                    .setTotalLen(MSG_HEADER_LEN + getCompressedJsonLen())
                    .setSender(Base58.decode(user.id)) // TODO: need to decode Base58
                    .setLocalChainId(headerLocalChainId.getBytes())
                    .build();
        } else {
            this.header = new MsgHeader.Builder()
                    .setMsgType(TypeMsg.MSG_RESPONSE_1.getType())
                    .setSerializationType(TypeComp.LZ4.getType())
                    .setTotalLen(MSG_HEADER_LEN + getCompressedJsonLen())
                    .setSender(Base58.decode(user.id)) // TODO: need to decode Base58
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
        return gson.toJson(PackMsgResponse1.this).getBytes();
    }
}
