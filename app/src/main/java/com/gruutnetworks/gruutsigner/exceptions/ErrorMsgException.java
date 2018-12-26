package com.gruutnetworks.gruutsigner.exceptions;

public class ErrorMsgException extends RuntimeException {

    public enum MsgErr {
        MSG_ERR_RECEIVED,
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

