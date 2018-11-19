package com.gruutnetworks.gruutsigner.gruut;

import java.nio.ByteBuffer;

public class MessageHeader {
    public static final int MSG_HEADER_LEN = 32;
    public static final int HEADER_TOTAL_LEN_SIZE = 4;
    public static final int HEADER_LOCAL_CHAIN_ID_SIZE = 8;
    public static final int HEADER_SENDER_SIZE = 8;
    public static final int HEADER_RESERVED_SIZE = 6;

    private final byte gruutConstant;   // 8 bits
    private final byte mainVersion;     // 4 bits
    private final byte subVersion;      // 4 bits
    private final byte msgType;         // 8 bits
    private final byte macType;         // 8 bits
    private final byte compressionType; // 8 bits
    private final byte notUsed;         // 8 bits
    private final byte[] totalLen;      // 32 bits
    private final byte[] localChainId;  // 64 bits
    private final byte[] sender;        // 64 bits
    private final byte[] reserved;      // 48 bits

    private MessageHeader(Builder builder) {
        this.gruutConstant = builder.gruutConstant;
        this.mainVersion = builder.mainVersion;
        this.subVersion = builder.subVersion;
        this.msgType = builder.msgType;
        this.macType = builder.macType;
        this.compressionType = builder.compressionType;
        this.notUsed = builder.notUsed;
        this.totalLen = builder.totalLen;
        this.localChainId = builder.localChainId;
        this.sender = builder.sender;
        this.reserved = builder.reserved;
    }

    public byte[] convertToByteArr() {
        ByteBuffer buffer = ByteBuffer.allocate(MSG_HEADER_LEN);
        buffer.clear();

        buffer.put(gruutConstant);

        byte version = (byte) (mainVersion << 4);
        version += subVersion;
        buffer.put(version);
        buffer.put(msgType);
        buffer.put(macType);
        buffer.put(compressionType);
        buffer.put(notUsed);
        buffer.put(totalLen);
        buffer.put(localChainId);
        buffer.put(sender);
        buffer.put(reserved);

        byte[] bytes = buffer.array();
        buffer.clear();
        return bytes;
    }

    @Override
    public String toString() {
        String str = "{";
        str += "gruutConstant: " + gruutConstant + ", ";
        str += "mainVersion: " + mainVersion + ", ";
        str += "subVersion: " + subVersion + ", ";
        str += "msgType: " + msgType + ", ";
        str += "macType: " + macType + ", ";
        str += "compressionType: " + compressionType + ", ";
        str += "notUsed: " + notUsed + ", ";
        str += "totalLen: " + new String(totalLen) + ", ";
        str += "localChainId: " + new String(localChainId) + ", ";
        str += "sender: " + new String(sender) + ", ";
        str += "reserved: " + new String(reserved) + "}";
        return str;
    }

    public int getTotalLen() {
        return ByteBuffer.wrap(totalLen).getInt();
    }

    public static class Builder {
        private byte gruutConstant = 'G';
        private byte mainVersion;
        private byte subVersion;
        private byte msgType;
        private byte macType;
        private byte compressionType;
        private byte notUsed = 0;
        private byte[] totalLen = new byte[HEADER_TOTAL_LEN_SIZE];
        private byte[] localChainId = new byte[HEADER_LOCAL_CHAIN_ID_SIZE];
        private byte[] sender = new byte[HEADER_SENDER_SIZE];
        private byte[] reserved = new byte[HEADER_RESERVED_SIZE];

        public MessageHeader build() {
            return new MessageHeader(this);
        }

        public Builder setGruutConstant(byte gruutConstant) {
            this.gruutConstant = gruutConstant;
            return this;
        }

        public Builder setMainVersion(byte mainVersion) {
            this.mainVersion = mainVersion;
            return this;
        }

        public Builder setSubVersion(byte subVersion) {
            this.subVersion = subVersion;
            return this;
        }

        public Builder setMsgType(byte msgType) {
            this.msgType = msgType;
            return this;
        }

        public Builder setMacType(byte macType) {
            this.macType = macType;
            return this;
        }

        public Builder setCompressionType(byte compressionType) {
            this.compressionType = compressionType;
            return this;
        }

        public Builder setNotUsed(byte notUsed) {
            this.notUsed = notUsed;
            return this;
        }

        public Builder setTotalLen(byte[] totalLen) {
            System.arraycopy(totalLen, 0, this.totalLen,
                    this.totalLen.length - totalLen.length, totalLen.length);
            return this;
        }

        public Builder setLocalChainId(byte[] localChainId) {
            System.arraycopy(localChainId, 0, this.localChainId,
                    this.localChainId.length - localChainId.length, localChainId.length);
            return this;
        }

        public Builder setSender(byte[] sender) {
            System.arraycopy(sender, 0, this.sender,
                    this.sender.length - sender.length, sender.length);
            return this;
        }

        public Builder setReserved(byte[] reserved) {
            System.arraycopy(reserved, 0, this.reserved,
                    this.reserved.length - reserved.length, reserved.length);
            return this;
        }
    }
}
