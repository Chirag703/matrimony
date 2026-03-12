package com.matrimony.controller;

import com.matrimony.dto.ApiResponse;
import com.matrimony.dto.InterestDto;
import com.matrimony.entity.Interest.Status;
import com.matrimony.entity.User;
import com.matrimony.service.InterestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
public class InterestController {

    private final InterestService interestService;

    @PostMapping("/send/{toUserId}")
    public ResponseEntity<ApiResponse<InterestDto>> sendInterest(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long toUserId) {
        InterestDto interest = interestService.sendInterest(currentUser.getId(), toUserId);
        return ResponseEntity.ok(ApiResponse.success("Interest sent", interest));
    }

    @PutMapping("/{interestId}/accept")
    public ResponseEntity<ApiResponse<InterestDto>> acceptInterest(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long interestId) {
        InterestDto interest = interestService.respondToInterest(
                interestId, currentUser.getId(), Status.ACCEPTED);
        return ResponseEntity.ok(ApiResponse.success("Interest accepted", interest));
    }

    @PutMapping("/{interestId}/reject")
    public ResponseEntity<ApiResponse<InterestDto>> rejectInterest(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long interestId) {
        InterestDto interest = interestService.respondToInterest(
                interestId, currentUser.getId(), Status.REJECTED);
        return ResponseEntity.ok(ApiResponse.success("Interest rejected", interest));
    }

    @GetMapping("/sent")
    public ResponseEntity<ApiResponse<List<InterestDto>>> getSentInterests(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                interestService.getSentInterests(currentUser.getId())));
    }

    @GetMapping("/received")
    public ResponseEntity<ApiResponse<List<InterestDto>>> getReceivedInterests(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                interestService.getReceivedInterests(currentUser.getId())));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<InterestDto>>> getPendingInterests(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                interestService.getPendingInterests(currentUser.getId())));
    }
}
