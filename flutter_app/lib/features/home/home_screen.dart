import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/api/api_client.dart';
import '../../core/theme/app_theme.dart';

class HomeScreen extends ConsumerStatefulWidget {
  const HomeScreen({super.key});

  @override
  ConsumerState<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends ConsumerState<HomeScreen> {
  Map<String, dynamic>? _profile;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadProfile();
  }

  Future<void> _loadProfile() async {
    try {
      final response = await ref.read(apiClientProvider).getMyProfile();
      setState(() {
        _profile = response.data['data'];
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(Icons.favorite, color: AppTheme.primary, size: 22),
            SizedBox(width: 6),
            Text('Matrimony'),
          ],
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications_outlined),
            onPressed: () => context.go('/notifications'),
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _loadProfile,
              child: SingleChildScrollView(
                physics: const AlwaysScrollableScrollPhysics(),
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // Welcome card
                    _WelcomeCard(profile: _profile),
                    const SizedBox(height: 20),

                    // Quick actions
                    Text('Quick Actions',
                        style: Theme.of(context).textTheme.titleMedium?.copyWith(fontWeight: FontWeight.w700)),
                    const SizedBox(height: 12),
                    Row(
                      children: [
                        _QuickActionCard(
                          icon: Icons.search,
                          label: 'Find Matches',
                          color: AppTheme.primary,
                          onTap: () => context.go('/matches'),
                        ),
                        const SizedBox(width: 12),
                        _QuickActionCard(
                          icon: Icons.favorite,
                          label: 'Interests',
                          color: Colors.orange,
                          onTap: () => context.go('/matches'),
                        ),
                        const SizedBox(width: 12),
                        _QuickActionCard(
                          icon: Icons.star,
                          label: 'Go Premium',
                          color: Colors.amber[700]!,
                          onTap: () => context.go('/subscription'),
                        ),
                      ],
                    ),

                    // Profile completion
                    if (_profile != null) ...[
                      const SizedBox(height: 20),
                      _ProfileCompletionCard(profile: _profile!),
                    ],
                  ],
                ),
              ),
            ),
    );
  }
}

class _WelcomeCard extends StatelessWidget {
  final Map<String, dynamic>? profile;
  const _WelcomeCard({this.profile});

  @override
  Widget build(BuildContext context) {
    final name = profile?['name'] ?? 'User';
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [AppTheme.primary, AppTheme.primaryDark],
        ),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Row(
        children: [
          CircleAvatar(
            radius: 28,
            backgroundColor: Colors.white.withOpacity(0.3),
            child: Text(
              name.isNotEmpty ? name[0].toUpperCase() : 'U',
              style: const TextStyle(color: Colors.white, fontSize: 24, fontWeight: FontWeight.w700),
            ),
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('Welcome, $name! 👋',
                    style: const TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.w700)),
                const SizedBox(height: 4),
                const Text('Find your perfect life partner',
                    style: TextStyle(color: Colors.white70, fontSize: 13)),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _QuickActionCard extends StatelessWidget {
  final IconData icon;
  final String label;
  final Color color;
  final VoidCallback onTap;

  const _QuickActionCard({
    required this.icon,
    required this.label,
    required this.color,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: GestureDetector(
        onTap: onTap,
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 16),
          decoration: BoxDecoration(
            color: color.withOpacity(0.1),
            borderRadius: BorderRadius.circular(12),
            border: Border.all(color: color.withOpacity(0.3)),
          ),
          child: Column(
            children: [
              Icon(icon, color: color, size: 28),
              const SizedBox(height: 6),
              Text(label, style: TextStyle(color: color, fontSize: 12, fontWeight: FontWeight.w600), textAlign: TextAlign.center),
            ],
          ),
        ),
      ),
    );
  }
}

class _ProfileCompletionCard extends StatelessWidget {
  final Map<String, dynamic> profile;
  const _ProfileCompletionCard({required this.profile});

  @override
  Widget build(BuildContext context) {
    final completion = (profile['profileCompletion'] as num?)?.toInt() ?? 0;
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text('Profile Completion', style: TextStyle(fontWeight: FontWeight.w700)),
                Text('$completion%', style: const TextStyle(color: AppTheme.primary, fontWeight: FontWeight.w700)),
              ],
            ),
            const SizedBox(height: 8),
            LinearProgressIndicator(
              value: completion / 100.0,
              backgroundColor: AppTheme.divider,
              valueColor: const AlwaysStoppedAnimation<Color>(AppTheme.primary),
              minHeight: 8,
              borderRadius: BorderRadius.circular(4),
            ),
            if (completion < 100) ...[
              const SizedBox(height: 8),
              const Text('Complete your profile to get more matches!',
                  style: TextStyle(color: AppTheme.textSecondary, fontSize: 12)),
            ],
          ],
        ),
      ),
    );
  }
}
