package com.matrimony.controller;

import com.matrimony.dto.ApiResponse;
import com.matrimony.dto.NotificationDto;
import com.matrimony.entity.User;
import com.matrimony.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getNotifications(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getNotifications(currentUser.getId())));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @AuthenticationPrincipal User currentUser) {
        long count = notificationService.getUnreadCount(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("count", count)));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        notificationService.markAsRead(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Marked as read", null));
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal User currentUser) {
        notificationService.markAllAsRead(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }
}
