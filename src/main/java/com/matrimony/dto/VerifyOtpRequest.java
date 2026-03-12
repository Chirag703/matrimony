package com.matrimony.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpRequest {

    @NotBlank(message = "Verification ID is required")
    private String verificationId;

    @NotBlank(message = "OTP code is required")
    private String code;
}
