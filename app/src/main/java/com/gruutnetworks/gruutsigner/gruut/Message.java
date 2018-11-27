package com.gruutnetworks.gruutsigner.gruut;

import com.gruutnetworks.gruutsigner.util.CompressionUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.gruutnetworks.gruutsigner.gruut.MessageHeader.*;

public class Message {

    private MessageHeader header; // 32 bytes
    private byte[] body; // message body
    private byte[] mac;

    /**
     * Byte array to formatted message
     * Warning! array's offset index and header's setter order are VERY strict.
     * Do not reorder it.
     *
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

    public Message(MessageHeader header, byte[] body, byte[] mac) {
        this.header = header;
        this.body = body;
        this.mac = mac;
    }

    public byte[] convertToByteArr() {
        int totalLength = header.getTotalLen();

        if (mac == null) {
            return convertToByteArrWithoutSig();
        }

        totalLength += mac.length;
        ByteBuffer buffer = ByteBuffer.allocate(totalLength);
        buffer.clear();
        buffer.put(header.convertToByteArr());
        buffer.put(body);
        buffer.put(mac);

        byte[] bytes = buffer.array();
        buffer.clear();
        return bytes;
    }

    public byte[] convertToByteArrWithoutSig() {
        int totalLength = header.getTotalLen();

        ByteBuffer buffer = ByteBuffer.allocate(totalLength);
        buffer.clear();
        buffer.put(header.convertToByteArr());
        buffer.put(body);

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

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public byte[] getMac() {
        return mac;
    }

    public void setMac(byte[] mac) {
        this.mac = mac;
    }

    @Override
    public String toString() {
        String str = header.toString();
        str += "\njson: " + new String(body);
        if (mac != null) {
            str += "\nmac: " + new String(mac);
        }
        return str;
    }
}
