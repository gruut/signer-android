package com.gruutnetworks.gruutsigner.model;

public enum TypeComp {
    LZ4 ((byte) 0x04),
    NONE((byte) 0xFF);

    private byte typeVal;

    TypeComp(byte b) {
        typeVal = b;
    }

    public byte getType() {
        return typeVal;
    }

    public static TypeComp convert(byte value) {
        for (TypeComp t : TypeComp.values()) {
            if (t.getType() == value) {
                return t;
            }
        }
        return NONE;
    }
}
