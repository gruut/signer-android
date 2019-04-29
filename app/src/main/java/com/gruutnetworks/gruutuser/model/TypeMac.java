package com.gruutnetworks.gruutuser.model;

public enum TypeMac {
    RSA         ((byte) 0x00),
    ECDSA       ((byte) 0x01),
    EdDSA       ((byte) 0x02),
    Schnorr     ((byte) 0x03),
    HMAC_SHA256 ((byte) 0xF1),
    NONE        ((byte) 0xFF);

    private byte typeVal;

    TypeMac(byte b) {
        typeVal = b;
    }

    public byte getType() {
        return typeVal;
    }

    public static TypeMac convert(byte value) {
        for (TypeMac t : TypeMac.values()) {
            if (t.getType() == value) {
                return t;
            }
        }
        return NONE;
    }
}
