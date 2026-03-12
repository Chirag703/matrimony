import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/api/api_client.dart';
import '../../../core/theme/app_theme.dart';

class MatchesScreen extends ConsumerStatefulWidget {
  const MatchesScreen({super.key});

  @override
  ConsumerState<MatchesScreen> createState() => _MatchesScreenState();
}

class _MatchesScreenState extends ConsumerState<MatchesScreen> {
  List<dynamic> _matches = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadMatches();
  }

  Future<void> _loadMatches() async {
    setState(() => _isLoading = true);
    try {
      final response = await ref.read(apiClientProvider).getMatches(limit: 20);
      setState(() {
        _matches = response.data['data'] as List? ?? [];
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
        title: const Text('Matches'),
        actions: [
          IconButton(
            icon: const Icon(Icons.filter_list),
            onPressed: () {},
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _loadMatches,
              child: _matches.isEmpty
                  ? const Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Icon(Icons.people_outline, size: 64, color: AppTheme.textSecondary),
                          SizedBox(height: 16),
                          Text('No matches found', style: TextStyle(color: AppTheme.textSecondary, fontSize: 16)),
                          SizedBox(height: 8),
                          Text('Complete your profile to discover matches',
                              style: TextStyle(color: AppTheme.textSecondary)),
                        ],
                      ),
                    )
                  : GridView.builder(
                      padding: const EdgeInsets.all(16),
                      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                        crossAxisCount: 2,
                        childAspectRatio: 0.72,
                        crossAxisSpacing: 12,
                        mainAxisSpacing: 12,
                      ),
                      itemCount: _matches.length,
                      itemBuilder: (context, index) =>
                          _MatchCard(match: _matches[index]),
                    ),
            ),
    );
  }
}

class _MatchCard extends ConsumerWidget {
  final Map<String, dynamic> match;
  const _MatchCard({required this.match});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final name = match['name'] ?? 'Unknown';
    final age = match['age'];
    final city = match['city'] ?? '';
    final religion = match['religion'] ?? '';
    final photoUrl = match['photoUrl'] as String?;
    final isPremium = match['premium'] == true;
    final userId = match['userId'];

    return GestureDetector(
      onTap: () => context.go('/profile/$userId'),
      child: Card(
        clipBehavior: Clip.antiAlias,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Photo
            Expanded(
              child: Stack(
                fit: StackFit.expand,
                children: [
                  photoUrl != null
                      ? CachedNetworkImage(
                          imageUrl: photoUrl,
                          fit: BoxFit.cover,
                          placeholder: (_, __) => Container(color: AppTheme.divider),
                          errorWidget: (_, __, ___) => _PlaceholderAvatar(name: name),
                        )
                      : _PlaceholderAvatar(name: name),
                  if (isPremium)
                    Positioned(
                      top: 8,
                      right: 8,
                      child: Container(
                        padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                        decoration: BoxDecoration(
                          color: Colors.amber[700],
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: const Text('★', style: TextStyle(color: Colors.white, fontSize: 10)),
                      ),
                    ),
                ],
              ),
            ),
            // Info
            Padding(
              padding: const EdgeInsets.all(10),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('$name${age != null ? ', $age' : ''}',
                      style: const TextStyle(fontWeight: FontWeight.w700, fontSize: 13),
                      maxLines: 1, overflow: TextOverflow.ellipsis),
                  const SizedBox(height: 2),
                  if (city.isNotEmpty)
                    Text(city, style: const TextStyle(color: AppTheme.textSecondary, fontSize: 11),
                        maxLines: 1, overflow: TextOverflow.ellipsis),
                  if (religion.isNotEmpty)
                    Text(religion, style: const TextStyle(color: AppTheme.textSecondary, fontSize: 11)),
                  const SizedBox(height: 8),
                  // Send interest button
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton.icon(
                      onPressed: () async {
                        try {
                          await ref.read(apiClientProvider).sendInterest(userId);
                          ScaffoldMessenger.of(context).showSnackBar(
                            const SnackBar(content: Text('Interest sent! ❤️'), backgroundColor: AppTheme.success),
                          );
                        } catch (_) {
                          ScaffoldMessenger.of(context).showSnackBar(
                            const SnackBar(content: Text('Interest already sent'), backgroundColor: AppTheme.error),
                          );
                        }
                      },
                      icon: const Icon(Icons.favorite_outline, size: 14),
                      label: const Text('Interest', style: TextStyle(fontSize: 11)),
                      style: ElevatedButton.styleFrom(
                        padding: const EdgeInsets.symmetric(vertical: 6),
                        minimumSize: Size.zero,
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _PlaceholderAvatar extends StatelessWidget {
  final String name;
  const _PlaceholderAvatar({required this.name});

  @override
  Widget build(BuildContext context) {
    return Container(
      color: AppTheme.primary.withOpacity(0.1),
      child: Center(
        child: Text(
          name.isNotEmpty ? name[0].toUpperCase() : '?',
          style: const TextStyle(fontSize: 48, fontWeight: FontWeight.w700, color: AppTheme.primary),
        ),
      ),
    );
  }
}
