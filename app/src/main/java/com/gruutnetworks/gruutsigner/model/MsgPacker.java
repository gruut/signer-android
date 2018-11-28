package com.gruutnetworks.gruutsigner.model;

import com.gruutnetworks.gruutsigner.util.CompressionUtil;
import com.gruutnetworks.gruutsigner.util.KeystoreUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class MsgPacker {
    abstract void setHeader();

    abstract byte[] bodyToJson();

    MsgHeader header;

    int getCompressedJsonLen() {
        return CompressionUtil.compress(bodyToJson()).length;
    }

    private byte[] generateMac(TypeMac typeMac, byte[] data) {
        switch (typeMac) {
            case HMAC_SHA256:
                return KeystoreUtil.getHmacSignature(data);
            default:
                return null;
        }
    }

    private byte[] compressData(TypeComp typeComp, byte[] data) {
        switch (typeComp) {
            case LZ4:
                return CompressionUtil.compress(data);
            case NONE:
            default:
                return data;
        }
    }

    public byte[] convertToByteArr() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(header.convertToByteArr());

            // Compress the data if necessary
            byte[] body = compressData(header.getCompressType(), bodyToJson());
            outputStream.write(body);

            // Generate the mac if necessary
            byte[] mac = generateMac(header.getMacType(), outputStream.toByteArray());
            outputStream.write(mac);

            byte[] wholeMsg = outputStream.toByteArray();
            outputStream.close();

            return wholeMsg;
        } catch (IOException e) {
            return null;
        }
    }

    public TypeMsg getMessageType() {
        return header.getMsgType();
    }
}
