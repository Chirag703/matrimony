package com.matrimony.controller;

import com.matrimony.dto.ApiResponse;
import com.matrimony.dto.AuthResponse;
import com.matrimony.dto.MessageCentralSendOtpResponse;
import com.matrimony.dto.SendOtpRequest;
import com.matrimony.dto.VerifyOtpRequest;
import com.matrimony.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    /**
     * Step 1: Send OTP to phone number via MessageCentral.
     * Returns a verificationId to be used in the validate-otp step.
     */
    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<MessageCentralSendOtpResponse>> sendOtp(
            @Valid @RequestBody SendOtpRequest request) {
        MessageCentralSendOtpResponse response = authService.sendOtp(request);
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully", response));
    }

    /**
     * Step 2: Validate OTP using verificationId + code from MessageCentral.
     * Returns a JWT token on success.
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        AuthResponse response = authService.verifyOtp(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // JWT is stateless; client should discard the token
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }
}
