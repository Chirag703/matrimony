package com.matrimony.controller;

import com.matrimony.dto.ApiResponse;
import com.matrimony.dto.SubscriptionDto;
import com.matrimony.dto.SubscriptionRequest;
import com.matrimony.entity.User;
import com.matrimony.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<SubscriptionDto>> subscribe(
            @AuthenticationPrincipal User currentUser,
            @RequestBody SubscriptionRequest request) {
        SubscriptionDto subscription = subscriptionService.subscribe(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Subscription activated", subscription));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<SubscriptionDto>> getActiveSubscription(
            @AuthenticationPrincipal User currentUser) {
        SubscriptionDto subscription = subscriptionService.getActiveSubscription(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(subscription));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<SubscriptionDto>>> getHistory(
            @AuthenticationPrincipal User currentUser) {
        List<SubscriptionDto> history = subscriptionService.getSubscriptionHistory(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
