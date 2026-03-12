package com.matrimony.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service for sending FCM push notifications to Flutter app clients.
 * If Firebase is not configured (no service-account key), all send operations
 * are no-ops and the application continues to function normally.
 */
@Service
@Slf4j
public class FcmService {

    @Nullable
    private final FirebaseApp firebaseApp;

    public FcmService(@Autowired(required = false) FirebaseApp firebaseApp) {
        this.firebaseApp = firebaseApp;
    }

    /**
     * Send a push notification to a single device via its FCM token.
     *
     * @param fcmToken device token registered by the Flutter app
     * @param title    notification title
     * @param body     notification body
     * @param data     optional key-value data payload for the Flutter app
     */
    public void sendNotification(String fcmToken, String title, String body,
                                 Map<String, String> data) {
        if (firebaseApp == null) {
            log.debug("FCM not configured – skipping push notification to token: {}", fcmToken);
            return;
        }
        if (fcmToken == null || fcmToken.isBlank()) {
            log.debug("FCM token is null/blank – skipping push notification");
            return;
        }

        try {
            Message.Builder builder = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setSound("default")
                                    .build())
                            .build());

            if (data != null && !data.isEmpty()) {
                builder.putAllData(data);
            }

            String messageId = FirebaseMessaging.getInstance(firebaseApp).send(builder.build());
            log.info("FCM notification sent successfully. MessageId: {}", messageId);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM notification: {}", e.getMessage());
        }
    }

    /**
     * Send a data-only (silent) push notification.
     */
    public void sendSilentNotification(String fcmToken, Map<String, String> data) {
        if (firebaseApp == null || fcmToken == null || fcmToken.isBlank()) {
            return;
        }
        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .putAllData(data)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .build();

            String messageId = FirebaseMessaging.getInstance(firebaseApp).send(message);
            log.info("FCM silent notification sent. MessageId: {}", messageId);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM silent notification: {}", e.getMessage());
        }
    }
}
