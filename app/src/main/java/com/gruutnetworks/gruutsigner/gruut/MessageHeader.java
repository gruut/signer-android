package com.gruutnetworks.gruutsigner.gruut;

import java.nio.ByteBuffer;

public class MessageHeader {
    public static final int MSG_HEADER_LEN = 32;

    private final byte gruutConstant;   // 8 bits
    private final byte mainVersion;     // 4 bits
    private final byte subVersion;      // 4 bits
    private final byte msgType;         // 8 bits
    private final byte macType;         // 8 bits
    private final byte compressionType; // 8 bits
    private final byte notUsed;         // 8 bits
    private final int totalLen;         // 32 bits
    private final long localChainId;    // 64 bits
    private final long sender;          // 64 bits
    private final long reserved;        // 48 bits

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
        buffer.putInt(totalLen);
        buffer.putLong(localChainId);
        buffer.putLong(sender);
        buffer.putLong(reserved);

        byte[] bytes = buffer.array();
        buffer.clear();
        return bytes;
    }

    public byte getGruutConstant() {
        return gruutConstant;
    }

    public byte getMainVersion() {
        return mainVersion;
    }

    public byte getSubVersion() {
        return subVersion;
    }

    public byte getMsgType() {
        return msgType;
    }

    public byte getMacType() {
        return macType;
    }

    public int getTotalLen() {
        return totalLen;
    }

    public long getLocalChainId() {
        return localChainId;
    }

    public long getSender() {
        return sender;
    }

    public long getReserved() {
        return reserved;
    }

    @Override
    public String toString() {
        String str = "{";
        str += "gruutConstant: " + gruutConstant + ", ";
        str += "mainVersion: " + mainVersion + ", ";
        str += "subVersion: " + subVersion + ", ";
        str += "msgType: " + msgType + ", ";
        str += "macType: " + macType + ", ";
        str += "totalLen: " + totalLen + ", ";
        str += "localChainId: " + localChainId + ", ";
        str += "sender: " + sender + ", ";
        str += "reserved: " + reserved + "}";
        return str;
    }

    public static class Builder {
        private byte gruutConstant = 'G';
        private long reserved = 0;

        private byte mainVersion;
        private byte subVersion;
        private byte msgType;
        private byte macType;
        private byte compressionType;
        private byte notUsed = 0;
        private int totalLen;
        private long localChainId;
        private long sender;

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

        public Builder setTotalLen(int totalLen) {
            this.totalLen = totalLen;
            return this;
        }

        public Builder setLocalChainId(long localChainId) {
            this.localChainId = localChainId;
            return this;
        }

        public Builder setSender(long sender) {
            this.sender = sender;
            return this;
        }

        public Builder setReserved(long reserved) {
            this.reserved = reserved;
            return this;
        }
    }
}
