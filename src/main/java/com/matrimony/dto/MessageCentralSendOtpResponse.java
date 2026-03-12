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
public class MessageCentralSendOtpResponse {

    @JsonProperty("responseCode")
    private Integer responseCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private SendOtpData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SendOtpData {

        @JsonProperty("verificationId")
        private String verificationId;

        @JsonProperty("mobileNumber")
        private String mobileNumber;

        @JsonProperty("responseCode")
        private String responseCode;

        @JsonProperty("errorMessage")
        private String errorMessage;

        @JsonProperty("timeout")
        private String timeout;

        @JsonProperty("smsCLI")
        private String smsCLI;

        @JsonProperty("transactionId")
        private String transactionId;
    }
}
