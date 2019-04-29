package com.gruutnetworks.gruutuser.model;

import com.google.gson.annotations.SerializedName;

public class SignUpSourceData {

    @SerializedName("phone")
    private String phone;

    @SerializedName("csr")
    private String csr;

    public SignUpSourceData(String phone, String csr) {
        this.phone = phone;
        this.csr = csr;
    }
}
