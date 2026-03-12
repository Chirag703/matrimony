import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/api/api_client.dart';
import '../../../core/theme/app_theme.dart';
import '../../../core/widgets/step_indicator.dart';

class PartnerPreferencesScreen extends ConsumerStatefulWidget {
  const PartnerPreferencesScreen({super.key});

  @override
  ConsumerState<PartnerPreferencesScreen> createState() =>
      _PartnerPreferencesScreenState();
}

class _PartnerPreferencesScreenState
    extends ConsumerState<PartnerPreferencesScreen> {
  RangeValues _ageRange = const RangeValues(22, 32);
  String? _religion;
  String? _education;
  bool _isLoading = false;

  Future<void> _saveAndNext() async {
    setState(() => _isLoading = true);
    try {
      await ref.read(apiClientProvider).updatePartnerPreferences({
        'minAge': _ageRange.start.round(),
        'maxAge': _ageRange.end.round(),
        'religion': _religion,
        'education': _education,
      });
      if (mounted) context.go('/onboarding/photo');
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
      appBar: AppBar(title: const Text('Partner Preferences')),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              StepIndicator(current: 5, total: 6),
              const SizedBox(height: 24),
              Text('Partner Preferences',
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(fontWeight: FontWeight.w700)),
              const SizedBox(height: 24),
              Text('Age Range: ${_ageRange.start.round()} - ${_ageRange.end.round()} years',
                  style: const TextStyle(fontWeight: FontWeight.w600)),
              RangeSlider(
                values: _ageRange,
                min: 18,
                max: 60,
                divisions: 42,
                labels: RangeLabels(
                  _ageRange.start.round().toString(),
                  _ageRange.end.round().toString(),
                ),
                activeColor: AppTheme.primary,
                onChanged: (v) => setState(() => _ageRange = v),
              ),
              const SizedBox(height: 16),
              DropdownButtonFormField<String>(
                value: _religion,
                decoration: const InputDecoration(labelText: 'Preferred Religion'),
                items: ['Any', 'Hindu', 'Muslim', 'Christian', 'Sikh', 'Jain']
                    .map((r) => DropdownMenuItem(value: r, child: Text(r)))
                    .toList(),
                onChanged: (v) => setState(() => _religion = v),
              ),
              const SizedBox(height: 16),
              DropdownButtonFormField<String>(
                value: _education,
                decoration: const InputDecoration(labelText: 'Minimum Education'),
                items: ['Any', 'Diploma', 'Graduate', 'Post Graduate', 'PhD']
                    .map((e) => DropdownMenuItem(value: e, child: Text(e)))
                    .toList(),
                onChanged: (v) => setState(() => _education = v),
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
                onPressed: () => context.go('/onboarding/photo'),
                child: const Text('Skip'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
