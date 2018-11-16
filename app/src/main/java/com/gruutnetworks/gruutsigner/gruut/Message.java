package com.gruutnetworks.gruutsigner.gruut;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.gruutnetworks.gruutsigner.gruut.MessageHeader.MSG_HEADER_LEN;

public class Message {

    private MessageHeader header; // 26 bytes
    private byte[] compressedJsonMsg; // Compressed JSON message
    private byte[] signature;

    public Message(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.BIG_ENDIAN);

        MessageHeader header = new MessageHeader.Builder()
                .setGruutConstant(buffer.get())
                .setMainVersion((byte) (buffer.get(1) >> 4))
                .setSubVersion((byte) (buffer.get() & 0x0f))
                .setMsgType(buffer.get())
                .setMacType(buffer.get())
                .setTotalLen(buffer.getInt())
                .setLocalChainId(buffer.getLong())
                .setSender(buffer.getLong())
                .setReserved(buffer.getShort())
                .build();

        byte[] jsonMsg = new byte[header.getTotalLen() - MSG_HEADER_LEN];
        buffer.get(jsonMsg);

        byte[] signature = new byte[buffer.remaining()];
        buffer.get(signature);

        buffer.clear();

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
