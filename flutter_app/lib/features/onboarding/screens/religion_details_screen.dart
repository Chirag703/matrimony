import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/api/api_client.dart';
import '../../../core/theme/app_theme.dart';
import '../../../core/widgets/step_indicator.dart';

class ReligionDetailsScreen extends ConsumerStatefulWidget {
  const ReligionDetailsScreen({super.key});

  @override
  ConsumerState<ReligionDetailsScreen> createState() =>
      _ReligionDetailsScreenState();
}

class _ReligionDetailsScreenState
    extends ConsumerState<ReligionDetailsScreen> {
  String? _religion;
  String? _caste;
  String? _community;
  bool _isLoading = false;

  final _religions = ['Hindu', 'Muslim', 'Christian', 'Sikh', 'Jain', 'Buddhist', 'Parsi', 'Other'];

  Future<void> _saveAndNext() async {
    setState(() => _isLoading = true);
    try {
      await ref.read(apiClientProvider).updateProfile({
        'religion': _religion,
        'caste': _caste,
        'community': _community,
      });
      if (mounted) context.go('/onboarding/education');
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error: $e'), backgroundColor: AppTheme.error),
        );
      }
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Religion & Community')),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              StepIndicator(current: 2, total: 6),
              const SizedBox(height: 24),
              Text('Religion & Community',
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(fontWeight: FontWeight.w700)),
              const SizedBox(height: 24),
              DropdownButtonFormField<String>(
                value: _religion,
                decoration: const InputDecoration(labelText: 'Religion'),
                items: _religions.map((r) => DropdownMenuItem(value: r, child: Text(r))).toList(),
                onChanged: (v) => setState(() => _religion = v),
              ),
              const SizedBox(height: 16),
              TextFormField(
                decoration: const InputDecoration(labelText: 'Caste (Optional)'),
                onChanged: (v) => _caste = v,
              ),
              const SizedBox(height: 16),
              TextFormField(
                decoration: const InputDecoration(labelText: 'Community (Optional)'),
                onChanged: (v) => _community = v,
              ),
              const SizedBox(height: 32),
              ElevatedButton(
                onPressed: _isLoading ? null : _saveAndNext,
                child: _isLoading
                    ? const CircularProgressIndicator(color: Colors.white, strokeWidth: 2)
                    : const Text('Next →'),
              ),
              const SizedBox(height: 8),
              OutlinedButton(
                onPressed: () => context.go('/onboarding/education'),
                child: const Text('Skip'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
