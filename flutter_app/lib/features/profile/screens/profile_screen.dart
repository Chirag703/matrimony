import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/api/api_client.dart';
import '../../../core/theme/app_theme.dart';

class ProfileScreen extends ConsumerStatefulWidget {
  const ProfileScreen({super.key});

  @override
  ConsumerState<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends ConsumerState<ProfileScreen> {
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

  Future<void> _logout() async {
    await ref.read(apiClientProvider).logout();
    if (mounted) context.go('/login');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('My Profile'),
        actions: [
          IconButton(
            icon: const Icon(Icons.star, color: Colors.amber),
            onPressed: () => context.go('/subscription'),
            tooltip: 'Go Premium',
          ),
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: _logout,
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : SingleChildScrollView(
              child: Column(
                children: [
                  // Profile Header
                  _ProfileHeader(profile: _profile),
                  // Details
                  _ProfileDetails(profile: _profile),
                  // Edit Button
                  Padding(
                    padding: const EdgeInsets.all(16),
                    child: ElevatedButton.icon(
                      onPressed: () {},
                      icon: const Icon(Icons.edit),
                      label: const Text('Edit Profile'),
                    ),
                  ),
                ],
              ),
            ),
    );
  }
}

class _ProfileHeader extends StatelessWidget {
  final Map<String, dynamic>? profile;
  const _ProfileHeader({this.profile});

  @override
  Widget build(BuildContext context) {
    final name = profile?['name'] ?? 'Your Name';
    final age = profile?['age'];
    final city = profile?['city'] ?? '';
    final isPremium = profile?['premium'] == true;
    final completion = (profile?['profileCompletion'] as num?)?.toInt() ?? 0;

    return Container(
      padding: const EdgeInsets.all(24),
      child: Column(
        children: [
          Stack(
            children: [
              CircleAvatar(
                radius: 56,
                backgroundColor: AppTheme.primary.withOpacity(0.1),
                child: Text(
                  name.isNotEmpty ? name[0].toUpperCase() : 'U',
                  style: const TextStyle(fontSize: 40, fontWeight: FontWeight.w700, color: AppTheme.primary),
                ),
              ),
              Positioned(
                bottom: 0,
                right: 0,
                child: Container(
                  padding: const EdgeInsets.all(4),
                  decoration: const BoxDecoration(
                    color: AppTheme.primary,
                    shape: BoxShape.circle,
                  ),
                  child: const Icon(Icons.camera_alt, size: 16, color: Colors.white),
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text('$name${age != null ? ", $age" : ""}',
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(fontWeight: FontWeight.w700)),
              if (isPremium) ...[
                const SizedBox(width: 8),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                  decoration: BoxDecoration(
                    color: Colors.amber[700],
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: const Text('PREMIUM', style: TextStyle(color: Colors.white, fontSize: 10, fontWeight: FontWeight.w700)),
                ),
              ],
            ],
          ),
          if (city.isNotEmpty) ...[
            const SizedBox(height: 4),
            Text(city, style: const TextStyle(color: AppTheme.textSecondary)),
          ],
          const SizedBox(height: 12),
          // Profile completion
          Row(
            children: [
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text('Profile Completion', style: TextStyle(fontSize: 12)),
                        Text('$completion%', style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: AppTheme.primary)),
                      ],
                    ),
                    const SizedBox(height: 4),
                    LinearProgressIndicator(
                      value: completion / 100.0,
                      backgroundColor: AppTheme.divider,
                      valueColor: const AlwaysStoppedAnimation<Color>(AppTheme.primary),
                      minHeight: 6,
                      borderRadius: BorderRadius.circular(3),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}

class _ProfileDetails extends StatelessWidget {
  final Map<String, dynamic>? profile;
  const _ProfileDetails({this.profile});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: Card(
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text('Profile Details', style: TextStyle(fontWeight: FontWeight.w700, fontSize: 16)),
              const Divider(),
              _DetailRow(icon: Icons.church, label: 'Religion', value: profile?['religion']),
              _DetailRow(icon: Icons.school, label: 'Education', value: profile?['education']),
              _DetailRow(icon: Icons.work, label: 'Occupation', value: profile?['occupation']),
              _DetailRow(icon: Icons.location_on, label: 'City', value: profile?['city']),
              _DetailRow(icon: Icons.height, label: 'Height', value: profile?['height']),
              _DetailRow(icon: Icons.favorite, label: 'Marital Status', value: profile?['maritalStatus']),
              if (profile?['about'] != null) ...[
                const Divider(),
                const Text('About', style: TextStyle(fontWeight: FontWeight.w600)),
                const SizedBox(height: 4),
                Text(profile!['about'], style: const TextStyle(color: AppTheme.textSecondary)),
              ],
            ],
          ),
        ),
      ),
    );
  }
}

class _DetailRow extends StatelessWidget {
  final IconData icon;
  final String label;
  final dynamic value;

  const _DetailRow({required this.icon, required this.label, this.value});

  @override
  Widget build(BuildContext context) {
    if (value == null) return const SizedBox.shrink();
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6),
      child: Row(
        children: [
          Icon(icon, size: 18, color: AppTheme.primary),
          const SizedBox(width: 10),
          Text('$label: ', style: const TextStyle(color: AppTheme.textSecondary, fontSize: 13)),
          Text('$value', style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 13)),
        ],
      ),
    );
  }
}
