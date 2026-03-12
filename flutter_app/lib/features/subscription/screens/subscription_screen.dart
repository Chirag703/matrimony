import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/api/api_client.dart';
import '../../../core/theme/app_theme.dart';

class SubscriptionScreen extends ConsumerStatefulWidget {
  const SubscriptionScreen({super.key});

  @override
  ConsumerState<SubscriptionScreen> createState() => _SubscriptionScreenState();
}

class _SubscriptionScreenState extends ConsumerState<SubscriptionScreen> {
  Map<String, dynamic>? _activeSub;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadSubscription();
  }

  Future<void> _loadSubscription() async {
    try {
      final response = await ref.read(apiClientProvider).getActiveSubscription();
      setState(() {
        _activeSub = response.data['data'];
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _subscribe(String plan) async {
    try {
      await ref.read(apiClientProvider).subscribe(plan);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('$plan plan activated! 🎉'), backgroundColor: AppTheme.success),
        );
        context.go('/home');
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed: $e'), backgroundColor: AppTheme.error),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Go Premium')),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : SingleChildScrollView(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  if (_activeSub != null)
                    _ActiveSubBanner(sub: _activeSub!),
                  const SizedBox(height: 16),
                  // Premium benefits
                  const _PremiumBenefitsList(),
                  const SizedBox(height: 24),
                  Text('Choose Your Plan',
                      style: Theme.of(context).textTheme.titleLarge?.copyWith(fontWeight: FontWeight.w700)),
                  const SizedBox(height: 16),
                  _PlanCard(
                    name: 'Basic',
                    price: '₹999',
                    duration: '1 Month',
                    color: Colors.grey,
                    isPopular: false,
                    onTap: () => _subscribe('BASIC'),
                  ),
                  const SizedBox(height: 12),
                  _PlanCard(
                    name: 'Gold',
                    price: '₹2,499',
                    duration: '3 Months',
                    color: Colors.amber[700]!,
                    isPopular: true,
                    onTap: () => _subscribe('GOLD'),
                  ),
                  const SizedBox(height: 12),
                  _PlanCard(
                    name: 'Platinum',
                    price: '₹3,999',
                    duration: '6 Months',
                    color: Colors.cyan[700]!,
                    isPopular: false,
                    onTap: () => _subscribe('PLATINUM'),
                  ),
                ],
              ),
            ),
    );
  }
}

class _ActiveSubBanner extends StatelessWidget {
  final Map<String, dynamic> sub;
  const _ActiveSubBanner({required this.sub});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        gradient: const LinearGradient(colors: [AppTheme.primary, AppTheme.primaryDark]),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        children: [
          const Icon(Icons.star, color: Colors.white, size: 32),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('Active: ${sub['plan']} Plan', style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w700)),
                Text('Valid until: ${sub['endDate']}', style: const TextStyle(color: Colors.white70, fontSize: 12)),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _PremiumBenefitsList extends StatelessWidget {
  const _PremiumBenefitsList();

  @override
  Widget build(BuildContext context) {
    const benefits = [
      ('Unlimited profile views', Icons.visibility),
      ('Unlimited chat', Icons.chat),
      ('View phone numbers', Icons.phone),
      ('See who liked you', Icons.favorite),
      ('Higher ranking in search', Icons.trending_up),
      ('Advanced filters', Icons.filter_list),
    ];

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Premium Benefits', style: TextStyle(fontWeight: FontWeight.w700, fontSize: 16)),
            const SizedBox(height: 12),
            ...benefits.map((b) => Padding(
              padding: const EdgeInsets.only(bottom: 8),
              child: Row(
                children: [
                  Icon(b.$2, color: AppTheme.primary, size: 20),
                  const SizedBox(width: 10),
                  Text(b.$1, style: const TextStyle(fontSize: 14)),
                ],
              ),
            )),
          ],
        ),
      ),
    );
  }
}

class _PlanCard extends StatelessWidget {
  final String name;
  final String price;
  final String duration;
  final Color color;
  final bool isPopular;
  final VoidCallback onTap;

  const _PlanCard({
    required this.name,
    required this.price,
    required this.duration,
    required this.color,
    required this.isPopular,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        Card(
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16),
            side: isPopular ? BorderSide(color: color, width: 2) : BorderSide.none,
          ),
          child: Padding(
            padding: const EdgeInsets.all(20),
            child: Row(
              children: [
                Container(
                  width: 50,
                  height: 50,
                  decoration: BoxDecoration(
                    color: color.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Icon(Icons.star, color: color, size: 28),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(name, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.w700)),
                      Text(duration, style: const TextStyle(color: AppTheme.textSecondary)),
                    ],
                  ),
                ),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.end,
                  children: [
                    Text(price, style: TextStyle(fontSize: 20, fontWeight: FontWeight.w700, color: color)),
                    ElevatedButton(
                      onPressed: onTap,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: color,
                        minimumSize: const Size(80, 36),
                        padding: const EdgeInsets.symmetric(horizontal: 16),
                      ),
                      child: const Text('Buy', style: TextStyle(fontSize: 13)),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
        if (isPopular)
          Positioned(
            top: 12,
            right: 12,
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
              decoration: BoxDecoration(
                color: color,
                borderRadius: BorderRadius.circular(8),
              ),
              child: const Text('POPULAR', style: TextStyle(color: Colors.white, fontSize: 10, fontWeight: FontWeight.w700)),
            ),
          ),
      ],
    );
  }
}
