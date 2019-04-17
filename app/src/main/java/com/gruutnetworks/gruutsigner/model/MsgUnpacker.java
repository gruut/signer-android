package com.gruutnetworks.gruutsigner.model;

import com.gruutnetworks.gruutsigner.util.AuthHmacUtil;
import com.gruutnetworks.gruutsigner.util.CompressionUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static com.gruutnetworks.gruutsigner.model.MsgHeader.*;

public abstract class MsgUnpacker {
    abstract void bodyFromJson(byte[] bodyBytes);

    abstract void setSenderValidity();

    MsgHeader header;
    byte[] body;
    boolean senderValidity;
    private boolean macValidity;

    public static TypeMsg classifyMsg(byte[] bytes) {
        if (bytes != null && bytes.length > 3) {
            return TypeMsg.convert(bytes[2]);
        }
        return TypeMsg.MSG_ERROR;
    }

    void parse(byte[] bytes) {
        int offset = 0;

        MsgHeader header = new MsgHeader.Builder()
                .setGruutConstant(bytes[offset++])
                .setVersion(bytes[offset++])
                .setMsgType(bytes[offset++])
                .setMacType(bytes[offset++])
                .setSerializationType(bytes[offset++])
                .setNotUsed(bytes[offset++])
                .setTotalLen(Arrays.copyOfRange(bytes, offset, offset += HEADER_TOTAL_LEN_SIZE))
                .setWorldId(Arrays.copyOfRange(bytes, offset, offset += HEADER_WORLD_ID_SIZE))
                .setLocalChainId(Arrays.copyOfRange(bytes, offset, offset += HEADER_LOCAL_CHAIN_ID_SIZE))
                .setSender(Arrays.copyOfRange(bytes, offset, offset += HEADER_SENDER_SIZE))
                .build();

        byte[] compressedMsg = Arrays.copyOfRange(bytes, offset, offset += (header.getTotalLen() - MSG_HEADER_LEN));
        byte[] mac = Arrays.copyOfRange(bytes, offset, bytes.length);

        this.header = header;
        this.macValidity = checkMacValidity(header, compressedMsg, mac);

        // 압축 해제가 필요할 경우 해제 후 body에 넣음.
        this.body = decompressData(header.getCompressType(), compressedMsg);
    }

    private byte[] decompressData(TypeComp typeComp, byte[] data) {
        switch (typeComp) {
            case LZ4:
                return CompressionUtil.decompress(data);
            case NONE:
            default:
                return data;
        }
    }

    private boolean checkMacValidity(MsgHeader header, byte[] compressedData, byte[] mac) {
        try {
            switch (header.getMacType()) {
                case HMAC_SHA256:
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    outputStream.write(header.convertToByteArr());
                    outputStream.write(compressedData);

                    byte[] headerAndBody = outputStream.toByteArray();
                    outputStream.close();

                    return AuthHmacUtil.verifyHmacSignature(header.getSender(), headerAndBody, mac);
                case NONE:
                default:
                    return true;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public TypeMsg getMessageType() {
        return header.getMsgType();
    }

    public boolean isMacValid() {
        return macValidity;
    }

    public boolean isSenderValid() {
        return senderValidity;
    }
}
