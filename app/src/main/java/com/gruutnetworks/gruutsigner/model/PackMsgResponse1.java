package com.gruutnetworks.gruutsigner.model;

import android.util.Base64;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static com.gruutnetworks.gruutsigner.model.MsgHeader.MSG_HEADER_LEN;

/**
 * Title: Response1 to Challenge
 * Description: Merger's response to identity verification request from Signer
 * Message Type: 0x56
 */
public class PackMsgResponse1 extends MsgPacker {
    @Expose(serialize = false)
    private String headerLocalChainId;

    @Expose
    @SerializedName("sID")
    private String sID;  // BASE64 encoded 8 byte data
    @Expose
    @SerializedName("time")
    private String time;    // UNIX timestamp
    @Expose
    @SerializedName("cert")
    private String cert;    // pem Certificate
    @Expose
    @SerializedName("sN")
    private String signerNonce; // 256bit random nonce
    @Expose
    @SerializedName("dhx")
    private String dhPubKeyX;   // HEX
    @Expose
    @SerializedName("dhy")
    private String dhPubKeyY;   // HEX
    @Expose
    @SerializedName("sig")
    private String sig; // signature with signer's nonce, merger's nonce, dhx, dhy, time

    public PackMsgResponse1(String sID, String time, String cert, String signerNonce, String dhPubKeyX, String dhPubKeyY, String sig) {
        this.sID = sID;
        this.time = time;
        this.cert = cert;
        this.signerNonce = signerNonce;
        this.dhPubKeyX = dhPubKeyX;
        this.dhPubKeyY = dhPubKeyY;
        this.sig = sig;

        setHeader();
    }

    public PackMsgResponse1(String headerLocalChainId, String sID, String time, String cert, String signerNonce, String dhPubKeyX, String dhPubKeyY, String sig) {
        this.headerLocalChainId = headerLocalChainId;
        this.sID = sID;
        this.time = time;
        this.cert = cert;
        this.signerNonce = signerNonce;
        this.dhPubKeyX = dhPubKeyX;
        this.dhPubKeyY = dhPubKeyY;
        this.sig = sig;

        setHeader();
    }

    @Override
    void setHeader() {
        if (headerLocalChainId != null) {
            this.header = new MsgHeader.Builder()
                    .setMsgType(TypeMsg.MSG_RESPONSE_1.getType())
                    .setCompressionType(TypeComp.LZ4.getType())
                    .setTotalLen(MSG_HEADER_LEN + getCompressedJsonLen())
                    .setSender(Base64.decode(sID, Base64.NO_WRAP)) // Base64 decoding
                    .setLocalChainId(Base64.decode(headerLocalChainId, Base64.NO_WRAP))
                    .build();
        } else {
            this.header = new MsgHeader.Builder()
                    .setMsgType(TypeMsg.MSG_RESPONSE_1.getType())
                    .setCompressionType(TypeComp.LZ4.getType())
                    .setTotalLen(MSG_HEADER_LEN + getCompressedJsonLen())
                    .setSender(Base64.decode(sID, Base64.NO_WRAP)) // Base64 decoding
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
