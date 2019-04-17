package com.gruutnetworks.gruutsigner.model;

import android.util.Base64;
import com.gruutnetworks.gruutsigner.gruut.GruutConfigs;
import org.spongycastle.util.encoders.Hex;
import com.gruutnetworks.gruutsigner.util.Base58;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class MsgHeader {
    static final int MSG_HEADER_LEN = 32;
    static final int HEADER_TOTAL_LEN_SIZE = 4;
    static final int HEADER_LOCAL_CHAIN_ID_SIZE = 8;
    static final int HEADER_WORLD_ID_SIZE = 8;
    static final int HEADER_SENDER_SIZE = 32;

    private final byte gruutConstant;   // 8 bits
    private final byte version;         // 8 bits
    private final byte msgType;         // 8 bits
    private final byte macType;         // 8 bits
    private final byte serializationType; // 8 bits
    private final byte notUsed;          // 8 bits;
    private final byte[] totalLen;      // 32 bits
    private final byte[] worldId;        // 64 bits
    private final byte[] localChainId;  // 64 bits
    private final byte[] sender;        // 256 bits

    private MsgHeader(Builder builder) {
        this.gruutConstant = builder.gruutConstant;
        this.version = builder.version;
        this.msgType = builder.msgType;
        this.macType = builder.macType;
        this.serializationType = builder.serializationType;
        this.notUsed = builder.notUsed;
        this.totalLen = builder.totalLen;
        this.worldId = builder.worldId;
        this.localChainId = builder.localChainId;
        this.sender = builder.sender;
    }

    byte[] convertToByteArr() {
        ByteBuffer buffer = ByteBuffer.allocate(MSG_HEADER_LEN);
        buffer.clear();

        buffer.put(gruutConstant);
        buffer.put(version);
        buffer.put(msgType);
        buffer.put(macType);
        buffer.put(serializationType);
        buffer.put(notUsed);
        buffer.put(totalLen);
        buffer.put(worldId);
        buffer.put(localChainId);
        buffer.put(sender);

        byte[] bytes = buffer.array();
        buffer.clear();
        return bytes;
    }

    @Override
    public String toString() {
        String str = "{";
        str += "gruutConstant: " + String.format("0x%02X", gruutConstant) + ", ";
        str += "Version: " + String.format("0x%02X", version) + ", ";
        str += "msgType: " + String.format("0x%02X", msgType) + ", ";
        str += "macType: " + String.format("0x%02X", macType) + ", ";
        str += "serializationType: " + String.format("0x%02X", serializationType) + ", ";
        str += "notUsed: " + String.format("0x%02X", notUsed) + ", ";
        str += "totalLen: " + new BigInteger(1, totalLen) + ", ";
        str += "worldId: " + new String(Hex.encode(worldId)) + ", ";
        str += "localChainId: " + new String(Hex.encode(localChainId)) + ", ";
        str += "sender: " + new String(Hex.encode(sender)) + "}";
        return str;
    }

    /**
     * @return 헤더의 sender byte array를 base 58로 인코딩하여 반환
     */
    String getSender() {
        return Base58.encode(sender);
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
        return TypeComp.convert(serializationType);
    }

    public static class Builder {
        private byte gruutConstant = GruutConfigs.gruutConstant;
        private byte version = GruutConfigs.version;
        private byte msgType;
        private byte macType = TypeMac.NONE.getType();
        private byte serializationType = TypeComp.NONE.getType();
        private byte notUsed = 0;
        private byte[] totalLen = new byte[HEADER_TOTAL_LEN_SIZE];
        private byte[] worldId = GruutConfigs.worldId.getBytes();
        private byte[] localChainId = GruutConfigs.localChainId.getBytes();
        private byte[] sender = new byte[HEADER_SENDER_SIZE];

        public MsgHeader build() {
            return new MsgHeader(this);
        }

        Builder setGruutConstant(byte gruutConstant) {
            this.gruutConstant = gruutConstant;
            return this;
        }

        Builder setVersion(byte version) {
            this.version = version;
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

        Builder setSerializationType(byte serializationType) {
            this.serializationType = serializationType;
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

        Builder setWorldId(byte[] worldId) {
            System.arraycopy(worldId, 0, this.worldId,
                    this.worldId.length - worldId.length, worldId.length);
            return this;
        }

        Builder setSender(byte[] sender) {
            System.arraycopy(sender, 0, this.sender,
                    this.sender.length - sender.length, sender.length);
            return this;
        }
    }
}
