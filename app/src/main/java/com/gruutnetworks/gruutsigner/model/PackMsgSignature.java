package com.gruutnetworks.gruutsigner.model;

import android.util.Base64;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static com.gruutnetworks.gruutsigner.model.MsgHeader.MSG_HEADER_LEN;

/**
 * Title: Signer's Signature
 * Description: Signer's signature to Merger
 * Message Type: 0xB3
 */
public class PackMsgSignature extends MsgPacker {
    @Expose(serialize = false)
    private String headerLocalChainId;

    @Expose
    @SerializedName("sID")
    private String sid;
    @Expose
    @SerializedName("time")
    private String time;
    @Expose
    @SerializedName("sig")
    private String sig;

    public PackMsgSignature(String sid, String time, String sig) {
        this.sid = sid;
        this.time = time;
        this.sig = sig;

        setHeader();
    }

    public PackMsgSignature(String headerLocalChainId, String sid, String time, String sig) {
        this.headerLocalChainId = headerLocalChainId;
        this.sid = sid;
        this.time = time;
        this.sig = sig;

        setHeader();
    }

    @Override
    void setHeader() {
        if (headerLocalChainId != null) {
            this.header = new MsgHeader.Builder()
                    .setMsgType(TypeMsg.MSG_SSIG.getType())
                    .setCompressionType(TypeComp.LZ4.getType())
                    .setTotalLen(MSG_HEADER_LEN + getCompressedJsonLen())
                    .setSender(Base64.decode(sid, Base64.NO_WRAP)) // Base64 decoding
                    .setLocalChainId(Base64.decode(headerLocalChainId, Base64.NO_WRAP))
                    .build();
        } else {
            this.header = new MsgHeader.Builder()
                    .setMsgType(TypeMsg.MSG_SSIG.getType())
                    .setCompressionType(TypeComp.LZ4.getType())
                    .setTotalLen(MSG_HEADER_LEN + getCompressedJsonLen())
                    .setSender(Base64.decode(sid, Base64.NO_WRAP)) // Base64 decoding
                    .build();
        }
    }

    @Override
    byte[] bodyToJson() {
        Gson gson = new Gson();
        return gson.toJson(PackMsgSignature.this).getBytes();
    }
}