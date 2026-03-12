package com.matrimony.exception;

public class OtpSendFailedException extends RuntimeException {

    private final String errorCode;

    public OtpSendFailedException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
