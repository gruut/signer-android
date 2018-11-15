package com.gruutnetworks.gruutsigner.model;

import com.google.gson.annotations.SerializedName;

public class SignUpSourceData {

    @SerializedName("phone")
    private String phone;

    @SerializedName("publicKey")
    private String publicKey;

    public SignUpSourceData(String phone, String publicKey) {
        this.phone = phone;
        this.publicKey = publicKey;
    }
}
