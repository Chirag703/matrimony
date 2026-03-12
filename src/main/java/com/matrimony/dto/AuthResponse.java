package com.matrimony.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private boolean onboardingComplete;
    private boolean newUser;

    public AuthResponse(String token, Long userId, boolean onboardingComplete, boolean newUser) {
        this.token = token;
        this.userId = userId;
        this.onboardingComplete = onboardingComplete;
        this.newUser = newUser;
    }
}
