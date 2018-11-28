package com.gruutnetworks.gruutsigner.gruut;

import android.util.Base64;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.gruutnetworks.gruutsigner.model.TypeComp;
import com.gruutnetworks.gruutsigner.model.TypeMsg;

import static com.gruutnetworks.gruutsigner.gruut.MessageHeader.MSG_HEADER_LEN;

/**
 * Title: Response1 to Challenge
 * Description: Merger's response to identity verification request from Signer
 * Message Type: 0x56
 */
public class PackMsgResponse1 extends MsgPacker {
    @Expose(serialize = false)
    private String headerLocalChainId;

    @Expose
    @SerializedName("sender")
    private String sender;  // BASE64 encoded 8 byte data
    @Expose
    @SerializedName("time")
    private String time;    // UNIX timestamp
    @Expose
    @SerializedName("cert")
    private String cert;    // pem Certificate
    @Expose
    @SerializedName("sN")
    private String signerNonce; // 256bit random nonce
    @Expose
    @SerializedName("dhx")
    private String dhPubKeyX;   // HEX
    @Expose
    @SerializedName("dhy")
    private String dhPubKeyY;   // HEX
    @Expose
    @SerializedName("sig")
    private String sig; // signature with signer's nonce, merger's nonce, dhx, dhy, time

    public PackMsgResponse1(String sender, String time, String cert, String signerNonce, String dhPubKeyX, String dhPubKeyY, String sig) {
        this.sender = sender;
        this.time = time;
        this.cert = cert;
        this.signerNonce = signerNonce;
        this.dhPubKeyX = dhPubKeyX;
        this.dhPubKeyY = dhPubKeyY;
        this.sig = sig;

        setHeader();
    }

    public PackMsgResponse1(String headerLocalChainId, String sender, String time, String cert, String signerNonce, String dhPubKeyX, String dhPubKeyY, String sig) {
        this.headerLocalChainId = headerLocalChainId;
        this.sender = sender;
        this.time = time;
        this.cert = cert;
        this.signerNonce = signerNonce;
        this.dhPubKeyX = dhPubKeyX;
        this.dhPubKeyY = dhPubKeyY;
        this.sig = sig;

        setHeader();
    }

    @Override
    void setHeader() {
        if (headerLocalChainId != null) {
            this.header = new MessageHeader.Builder()
                    .setMsgType(TypeMsg.MSG_RESPONSE_1.getType())
                    .setCompressionType(TypeComp.LZ4.getType())
                    .setTotalLen(MSG_HEADER_LEN + getCompressedJsonLen())
                    .setSender(Base64.decode(sender, Base64.NO_WRAP)) // Base64 decoding
                    .setLocalChainId(Base64.decode(headerLocalChainId, Base64.NO_WRAP))
                    .build();
        } else {
            this.header = new MessageHeader.Builder()
                    .setMsgType(TypeMsg.MSG_RESPONSE_1.getType())
                    .setCompressionType(TypeComp.LZ4.getType())
                    .setTotalLen(MSG_HEADER_LEN + getCompressedJsonLen())
                    .setSender(Base64.decode(sender, Base64.NO_WRAP)) // Base64 decoding
                    .build();
        }
    }

    @Override
    byte[] bodyToJson() {
        Gson gson = new Gson();
        return gson.toJson(PackMsgResponse1.this).getBytes();
    }
}
