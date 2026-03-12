package com.matrimony.controller;

import com.matrimony.dto.ApiResponse;
import com.matrimony.dto.FcmTokenRequest;
import com.matrimony.entity.User;
import com.matrimony.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DeviceController {

    private final UserRepository userRepository;

    /**
     * Register or refresh the FCM device token for the authenticated user.
     * Called by the Flutter app on startup / token refresh.
     */
    @PostMapping("/fcm-token")
    public ResponseEntity<ApiResponse<Void>> registerFcmToken(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody FcmTokenRequest request) {

        userRepository.findById(currentUser.getId()).ifPresent(user -> {
            user.setFcmToken(request.getFcmToken());
            userRepository.save(user);
        });

        return ResponseEntity.ok(ApiResponse.success("FCM token registered", null));
    }
}
