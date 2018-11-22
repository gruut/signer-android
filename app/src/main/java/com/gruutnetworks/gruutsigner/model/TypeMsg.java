package com.gruutnetworks.gruutsigner.model;

public enum TypeMsg {
    MSG_REQ_SIGNUP((byte) 0x50),
    MSG_ISSUE_NID((byte) 0x51),
    MSG_REQ_CERT((byte) 0x52),
    MSG_ISSUE_CERT((byte) 0x53),
    MSG_JOIN((byte) 0x54),
    MSG_CHALLENGE((byte) 0x55),
    MSG_RESPONSE_1((byte) 0x56),
    MSG_RESPONSE_2((byte) 0x57),
    MSG_SUCCESS((byte) 0x58),
    MSG_ACCEPT((byte) 0x59),
    MSG_ECHO((byte) 0x5A),
    MSG_LEAVE((byte) 0x5B),
    MSG_REQ_SSIG((byte) 0xB2),
    MSG_SSIG((byte) 0xB3),
    MSG_ERROR((byte) 0xFF);

    private byte typeVal;

    TypeMsg(byte b) {
        typeVal = b;
    }

    public byte getType() {
        return typeVal;
    }

    public static TypeMsg convert(byte value) {
        for (TypeMsg t : TypeMsg.values()) {
            if (t.getType() == value) {
                return t;
            }
        }
        return MSG_ERROR;
    }
}
