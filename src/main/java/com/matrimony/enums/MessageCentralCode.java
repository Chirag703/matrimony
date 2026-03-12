package com.matrimony.enums;

public enum MessageCentralCode {
    SUCCESS("200"),
    BAD_REQUEST("400"),
    DUPLICATE_RESOURCE("409"),
    SERVER_ERROR("500"),
    INVALID_CUSTOMER_ID("501"),
    INVALID_VERIFICATION_ID("505"),
    REQUEST_ALREADY_EXISTS("506"),
    INVALID_COUNTRY_CODE("511"),
    VERIFICATION_FAILED("700"),
    WRONG_OTP_PROVIDED("702"),
    ALREADY_VERIFIED("703"),
    VERIFICATION_EXPIRED("705"),
    MAXIMUM_LIMIT_REACHED("800"),
    UNKNOWN("0");

    private final String code;

    MessageCentralCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static MessageCentralCode fromCode(String code) {
        if (code == null) {
            return UNKNOWN;
        }
        for (MessageCentralCode messageCentralCode : MessageCentralCode.values()) {
            if (messageCentralCode.code.equals(code)) {
                return messageCentralCode;
            }
        }
        return UNKNOWN;
    }
}
