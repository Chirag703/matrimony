package com.matrimony.service;

import com.matrimony.client.MessageCentralClient;
import com.matrimony.dto.AuthResponse;
import com.matrimony.dto.MessageCentralSendOtpResponse;
import com.matrimony.dto.MessageCentralValidateOtpResponse;
import com.matrimony.dto.SendOtpRequest;
import com.matrimony.dto.VerifyOtpRequest;
import com.matrimony.entity.User;
import com.matrimony.repository.UserRepository;
import com.matrimony.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final MessageCentralClient messageCentralClient;

    @Transactional
    public MessageCentralSendOtpResponse sendOtp(SendOtpRequest request) {
        String phone = request.getPhone();
        String countryCode = request.getCountryCode();
        log.info("Sending OTP via MessageCentral for phone: {}", phone);
        return messageCentralClient.sendOtp(countryCode, phone);
    }

    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        String verificationId = request.getVerificationId();
        String code = request.getCode();

        log.info("Validating OTP via MessageCentral for verificationId: {}", verificationId);
        MessageCentralValidateOtpResponse otpResponse =
                messageCentralClient.validateOtp(verificationId, code);

        if (!isOtpVerified(otpResponse)) {
            throw new IllegalArgumentException("OTP verification failed: "
                    + (otpResponse != null ? otpResponse.getMessage() : "null response"));
        }

        String phone = resolvePhoneNumber(otpResponse);

        boolean newUser = !userRepository.existsByPhone(phone);
        User user;

        if (newUser) {
            user = new User();
            user.setPhone(phone);
            user.setPremium(false);
            user.setBanned(false);
            user.setOnboardingComplete(false);
            user = userRepository.save(user);
        } else {
            user = userRepository.findByPhone(phone)
                    .orElseThrow(() -> new IllegalStateException("User not found"));
            if (user.getBanned()) {
                throw new IllegalStateException("Account has been banned");
            }
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getPhone());
        return new AuthResponse(token, user.getId(), user.getOnboardingComplete(), newUser);
    }

    private boolean isOtpVerified(MessageCentralValidateOtpResponse response) {
        if (response == null || response.getResponseCode() == null || response.getResponseCode() != 200) {
            return false;
        }

        if (response.getData() == null) {
            return false;
        }

        String status = response.getData().getVerificationStatus();
        if (status == null || status.isBlank()) {
            return false;
        }

        return "VERIFIED".equalsIgnoreCase(status)
                || "VERIFICATION_SUCCESS".equalsIgnoreCase(status)
                || "VERIFICATION_COMPLETED".equalsIgnoreCase(status)
                || "SUCCESS".equalsIgnoreCase(status);
    }

    private String resolvePhoneNumber(MessageCentralValidateOtpResponse response) {
        if (response.getPhoneNumber() != null && !response.getPhoneNumber().isBlank()) {
            return response.getPhoneNumber();
        }
        if (response.getData() != null && response.getData().getMobileNumber() != null) {
            return response.getData().getMobileNumber();
        }
        throw new IllegalStateException("Phone number could not be resolved from OTP validation response");
    }
}

