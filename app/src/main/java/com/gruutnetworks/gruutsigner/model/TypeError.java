package com.gruutnetworks.gruutsigner.model;

public enum TypeError {
    UNKNOWN             ("0"),
    MERGER_BOOTSTRAP    ("11"),
    ECDH_ILLEGAL_ACCESS ("21"),
    ECDH_MAX_SIGNER_POOL("22"),
    ECDH_TIMEOUT        ("23"),
    ECDH_INVALID_SIG    ("24"),
    ECDH_INVALID_PK     ("25"),
    TIME_SYNC           ("61");

    private String typeVal;

    TypeError(String b) {
        typeVal = b;
    }

    public String getType() {
        return typeVal;
    }

    public static TypeError convert(String value) {
        for (TypeError t : TypeError.values()) {
            if (t.getType().equals(value)) {
                return t;
            }
        }
        return UNKNOWN;
    }
}
