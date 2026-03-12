package com.matrimony.controller;

import com.matrimony.dto.ApiResponse;
import com.matrimony.dto.AuthResponse;
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
public class AuthController {

    private final AuthService authService;

    /**
     * Step 1: Send OTP to phone number
     */
    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<Void>> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        authService.sendOtp(request);
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully", null));
    }

    /**
     * Step 2: Verify OTP and get JWT token
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
