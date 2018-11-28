package com.gruutnetworks.gruutsigner.gruut;

import com.gruutnetworks.gruutsigner.util.CompressionUtil;

import java.util.Arrays;

import static com.gruutnetworks.gruutsigner.gruut.MessageHeader.*;
import static com.gruutnetworks.gruutsigner.gruut.MessageHeader.MSG_HEADER_LEN;

public abstract class ResMsg {
    abstract void bodyFromJson(byte[] bodyBytes);

    MessageHeader header;
    byte[] body;
    byte[] mac;

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

        byte[] jsonMsg = Arrays.copyOfRange(bytes, offset, offset += (header.getTotalLen() - MSG_HEADER_LEN));
        byte[] mac = Arrays.copyOfRange(bytes, offset, bytes.length);

        this.header = header;
        this.mac = mac;

        // 압축 해제가 필요할 경우 해제 후 body에 넣음.
        switch (header.getCompressType()) {
            case LZ4:
                this.body = CompressionUtil.decompress(jsonMsg);
                break;
            case NONE:
            default:
                this.body = jsonMsg;
                break;
        }
    }
}
