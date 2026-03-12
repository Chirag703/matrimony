import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

const String _baseUrl = 'http://10.0.2.2:8080'; // Android emulator → localhost
// For physical device or production, replace with your server URL:
// const String _baseUrl = 'https://api.yourmatrimonyapp.com';

const String _tokenKey = 'auth_token';

final apiClientProvider = Provider<ApiClient>((ref) => ApiClient());

class ApiClient {
  late final Dio _dio;
  final _storage = const FlutterSecureStorage();

  ApiClient() {
    _dio = Dio(BaseOptions(
      baseUrl: _baseUrl,
      connectTimeout: const Duration(seconds: 30),
      receiveTimeout: const Duration(seconds: 30),
      headers: {'Content-Type': 'application/json'},
    ));

    _dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) async {
        final token = await _storage.read(key: _tokenKey);
        if (token != null) {
          options.headers['Authorization'] = 'Bearer $token';
        }
        return handler.next(options);
      },
      onError: (error, handler) {
        return handler.next(error);
      },
    ));
  }

  Future<void> saveToken(String token) async {
    await _storage.write(key: _tokenKey, value: token);
  }

  Future<String?> getToken() async {
    return await _storage.read(key: _tokenKey);
  }

  Future<void> clearToken() async {
    await _storage.delete(key: _tokenKey);
  }

  // ── Auth ─────────────────────────────────────────────────────────────────

  Future<Response> sendOtp(String phone) async {
    return _dio.post('/api/auth/send-otp', data: {'phone': phone});
  }

  Future<Response> verifyOtp(String phone, String otp) async {
    return _dio.post('/api/auth/verify-otp', data: {'phone': phone, 'otp': otp});
  }

  Future<void> logout() async {
    await _dio.post('/api/auth/logout');
    await clearToken();
  }

  // ── Profile ───────────────────────────────────────────────────────────────

  Future<Response> getMyProfile() async {
    return _dio.get('/api/profile/me');
  }

  Future<Response> getUserProfile(int userId) async {
    return _dio.get('/api/profile/$userId');
  }

  Future<Response> saveBasicInfo(Map<String, dynamic> data) async {
    return _dio.post('/api/profile/basic-info', data: data);
  }

  Future<Response> updateProfile(Map<String, dynamic> data) async {
    return _dio.put('/api/profile/update', data: data);
  }

  Future<Response> getPartnerPreferences() async {
    return _dio.get('/api/profile/preferences');
  }

  Future<Response> updatePartnerPreferences(Map<String, dynamic> data) async {
    return _dio.put('/api/profile/preferences', data: data);
  }

  Future<Response> completeOnboarding() async {
    return _dio.post('/api/profile/complete-onboarding');
  }

  // ── Matches ───────────────────────────────────────────────────────────────

  Future<Response> getMatches({int limit = 10}) async {
    return _dio.get('/api/matches', queryParameters: {'limit': limit});
  }

  // ── Interests ─────────────────────────────────────────────────────────────

  Future<Response> sendInterest(int toUserId) async {
    return _dio.post('/api/interests/send/$toUserId');
  }

  Future<Response> acceptInterest(int interestId) async {
    return _dio.put('/api/interests/$interestId/accept');
  }

  Future<Response> rejectInterest(int interestId) async {
    return _dio.put('/api/interests/$interestId/reject');
  }

  Future<Response> getSentInterests() async {
    return _dio.get('/api/interests/sent');
  }

  Future<Response> getReceivedInterests() async {
    return _dio.get('/api/interests/received');
  }

  Future<Response> getPendingInterests() async {
    return _dio.get('/api/interests/pending');
  }

  // ── Chats ─────────────────────────────────────────────────────────────────

  Future<Response> getChats() async {
    return _dio.get('/api/chats');
  }

  Future<Response> startChat(int otherUserId) async {
    return _dio.post('/api/chats/start/$otherUserId');
  }

  Future<Response> getMessages(int chatId) async {
    return _dio.get('/api/chats/$chatId/messages');
  }

  Future<Response> sendMessage(int chatId, String message) async {
    return _dio.post('/api/chats/$chatId/messages', data: {'message': message});
  }

  // ── Subscriptions ─────────────────────────────────────────────────────────

  Future<Response> subscribe(String plan) async {
    return _dio.post('/api/subscriptions/subscribe', data: {'plan': plan});
  }

  Future<Response> getActiveSubscription() async {
    return _dio.get('/api/subscriptions/active');
  }

  // ── Notifications ─────────────────────────────────────────────────────────

  Future<Response> getNotifications() async {
    return _dio.get('/api/notifications');
  }

  Future<Response> getUnreadCount() async {
    return _dio.get('/api/notifications/unread-count');
  }

  Future<Response> markNotificationRead(int id) async {
    return _dio.put('/api/notifications/$id/read');
  }

  // ── Device / FCM ─────────────────────────────────────────────────────────

  Future<Response> registerFcmToken(String fcmToken) async {
    return _dio.post('/api/device/fcm-token', data: {'fcmToken': fcmToken});
  }
}
