package com.gruutnetworks.gruutsigner.model;

import com.google.gson.annotations.SerializedName;

public class JoiningResponse {
    @SerializedName("nid")
    private int nid;
    @SerializedName("pem")
    private String pem;

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public String getPem() {
        return pem;
    }

    public void setPem(String pem) {
        this.pem = pem;
    }
}
