import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/api/api_client.dart';
import '../../../core/theme/app_theme.dart';
import '../../../core/widgets/step_indicator.dart';

class LifestyleScreen extends ConsumerStatefulWidget {
  const LifestyleScreen({super.key});

  @override
  ConsumerState<LifestyleScreen> createState() => _LifestyleScreenState();
}

class _LifestyleScreenState extends ConsumerState<LifestyleScreen> {
  String? _smoking;
  String? _drinking;
  String? _diet;
  bool _isLoading = false;

  final _options = ['Yes', 'No', 'Occasionally'];
  final _diets = ['Vegetarian', 'Non-Vegetarian', 'Vegan', 'Jain'];

  Future<void> _saveAndNext() async {
    setState(() => _isLoading = true);
    try {
      await ref.read(apiClientProvider).updateProfile({
        'smoking': _smoking,
        'drinking': _drinking,
        'diet': _diet,
      });
      if (mounted) context.go('/onboarding/preferences');
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
      appBar: AppBar(title: const Text('Lifestyle')),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              StepIndicator(current: 4, total: 6),
              const SizedBox(height: 24),
              Text('Your Lifestyle',
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(fontWeight: FontWeight.w700)),
              const SizedBox(height: 24),
              DropdownButtonFormField<String>(
                value: _smoking,
                decoration: const InputDecoration(labelText: 'Smoking'),
                items: _options.map((o) => DropdownMenuItem(value: o, child: Text(o))).toList(),
                onChanged: (v) => setState(() => _smoking = v),
              ),
              const SizedBox(height: 16),
              DropdownButtonFormField<String>(
                value: _drinking,
                decoration: const InputDecoration(labelText: 'Drinking'),
                items: _options.map((o) => DropdownMenuItem(value: o, child: Text(o))).toList(),
                onChanged: (v) => setState(() => _drinking = v),
              ),
              const SizedBox(height: 16),
              DropdownButtonFormField<String>(
                value: _diet,
                decoration: const InputDecoration(labelText: 'Diet'),
                items: _diets.map((d) => DropdownMenuItem(value: d, child: Text(d))).toList(),
                onChanged: (v) => setState(() => _diet = v),
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
                onPressed: () => context.go('/onboarding/preferences'),
                child: const Text('Skip'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
