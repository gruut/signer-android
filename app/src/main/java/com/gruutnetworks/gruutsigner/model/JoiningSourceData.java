package com.gruutnetworks.gruutsigner.model;

import com.google.gson.annotations.SerializedName;

public class JoiningSourceData {

    @SerializedName("phone")
    private String phone;

    private String publicKey;

    public JoiningSourceData(String phone, String publicKey) {
        this.phone = phone;
        this.publicKey = publicKey;
    }
}
