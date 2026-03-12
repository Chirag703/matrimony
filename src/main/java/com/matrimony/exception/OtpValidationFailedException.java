package com.matrimony.exception;

public class OtpValidationFailedException extends RuntimeException {

    private final String errorCode;

    public OtpValidationFailedException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
