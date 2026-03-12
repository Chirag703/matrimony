package com.matrimony.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageCentralValidateOtpResponse {

    @JsonProperty("responseCode")
    private Integer responseCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private ValidateOtpData data;

    private String phoneNumber;

    @JsonProperty("token")
    private String token;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ValidateOtpData {

        @JsonProperty("verificationId")
        private String verificationId;

        @JsonProperty("mobileNumber")
        private String mobileNumber;

        @JsonProperty("verificationStatus")
        private String verificationStatus;

        @JsonProperty("responseCode")
        private String responseCode;

        @JsonProperty("errorMessage")
        private String errorMessage;

        @JsonProperty("transactionId")
        private String transactionId;

        @JsonProperty("authToken")
        private String authToken;
    }
}
