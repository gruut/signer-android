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
 * Title: Join
 * Description: Signer's network participation request
 * Message Type: 0x54
 */
public class PackMsgJoin extends MsgPacker {
    @Expose
    @SerializedName("time")
    private String time;    // UNIX timestamp
    @Expose
    @SerializedName("world")
    private String world;   // 8 bytes
    @Expose
    @SerializedName("chain")
    private String chain;  // 8 bytes
    @Expose
    @SerializedName("merger")
    private String merger;  // BASE58 encoded 32 byte data
    @Expose
    @SerializedName("user")
    private String signer;  // BASE58 encoded 32 byte data

    public PackMsgJoin(String time, String world_id, String chain_id, String signer_id, String merger_id) {
        this.time = time;
        this.world = world_id;
        this.chain = chain_id;
        this.signer = signer_id;
        this.merger = merger_id;
        setHeader();
    }

    @Override
    void setHeader() {
        this.header = new MsgHeader.Builder()
                .setMsgType(TypeMsg.MSG_JOIN.getType())
                .setSerializationType(TypeComp.LZ4.getType())
                .setTotalLen(MSG_HEADER_LEN + getCompressedJsonLen())
                .setSender(Base58.decode(signer))
                .build();
    }

    @Override
    public void setDestinationId(String id) {
        this.destinationId = id;
    }

    @Override
    public byte[] bodyToJson() {
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
        String tmp = gson.toJson(PackMsgJoin.this);
        return tmp.getBytes();
    }
}
