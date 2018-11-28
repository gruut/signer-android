package com.gruutnetworks.gruutsigner.model;

import android.util.Base64;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static com.gruutnetworks.gruutsigner.model.MsgHeader.MSG_HEADER_LEN;

/**
 * Title: Join
 * Description: Signer's network participation request
 * Message Type: 0x54
 */
public class PackMsgJoin extends MsgPacker {
    @Expose
    @SerializedName("sender")
    private String sender;  // BASE64 encoded 8 byte data
    @Expose
    @SerializedName("time")
    private String time;    // UNIX timestamp
    @Expose
    @SerializedName("ver")
    private String ver;
    @Expose
    @SerializedName("cID")
    private String localChainId;  // BASE64 encoded 8 byte data

    public PackMsgJoin(String sender, String time, String ver, String localChainId) {
        this.sender = sender;
        this.time = time;
        this.ver = ver;
        this.localChainId = localChainId;

        setHeader();
    }

    @Override
    void setHeader() {
        this.header = new MsgHeader.Builder()
                .setMsgType(TypeMsg.MSG_JOIN.getType())
                .setCompressionType(TypeComp.LZ4.getType())
                .setTotalLen(MSG_HEADER_LEN + getCompressedJsonLen())
                .setLocalChainId(Base64.decode(localChainId, Base64.NO_WRAP)) // Base64 decoding
                .setSender(Base64.decode(sender, Base64.NO_WRAP)) // Base64 decoding
                .build();
    }

    @Override
    public byte[] bodyToJson() {
        Gson gson = new Gson();
        return gson.toJson(PackMsgJoin.this).getBytes();
    }
}
