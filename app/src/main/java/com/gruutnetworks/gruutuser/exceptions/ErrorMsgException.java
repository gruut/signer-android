package com.gruutnetworks.gruutuser.exceptions;

public class ErrorMsgException extends RuntimeException {

    public enum MsgErr {
        MSG_ERR_RECEIVED,
        MSG_INVALID_RECEIVED,
        MSG_MERGER_ERR,
        MSG_MERGER_ECDH_ERROR,
        MSG_NOT_FOUND,
        MSG_INVALID_HMAC,
        MSG_EXPIRED,
        MSG_HEADER_NOT_MATCHED
    }

    public ErrorMsgException(MsgErr msgErr) {
        super(msgErr.name());
    }

    public ErrorMsgException(MsgErr msgErr, String str) {
        super(msgErr.name() + "::" + str);
    }
}

