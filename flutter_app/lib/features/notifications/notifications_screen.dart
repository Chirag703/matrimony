import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/api/api_client.dart';
import '../../core/theme/app_theme.dart';

class NotificationsScreen extends ConsumerStatefulWidget {
  const NotificationsScreen({super.key});

  @override
  ConsumerState<NotificationsScreen> createState() => _NotificationsScreenState();
}

class _NotificationsScreenState extends ConsumerState<NotificationsScreen> {
  List<dynamic> _notifications = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadNotifications();
  }

  Future<void> _loadNotifications() async {
    try {
      final response = await ref.read(apiClientProvider).getNotifications();
      // Mark all as read
      await ref.read(apiClientProvider).getUnreadCount();
      setState(() {
        _notifications = response.data['data'] as List? ?? [];
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);
    }
  }

  IconData _getIcon(String? type) {
    switch (type) {
      case 'INTEREST':
        return Icons.favorite;
      case 'INTEREST_ACCEPTED':
        return Icons.favorite;
      case 'MESSAGE':
        return Icons.chat_bubble;
      default:
        return Icons.notifications;
    }
  }

  Color _getColor(String? type) {
    switch (type) {
      case 'INTEREST':
      case 'INTEREST_ACCEPTED':
        return AppTheme.primary;
      case 'MESSAGE':
        return Colors.blue;
      default:
        return AppTheme.textSecondary;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Notifications')),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _loadNotifications,
              child: _notifications.isEmpty
                  ? const Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Icon(Icons.notifications_none, size: 64, color: AppTheme.textSecondary),
                          SizedBox(height: 16),
                          Text('No notifications yet', style: TextStyle(color: AppTheme.textSecondary, fontSize: 16)),
                        ],
                      ),
                    )
                  : ListView.separated(
                      itemCount: _notifications.length,
                      separatorBuilder: (_, __) => const Divider(height: 1),
                      itemBuilder: (context, i) {
                        final n = _notifications[i];
                        final isRead = n['readStatus'] == true;
                        return ListTile(
                          tileColor: isRead ? null : AppTheme.primary.withOpacity(0.05),
                          leading: Container(
                            width: 44,
                            height: 44,
                            decoration: BoxDecoration(
                              color: _getColor(n['type']).withOpacity(0.1),
                              shape: BoxShape.circle,
                            ),
                            child: Icon(_getIcon(n['type']), color: _getColor(n['type']), size: 22),
                          ),
                          title: Text(
                            n['title'] ?? '',
                            style: TextStyle(
                              fontWeight: isRead ? FontWeight.normal : FontWeight.w700,
                              fontSize: 14,
                            ),
                          ),
                          subtitle: Text(
                            n['message'] ?? '',
                            maxLines: 2,
                            overflow: TextOverflow.ellipsis,
                            style: const TextStyle(fontSize: 12),
                          ),
                          onTap: () async {
                            if (!isRead) {
                              await ref.read(apiClientProvider).markNotificationRead(n['id']);
                              setState(() => n['readStatus'] = true);
                            }
                          },
                        );
                      },
                    ),
            ),
    );
  }
}
