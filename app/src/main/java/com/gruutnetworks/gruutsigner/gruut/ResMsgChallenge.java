package com.gruutnetworks.gruutsigner.gruut;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResMsgChallenge extends ResMsg {
    @Expose
    @SerializedName("sender")
    private String sender;
    @Expose
    @SerializedName("time")
    private String time;
    @Expose
    @SerializedName("mN")
    private String mergerNonce;

    public ResMsgChallenge(byte[] bytes) {
        parse(bytes); // parse the whole message
        bodyFromJson(body);
    }

    @Override
    void bodyFromJson(byte[] bodyBytes) {
        Gson gson = new Gson();
        ResMsgChallenge msgChallenge = gson.fromJson(new String(bodyBytes), ResMsgChallenge.class);

        this.sender = msgChallenge.sender;
        this.time = msgChallenge.time;
        this.mergerNonce = msgChallenge.mergerNonce;
    }
}
