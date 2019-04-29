package com.gruutnetworks.gruutuser.model;

import com.gruutnetworks.gruutuser.util.AuthHmacUtil;
import com.gruutnetworks.gruutuser.util.CompressionUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class MsgPacker {
    abstract void setHeader();

    abstract public void setDestinationId(String id);

    abstract byte[] bodyToJson();

    MsgHeader header;
    String destinationId;

    int getCompressedJsonLen() {
        return CompressionUtil.compress(bodyToJson()).length;
    }

    private byte[] generateMac(TypeMac typeMac, byte[] data) {
        switch (typeMac) {
            case HMAC_SHA256:
                return AuthHmacUtil.getHmacSignature(destinationId, data);
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
            if (mac != null) {
                outputStream.write(mac);
            }

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
