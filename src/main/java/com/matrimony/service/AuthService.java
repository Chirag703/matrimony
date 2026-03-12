package com.matrimony.service;

import com.matrimony.dto.AuthResponse;
import com.matrimony.dto.SendOtpRequest;
import com.matrimony.dto.VerifyOtpRequest;
import com.matrimony.entity.OtpStore;
import com.matrimony.entity.User;
import com.matrimony.repository.OtpStoreRepository;
import com.matrimony.repository.UserRepository;
import com.matrimony.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final OtpStoreRepository otpStoreRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void sendOtp(SendOtpRequest request) {
        String phone = request.getPhone();
        String otp = generateOtp();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

        OtpStore otpStore = new OtpStore();
        otpStore.setPhone(phone);
        otpStore.setOtp(otp);
        otpStore.setExpiresAt(expiresAt);
        otpStore.setUsed(false);
        otpStoreRepository.save(otpStore);

        // In production, integrate with SMS gateway (Twilio, AWS SNS, etc.)
        log.info("OTP for phone {}: {} (expires at {})", phone, otp, expiresAt);
    }

    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        String phone = request.getPhone();
        String otp = request.getOtp();

        OtpStore otpStore = otpStoreRepository
                .findTopByPhoneAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        phone, LocalDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("OTP expired or not found"));

        if (!otpStore.getOtp().equals(otp)) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        otpStore.setUsed(true);
        otpStoreRepository.save(otpStore);

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

    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}
