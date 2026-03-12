package com.matrimony.client;

import com.matrimony.dto.MessageCentralSendOtpResponse;
import com.matrimony.dto.MessageCentralValidateOtpResponse;
import com.matrimony.enums.MessageCentralCode;
import com.matrimony.exception.OtpSendFailedException;
import com.matrimony.exception.OtpValidationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class MessageCentralClient {

    private static final Logger log = LoggerFactory.getLogger(MessageCentralClient.class);

    @Value("${messagecentral.base-url}")
    private String baseUrl;

    @Value("${messagecentral.auth-token}")
    private String authToken;

    private final RestTemplate restTemplate;

    public MessageCentralClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("authToken", authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /* ========================= SEND OTP ========================= */

    public MessageCentralSendOtpResponse sendOtp(String countryCode, String mobileNumber) {

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/verification/v3/send")
                .queryParam("countryCode", countryCode)
                .queryParam("flowType", "SMS")
                .queryParam("mobileNumber", mobileNumber)
                .toUriString();

        log.info("Send OTP to mobile: {}", mobileNumber);

        try {
            ResponseEntity<MessageCentralSendOtpResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST,
                    new HttpEntity<>(headers()),
                    MessageCentralSendOtpResponse.class);

            return response.getBody();

        } catch (HttpClientErrorException ex) {

            String body = ex.getResponseBodyAsString();
            log.error("Send OTP error={}", body);

            MessageCentralSendOtpResponse errorResponse =
                    MessageCentralErrorParser.parse(body, MessageCentralSendOtpResponse.class);

            MessageCentralCode code =
                    MessageCentralCode.fromCode(String.valueOf(errorResponse.getResponseCode()));

            switch (code) {
                case REQUEST_ALREADY_EXISTS:
                case MAXIMUM_LIMIT_REACHED:
                case INVALID_COUNTRY_CODE:
                case DUPLICATE_RESOURCE:
                case BAD_REQUEST:
                case INVALID_CUSTOMER_ID:
                    return errorResponse;

                case SERVER_ERROR:
                default:
                    throw new OtpSendFailedException("OTP_SEND_FAILED: " + code.name(), code.getCode());
            }
        }
    }

    /* ========================= VALIDATE OTP ========================= */

    public MessageCentralValidateOtpResponse validateOtp(String verificationId, String code) {

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/verification/v3/validateOtp")
                .queryParam("verificationId", verificationId)
                .queryParam("code", code)
                .toUriString();

        log.info("Validate OTP for verificationId: {}", verificationId);

        try {
            ResponseEntity<MessageCentralValidateOtpResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET,
                    new HttpEntity<>(headers()),
                    MessageCentralValidateOtpResponse.class);

            MessageCentralValidateOtpResponse responseBody = response.getBody();

            // Extract phone number from the data map if available
            if (responseBody != null && responseBody.getData() != null) {
                responseBody.setPhoneNumber(responseBody.getData().getMobileNumber());
            }

            return responseBody;

        } catch (HttpClientErrorException ex) {

            String body = ex.getResponseBodyAsString();
            log.error("Validate OTP error={}", body);

            MessageCentralValidateOtpResponse errorResponse =
                    MessageCentralErrorParser.parse(body, MessageCentralValidateOtpResponse.class);

            MessageCentralCode errorCode =
                    MessageCentralCode.fromCode(String.valueOf(errorResponse.getResponseCode()));

            switch (errorCode) {
                case REQUEST_ALREADY_EXISTS:
                case MAXIMUM_LIMIT_REACHED:
                case INVALID_COUNTRY_CODE:
                case DUPLICATE_RESOURCE:
                case BAD_REQUEST:
                case INVALID_CUSTOMER_ID:
                case INVALID_VERIFICATION_ID:
                case VERIFICATION_FAILED:
                case WRONG_OTP_PROVIDED:
                case ALREADY_VERIFIED:
                case VERIFICATION_EXPIRED:
                    return errorResponse;

                case SERVER_ERROR:
                default:
                    throw new OtpValidationFailedException(
                            "OTP_VALIDATION_FAILED: " + errorCode.name(), errorCode.getCode());
            }
        }
    }
}
