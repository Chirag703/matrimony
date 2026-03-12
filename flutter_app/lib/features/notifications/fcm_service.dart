import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/api/api_client.dart';

final fcmServiceProvider = Provider<FcmService>((ref) {
  return FcmService(ref.read(apiClientProvider));
});

/// Handles Firebase Cloud Messaging for the Flutter app.
/// - Requests permission on iOS
/// - Obtains the device FCM token and registers it with the backend
/// - Shows local notifications when the app is in the foreground
class FcmService {
  final ApiClient _apiClient;
  final FirebaseMessaging _messaging = FirebaseMessaging.instance;
  final FlutterLocalNotificationsPlugin _localNotifications =
      FlutterLocalNotificationsPlugin();

  FcmService(this._apiClient);

  Future<void> initialize() async {
    // 1. Request notification permissions (iOS + Android 13+)
    await _messaging.requestPermission(
      alert: true,
      badge: true,
      sound: true,
      provisional: false,
    );

    // 2. Configure local notifications (for foreground messages)
    const androidInit =
        AndroidInitializationSettings('@mipmap/ic_launcher');
    const iosInit = DarwinInitializationSettings();
    await _localNotifications.initialize(
      const InitializationSettings(android: androidInit, iOS: iosInit),
    );

    // 3. Create Android notification channel
    const channel = AndroidNotificationChannel(
      'matrimony_high_importance',
      'Matrimony Notifications',
      description: 'Notifications for interests, messages and matches.',
      importance: Importance.high,
    );
    await _localNotifications
        .resolvePlatformSpecificImplementation<
            AndroidFlutterLocalNotificationsPlugin>()
        ?.createNotificationChannel(channel);

    // 4. Register FCM token with backend
    await refreshAndRegisterToken();

    // 5. Listen for token refreshes
    _messaging.onTokenRefresh.listen((newToken) async {
      debugPrint('FCM token refreshed: $newToken');
      await _registerTokenWithBackend(newToken);
    });

    // 6. Handle foreground messages → show local notification
    FirebaseMessaging.onMessage.listen(_handleForegroundMessage);

    // 7. Handle messages that opened the app from background/terminated
    FirebaseMessaging.onMessageOpenedApp.listen(_handleMessageOpenedApp);

    // 8. Check if the app was launched from a terminated state via notification
    final initialMessage = await _messaging.getInitialMessage();
    if (initialMessage != null) {
      _handleMessageOpenedApp(initialMessage);
    }
  }

  Future<void> refreshAndRegisterToken() async {
    try {
      final token = await _messaging.getToken();
      if (token != null) {
        debugPrint('FCM token: $token');
        await _registerTokenWithBackend(token);
      }
    } catch (e) {
      debugPrint('Error getting FCM token: $e');
    }
  }

  Future<void> _registerTokenWithBackend(String token) async {
    try {
      await _apiClient.registerFcmToken(token);
    } catch (e) {
      debugPrint('Failed to register FCM token with backend: $e');
    }
  }

  void _handleForegroundMessage(RemoteMessage message) {
    final notification = message.notification;
    if (notification == null) return;

    _localNotifications.show(
      notification.hashCode,
      notification.title,
      notification.body,
      NotificationDetails(
        android: AndroidNotificationDetails(
          'matrimony_high_importance',
          'Matrimony Notifications',
          channelDescription:
              'Notifications for interests, messages and matches.',
          importance: Importance.high,
          priority: Priority.high,
          icon: '@mipmap/ic_launcher',
        ),
        iOS: const DarwinNotificationDetails(),
      ),
    );
  }

  void _handleMessageOpenedApp(RemoteMessage message) {
    debugPrint('Notification opened app: ${message.data}');
    // Navigation based on notification type can be added here
    // e.g., navigate to chat if type == 'MESSAGE'
  }
}
