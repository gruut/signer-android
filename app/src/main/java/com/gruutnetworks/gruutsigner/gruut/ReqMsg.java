package com.gruutnetworks.gruutsigner.gruut;

import com.gruutnetworks.gruutsigner.util.CompressionUtil;

import java.nio.ByteBuffer;

public abstract class ReqMsg {
    abstract void setHeader();
    abstract byte[] bodyToJson();

    MessageHeader header;
    byte[] mac;

    int getCompressedJsonLen() {
        return CompressionUtil.compress(bodyToJson()).length;
    }

    public byte[] convertToByteArr() {
        int totalLength = header.getTotalLen();
        if (mac != null) {
            totalLength += mac.length;
        }

        ByteBuffer buffer = ByteBuffer.allocate(totalLength);
        buffer.clear();
        buffer.put(header.convertToByteArr());

        switch (header.getCompressType()) {
            case LZ4:
                buffer.put(CompressionUtil.compress(bodyToJson()));
                break;
            case NONE:
            default:
                buffer.put(bodyToJson());
                break;
        }

        if (mac != null) {
            buffer.put(mac);
        }

        byte[] bytes = buffer.array();
        buffer.clear();

        return bytes;
    }
}
