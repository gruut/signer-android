package com.gruutnetworks.gruutsigner.model;

import android.util.Base64;
import com.gruutnetworks.gruutsigner.gruut.GruutConfigs;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class MsgHeader {
    static final int MSG_HEADER_LEN = 32;
    static final int HEADER_TOTAL_LEN_SIZE = 4;
    static final int HEADER_LOCAL_CHAIN_ID_SIZE = 8;
    static final int HEADER_SENDER_SIZE = 8;
    static final int HEADER_RESERVED_SIZE = 6;

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

    private MsgHeader(Builder builder) {
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

    byte[] convertToByteArr() {
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
        byte version = (byte) (mainVersion << 4);
        version += subVersion;

        String str = "{";
        str += "gruutConstant: " + String.format("0x%02X", gruutConstant) + ", ";
        str += "Version: " + String.format("0x%02X", version) + ", ";
        str += "mainVersion: " + String.format("0x%01X",mainVersion) + ", ";
        str += "subVersion: " +String.format("0x%01X", subVersion) + ", ";
        str += "msgType: " + String.format("0x%02X", msgType) + ", ";
        str += "macType: " + String.format("0x%02X", macType) + ", ";
        str += "compressionType: " + String.format("0x%02X", compressionType) + ", ";
        str += "notUsed: " + String.format("0x%02X", notUsed) + ", ";
        str += "totalLen: " + new BigInteger(1, totalLen) + ", ";
        str += "localChainId: " + new String(Hex.encode(localChainId)) + ", ";
        str += "sender: " + new String(Hex.encode(sender)) + ", ";
        str += "reserved: " + new String(Hex.encode(reserved)) + "}";
        return str;
    }

    /**
     * @return 헤더의 sender byte array를 base 64로 인코딩하여 반환
     */
    String getSender() {
        return Base64.encodeToString(sender, Base64.NO_WRAP);
    }

    int getTotalLen() {
        return ByteBuffer.wrap(totalLen).getInt();
    }

    TypeMsg getMsgType() {
        return TypeMsg.convert(msgType);
    }

    TypeMac getMacType() {
        return TypeMac.convert(macType);
    }

    TypeComp getCompressType() {
        return TypeComp.convert(compressionType);
    }

    public static class Builder {
        private byte gruutConstant = GruutConfigs.gruutConstant;
        private byte mainVersion = GruutConfigs.mainVersion;
        private byte subVersion = GruutConfigs.subVersion;
        private byte msgType;
        private byte macType = TypeMac.NONE.getType();
        private byte compressionType = TypeComp.NONE.getType();
        private byte notUsed = 0;
        private byte[] totalLen = new byte[HEADER_TOTAL_LEN_SIZE];
        private byte[] localChainId = Base64.decode(GruutConfigs.localChainId, Base64.NO_WRAP);
        private byte[] sender = new byte[HEADER_SENDER_SIZE];
        private byte[] reserved = new byte[HEADER_RESERVED_SIZE];

        public MsgHeader build() {
            return new MsgHeader(this);
        }

        Builder setGruutConstant(byte gruutConstant) {
            this.gruutConstant = gruutConstant;
            return this;
        }

        Builder setMainVersion(byte mainVersion) {
            this.mainVersion = mainVersion;
            return this;
        }

        Builder setSubVersion(byte subVersion) {
            this.subVersion = subVersion;
            return this;
        }

        Builder setMsgType(byte msgType) {
            this.msgType = msgType;
            return this;
        }

        Builder setMacType(byte macType) {
            this.macType = macType;
            return this;
        }

        Builder setCompressionType(byte compressionType) {
            this.compressionType = compressionType;
            return this;
        }

        Builder setNotUsed(byte notUsed) {
            this.notUsed = notUsed;
            return this;
        }

        Builder setTotalLen(byte[] totalLen) {
            System.arraycopy(totalLen, 0, this.totalLen,
                    this.totalLen.length - totalLen.length, totalLen.length);
            return this;
        }

        Builder setTotalLen(int totalLen) {
            this.totalLen = ByteBuffer.allocate(HEADER_TOTAL_LEN_SIZE).putInt(totalLen).array();
            return this;
        }

        Builder setLocalChainId(byte[] localChainId) {
            System.arraycopy(localChainId, 0, this.localChainId,
                    this.localChainId.length - localChainId.length, localChainId.length);
            return this;
        }

        Builder setSender(byte[] sender) {
            System.arraycopy(sender, 0, this.sender,
                    this.sender.length - sender.length, sender.length);
            return this;
        }

        Builder setReserved(byte[] reserved) {
            System.arraycopy(reserved, 0, this.reserved,
                    this.reserved.length - reserved.length, reserved.length);
            return this;
        }
    }
}
