import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/api/api_client.dart';
import '../../../core/theme/app_theme.dart';

class ViewProfileScreen extends ConsumerStatefulWidget {
  final int userId;
  const ViewProfileScreen({super.key, required this.userId});

  @override
  ConsumerState<ViewProfileScreen> createState() => _ViewProfileScreenState();
}

class _ViewProfileScreenState extends ConsumerState<ViewProfileScreen> {
  Map<String, dynamic>? _profile;
  bool _isLoading = true;
  bool _interestSent = false;

  @override
  void initState() {
    super.initState();
    _loadProfile();
  }

  Future<void> _loadProfile() async {
    try {
      final response = await ref.read(apiClientProvider).getUserProfile(widget.userId);
      setState(() {
        _profile = response.data['data'];
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _sendInterest() async {
    try {
      await ref.read(apiClientProvider).sendInterest(widget.userId);
      setState(() => _interestSent = true);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Interest sent! ❤️'), backgroundColor: AppTheme.success),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('$e'), backgroundColor: AppTheme.error),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      extendBodyBehindAppBar: true,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        foregroundColor: Colors.white,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _profile == null
              ? const Center(child: Text('Profile not found'))
              : Stack(
                  children: [
                    CustomScrollView(
                      slivers: [
                        SliverToBoxAdapter(
                          child: Container(
                            height: 360,
                            color: AppTheme.primary.withOpacity(0.1),
                            child: _profile!['photoUrl'] != null
                                ? CachedNetworkImage(
                                    imageUrl: _profile!['photoUrl'],
                                    fit: BoxFit.cover,
                                    errorWidget: (_, __, ___) => Center(
                                      child: Text(
                                        (_profile!['name'] ?? 'U')[0].toUpperCase(),
                                        style: const TextStyle(fontSize: 80, fontWeight: FontWeight.w700, color: AppTheme.primary),
                                      ),
                                    ),
                                  )
                                : Center(
                                    child: Text(
                                      (_profile!['name'] ?? 'U')[0].toUpperCase(),
                                      style: const TextStyle(fontSize: 80, fontWeight: FontWeight.w700, color: AppTheme.primary),
                                    ),
                                  ),
                          ),
                        ),
                        SliverToBoxAdapter(
                          child: Padding(
                            padding: const EdgeInsets.all(20),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Row(
                                  children: [
                                    Expanded(
                                      child: Text(
                                        '${_profile!['name'] ?? 'Unknown'}${_profile!['age'] != null ? ', ${_profile!['age']}' : ''}',
                                        style: const TextStyle(fontSize: 24, fontWeight: FontWeight.w700),
                                      ),
                                    ),
                                    if (_profile!['verified'] == true)
                                      const Icon(Icons.verified, color: AppTheme.primary, size: 24),
                                  ],
                                ),
                                if (_profile!['city'] != null) ...[
                                  const SizedBox(height: 4),
                                  Row(
                                    children: [
                                      const Icon(Icons.location_on, size: 16, color: AppTheme.textSecondary),
                                      Text(_profile!['city'], style: const TextStyle(color: AppTheme.textSecondary)),
                                    ],
                                  ),
                                ],
                                const Divider(height: 32),
                                _infoGrid(),
                                if (_profile!['about'] != null) ...[
                                  const Divider(height: 32),
                                  const Text('About', style: TextStyle(fontSize: 16, fontWeight: FontWeight.w700)),
                                  const SizedBox(height: 8),
                                  Text(_profile!['about'], style: const TextStyle(color: AppTheme.textSecondary)),
                                ],
                                const SizedBox(height: 100),
                              ],
                            ),
                          ),
                        ),
                      ],
                    ),
                    // Bottom action button
                    Positioned(
                      left: 16, right: 16, bottom: 24,
                      child: ElevatedButton.icon(
                        onPressed: _interestSent ? null : _sendInterest,
                        icon: Icon(_interestSent ? Icons.favorite : Icons.favorite_outline),
                        label: Text(_interestSent ? 'Interest Sent ✓' : 'Send Interest'),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: _interestSent ? AppTheme.success : AppTheme.primary,
                        ),
                      ),
                    ),
                  ],
                ),
    );
  }

  Widget _infoGrid() {
    final items = [
      {'icon': Icons.church, 'label': 'Religion', 'value': _profile!['religion']},
      {'icon': Icons.school, 'label': 'Education', 'value': _profile!['education']},
      {'icon': Icons.work, 'label': 'Occupation', 'value': _profile!['occupation']},
      {'icon': Icons.height, 'label': 'Height', 'value': _profile!['height']},
      {'icon': Icons.favorite, 'label': 'Status', 'value': _profile!['maritalStatus']},
      {'icon': Icons.groups, 'label': 'Caste', 'value': _profile!['caste']},
    ].where((e) => e['value'] != null).toList();

    return Wrap(
      spacing: 8,
      runSpacing: 8,
      children: items.map((item) => Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        decoration: BoxDecoration(
          color: AppTheme.primary.withOpacity(0.08),
          borderRadius: BorderRadius.circular(20),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(item['icon'] as IconData, size: 14, color: AppTheme.primary),
            const SizedBox(width: 4),
            Text('${item['value']}', style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600)),
          ],
        ),
      )).toList(),
    );
  }
}
