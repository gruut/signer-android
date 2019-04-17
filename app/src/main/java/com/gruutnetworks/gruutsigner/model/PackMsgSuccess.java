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
 * Title: Success in Key Exchange
 * Description: Success in Diff-Hellman Key Exchange
 * Message Type: 0x58
 */
public class PackMsgSuccess extends MsgPacker {
    @Expose(serialize = false)
    private String headerLocalChainId;

    @Expose
    @SerializedName("user")
    private String userID;
    @Expose
    @SerializedName("time")
    private String time;
    @Expose
    @SerializedName("val")
    private boolean val;

    public PackMsgSuccess(String sID, String time, boolean val) {
        this.userID = sID;
        this.time = time;
        this.val = val;

        setHeader();
    }

    public PackMsgSuccess(String headerLocalChainId, String sID, String time, boolean val) {
        this.headerLocalChainId = headerLocalChainId;
        this.userID = sID;
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
                    .setSerializationType(TypeComp.LZ4.getType())
                    .setTotalLen(MSG_HEADER_LEN + getCompressedJsonLen())
                    .setSender(Base58.decode(userID))
                    .setLocalChainId(Base64.decode(headerLocalChainId, Base64.NO_WRAP))
                    .build();
        } else {
            this.header = new MsgHeader.Builder()
                    .setMsgType(TypeMsg.MSG_SUCCESS.getType())
                    .setMacType(TypeMac.HMAC_SHA256.getType())
                    .setSerializationType(TypeComp.LZ4.getType())
                    .setTotalLen(MSG_HEADER_LEN + getCompressedJsonLen())
                    .setSender(Base58.decode(userID))
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
        return gson.toJson(PackMsgSuccess.this).getBytes();
    }
}
