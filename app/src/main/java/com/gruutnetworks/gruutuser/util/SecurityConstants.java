package com.gruutnetworks.gruutuser.util;

public interface SecurityConstants {
    String KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore";

    String TYPE_ECDH = "ECDH";
    String TYPE_SHA256 = "SHA-256";
    String TYPE_HMAC = "HmacSHA256";

    String SIGNATURE_SHA256withRSA = "SHA256withRSA";
    String SHA256withECDSA = "SHA256withECDSA";

    String CURVE_SECP256R1 = "secp256r1";

    int MSG_EXPIRATION_TIME = 1000 * 10;
    int BLOCK_EXPIRATION_TIME = 1000 * 60 * 10;

    enum Alias {
        SELF_CERT,
        GRUUT_AUTH
    }
}