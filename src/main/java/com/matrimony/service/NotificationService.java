package com.matrimony.service;

import com.matrimony.dto.NotificationDto;
import com.matrimony.entity.Notification;
import com.matrimony.entity.User;
import com.matrimony.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FcmService fcmService;

    public List<NotificationDto> getNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadStatusFalse(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            if (notification.getUser().getId().equals(userId)) {
                notification.setReadStatus(true);
                notificationRepository.save(notification);
            }
        });
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.findByUserIdAndReadStatusFalse(userId)
                .forEach(notification -> {
                    notification.setReadStatus(true);
                    notificationRepository.save(notification);
                });
    }

    /**
     * Persist an in-app notification and push it via FCM to the user's device.
     */
    @Transactional
    public void sendNotification(User user, String title, String message, String type) {
        // 1. Persist in-app notification
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notificationRepository.save(notification);

        // 2. Push FCM notification to the user's device
        if (user.getFcmToken() != null && !user.getFcmToken().isBlank()) {
            fcmService.sendNotification(
                    user.getFcmToken(),
                    title,
                    message,
                    Map.of("type", type)
            );
        }
    }

    private NotificationDto toDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setReadStatus(notification.getReadStatus());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
