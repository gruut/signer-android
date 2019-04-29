package com.gruutnetworks.gruutuser.model;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Title: Error
 * Description: 요청 거부 및 오류
 * Message Type: 0xFF
 */
public class UnpackMsgError extends MsgUnpacker {
    @Expose
    @SerializedName("sender")
    private String sender;
    @Expose
    @SerializedName("time")
    private String time;
    @Expose
    @SerializedName("type")
    private String errType;
    @Expose
    @SerializedName("info")
    private String errInfo;

    public UnpackMsgError(byte[] bytes) {
        parse(bytes); // parse the whole message
        bodyFromJson(body);
        setSenderValidity();
    }

    public String getErrType() {
        return TypeError.convert(errType).getType();
    }

    public String getErrInfo() {
        return errInfo;
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

        UnpackMsgError msgError = gson.fromJson(new String(bodyBytes), UnpackMsgError.class);

        this.sender = msgError.sender;
        this.time = msgError.time;
        this.errType = msgError.errType;
        this.errInfo = msgError.errInfo;
    }

    @Override
    void setSenderValidity() {
        this.senderValidity = header.getSender().equals(sender);
    }
}
