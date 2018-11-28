package com.gruutnetworks.gruutsigner.model;

import android.util.Base64;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static com.gruutnetworks.gruutsigner.model.MsgHeader.MSG_HEADER_LEN;

/**
 * Title: Success in Key Exchange
 * Description: Success in Diff-Hellman Key Exchange
 * Message Type: 0x58
 */
public class PackMsgSuccess extends MsgPacker {
    @Expose(serialize = false)
    private String headerLocalChainId;

    @Expose
    @SerializedName("sender")
    private String sender;
    @Expose
    @SerializedName("time")
    private String time;
    @Expose
    @SerializedName("val")
    private boolean val;

    public PackMsgSuccess(String sender, String time, boolean val) {
        this.sender = sender;
        this.time = time;
        this.val = val;

        setHeader();
    }

    public PackMsgSuccess(String headerLocalChainId, String sender, String time, boolean val) {
        this.headerLocalChainId = headerLocalChainId;
        this.sender = sender;
        this.time = time;
        this.val = val;

        setHeader();
    }

    @Override
    void setHeader() {
        if (headerLocalChainId != null) {
            this.header = new MsgHeader.Builder()
                    .setMsgType(TypeMsg.MSG_SUCCESS.getType())
                    .setMacType(TypeMac.HMAC_SHA256.getType())
                    .setCompressionType(TypeComp.LZ4.getType())
                    .setTotalLen(MSG_HEADER_LEN + getCompressedJsonLen())
                    .setSender(Base64.decode(sender, Base64.NO_WRAP)) // Base64 decoding
                    .setLocalChainId(Base64.decode(headerLocalChainId, Base64.NO_WRAP))
                    .build();
        } else {
            this.header = new MsgHeader.Builder()
                    .setMsgType(TypeMsg.MSG_SUCCESS.getType())
                    .setMacType(TypeMac.HMAC_SHA256.getType())
                    .setCompressionType(TypeComp.LZ4.getType())
                    .setTotalLen(MSG_HEADER_LEN + getCompressedJsonLen())
                    .setSender(Base64.decode(sender, Base64.NO_WRAP)) // Base64 decoding
                    .build();
        }
    }

    @Override
    byte[] bodyToJson() {
        Gson gson = new Gson();
        return gson.toJson(PackMsgSuccess.this).getBytes();
    }
}
