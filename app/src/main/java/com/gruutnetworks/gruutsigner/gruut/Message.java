package com.gruutnetworks.gruutsigner.gruut;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.gruutnetworks.gruutsigner.gruut.MessageHeader.*;

public class Message {

    private MessageHeader header; // 32 bytes
    private byte[] compressedJsonMsg; // Compressed JSON message
    private byte[] signature;

    /**
     * Byte array to formatted message
     * Warning! array's offset index and header's setter order are VERY strict.
     * Do not reorder it.
     * @param bytes received byte array
     */
    public Message(byte[] bytes) {
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
        byte[] signature = Arrays.copyOfRange(bytes, offset, bytes.length);

        this.header = header;
        this.compressedJsonMsg = jsonMsg;
        this.signature = signature;
    }

    public Message(MessageHeader header, byte[] compressedJsonMsg, byte[] signature) {
        this.header = header;
        this.compressedJsonMsg = compressedJsonMsg;
        this.signature = signature;
    }

    public byte[] covertToByteArr() {
        int totalLength = header.getTotalLen();

        if (signature != null) {
            totalLength += signature.length;
        }

        ByteBuffer buffer = ByteBuffer.allocate(totalLength);
        buffer.clear();
        buffer.put(header.convertToByteArr());
        buffer.put(compressedJsonMsg);

        if (signature != null) {
            buffer.put(signature);
        }

        byte[] bytes = buffer.array();
        buffer.clear();
        return bytes;
    }

    public MessageHeader getHeader() {
        return header;
    }

    public void setHeader(MessageHeader header) {
        this.header = header;
    }

    public byte[] getCompressedJsonMsg() {
        return compressedJsonMsg;
    }

    public void setCompressedJsonMsg(byte[] compressedJsonMsg) {
        this.compressedJsonMsg = compressedJsonMsg;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return header.toString() + "\njson: " + new String(compressedJsonMsg) + "\nsignature: " + new String(signature);
    }
}
