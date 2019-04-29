package com.gruutnetworks.gruutuser.exceptions;

public class AuthUtilException extends RuntimeException {

    public enum AuthErr {
        KEY_GEN_ERROR,
        HMAC_KEY_GEN_ERROR,
        NO_KEY_ERROR,
        NO_CERT_ERROR,
        SIGNING_ERROR,
        VERIFYING_ERROR,
        GET_CERT_ERROR,
        INVALID_SIGNATURE
    }

    public AuthUtilException(AuthErr authErr) {
        super(authErr.name());
    }
}
