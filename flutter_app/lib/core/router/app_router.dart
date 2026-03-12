import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../features/auth/screens/phone_login_screen.dart';
import '../../features/auth/screens/otp_verification_screen.dart';
import '../../features/auth/screens/splash_screen.dart';
import '../../features/chat/screens/chat_list_screen.dart';
import '../../features/chat/screens/chat_screen.dart';
import '../../features/home/main_navigation_screen.dart';
import '../../features/matches/screens/matches_screen.dart';
import '../../features/notifications/notifications_screen.dart';
import '../../features/onboarding/screens/basic_details_screen.dart';
import '../../features/onboarding/screens/education_details_screen.dart';
import '../../features/onboarding/screens/lifestyle_screen.dart';
import '../../features/onboarding/screens/partner_preferences_screen.dart';
import '../../features/onboarding/screens/photo_upload_screen.dart';
import '../../features/onboarding/screens/religion_details_screen.dart';
import '../../features/profile/screens/profile_screen.dart';
import '../../features/profile/screens/view_profile_screen.dart';
import '../../features/subscription/screens/subscription_screen.dart';

final appRouterProvider = Provider<GoRouter>((ref) {
  return GoRouter(
    initialLocation: '/splash',
    routes: [
      GoRoute(
        path: '/splash',
        builder: (context, state) => const SplashScreen(),
      ),
      GoRoute(
        path: '/login',
        builder: (context, state) => const PhoneLoginScreen(),
      ),
      GoRoute(
        path: '/verify-otp',
        builder: (context, state) {
          final phone = state.uri.queryParameters['phone'] ?? '';
          return OtpVerificationScreen(phone: phone);
        },
      ),
      // ── Onboarding ──────────────────────────────────────────────────────
      GoRoute(
        path: '/onboarding/basic',
        builder: (context, state) => const BasicDetailsScreen(),
      ),
      GoRoute(
        path: '/onboarding/religion',
        builder: (context, state) => const ReligionDetailsScreen(),
      ),
      GoRoute(
        path: '/onboarding/education',
        builder: (context, state) => const EducationDetailsScreen(),
      ),
      GoRoute(
        path: '/onboarding/lifestyle',
        builder: (context, state) => const LifestyleScreen(),
      ),
      GoRoute(
        path: '/onboarding/preferences',
        builder: (context, state) => const PartnerPreferencesScreen(),
      ),
      GoRoute(
        path: '/onboarding/photo',
        builder: (context, state) => const PhotoUploadScreen(),
      ),
      // ── Main App ─────────────────────────────────────────────────────────
      GoRoute(
        path: '/home',
        builder: (context, state) => const MainNavigationScreen(),
      ),
      GoRoute(
        path: '/matches',
        builder: (context, state) => const MatchesScreen(),
      ),
      GoRoute(
        path: '/chats',
        builder: (context, state) => const ChatListScreen(),
      ),
      GoRoute(
        path: '/chat/:chatId',
        builder: (context, state) {
          final chatId = int.parse(state.pathParameters['chatId']!);
          final otherUserName =
              state.uri.queryParameters['name'] ?? 'Chat';
          return ChatScreen(chatId: chatId, otherUserName: otherUserName);
        },
      ),
      GoRoute(
        path: '/profile',
        builder: (context, state) => const ProfileScreen(),
      ),
      GoRoute(
        path: '/profile/:userId',
        builder: (context, state) {
          final userId = int.parse(state.pathParameters['userId']!);
          return ViewProfileScreen(userId: userId);
        },
      ),
      GoRoute(
        path: '/subscription',
        builder: (context, state) => const SubscriptionScreen(),
      ),
      GoRoute(
        path: '/notifications',
        builder: (context, state) => const NotificationsScreen(),
      ),
    ],
    errorBuilder: (context, state) => Scaffold(
      body: Center(
        child: Text('Page not found: ${state.uri}'),
      ),
    ),
  );
});
