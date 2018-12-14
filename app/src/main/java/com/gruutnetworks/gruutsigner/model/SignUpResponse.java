package com.gruutnetworks.gruutsigner.model;

import com.google.gson.annotations.SerializedName;

public class SignUpResponse {

    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;
    @SerializedName("nid")
    private String nid;
    @SerializedName("pem")
    private String pem;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getNid() {
        return nid;
    }

    public String getPem() {
        return pem;
    }

}
