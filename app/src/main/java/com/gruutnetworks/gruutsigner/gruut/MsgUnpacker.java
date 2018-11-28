package com.gruutnetworks.gruutsigner.gruut;

import com.gruutnetworks.gruutsigner.model.TypeComp;
import com.gruutnetworks.gruutsigner.model.TypeMsg;
import com.gruutnetworks.gruutsigner.util.CompressionUtil;
import com.gruutnetworks.gruutsigner.util.KeystoreUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static com.gruutnetworks.gruutsigner.gruut.MessageHeader.*;
import static com.gruutnetworks.gruutsigner.gruut.MessageHeader.MSG_HEADER_LEN;

public abstract class MsgUnpacker {
    abstract void bodyFromJson(byte[] bodyBytes);

    MessageHeader header;
    byte[] body;
    byte[] mac;
    boolean macValidity;

    void parse(byte[] bytes) {
        int offset = 0;

        MessageHeader header = new MessageHeader.Builder()
                .setGruutConstant(bytes[offset++])
                .setMainVersion((byte) (bytes[offset] >> 4))
                .setSubVersion((byte) (bytes[offset++] & 0x0f))
                .setMsgType(bytes[offset++])
                .setMacType(bytes[offset++])
                .setCompressionType(bytes[offset++])
                .setNotUsed(bytes[offset++])
                .setTotalLen(Arrays.copyOfRange(bytes, offset, offset += HEADER_TOTAL_LEN_SIZE))
                .setLocalChainId(Arrays.copyOfRange(bytes, offset, offset += HEADER_LOCAL_CHAIN_ID_SIZE))
                .setSender(Arrays.copyOfRange(bytes, offset, offset += HEADER_SENDER_SIZE))
                .setReserved(Arrays.copyOfRange(bytes, offset, offset += HEADER_RESERVED_SIZE))
                .build();

        byte[] compressedMsg = Arrays.copyOfRange(bytes, offset, offset += (header.getTotalLen() - MSG_HEADER_LEN));
        byte[] mac = Arrays.copyOfRange(bytes, offset, bytes.length);

        this.header = header;
        this.mac = mac;
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

    private boolean checkMacValidity(MessageHeader header, byte[] compressedData, byte[] mac) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(header.convertToByteArr());
            outputStream.write(compressedData);

            byte[] headerAndBody = outputStream.toByteArray();
            outputStream.close();

            return KeystoreUtil.verifyHmacSignature(headerAndBody, mac);
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
}
