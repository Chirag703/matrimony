package com.matrimony.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SendOtpRequest {

    @NotBlank(message = "Country code is required")
    private String countryCode;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{5,15}$", message = "Invalid phone number format")
    private String phone;
}
