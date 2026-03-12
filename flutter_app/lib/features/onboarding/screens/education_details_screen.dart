import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/api/api_client.dart';
import '../../../core/theme/app_theme.dart';
import '../../../core/widgets/step_indicator.dart';

class EducationDetailsScreen extends ConsumerStatefulWidget {
  const EducationDetailsScreen({super.key});

  @override
  ConsumerState<EducationDetailsScreen> createState() =>
      _EducationDetailsScreenState();
}

class _EducationDetailsScreenState
    extends ConsumerState<EducationDetailsScreen> {
  String? _education;
  final _occupationController = TextEditingController();
  String? _salary;
  bool _isLoading = false;

  final _educations = [
    'High School', 'Diploma', 'B.Tech/B.E.', 'B.Sc', 'B.Com', 'BBA',
    'MBA', 'M.Tech', 'M.Sc', 'MBBS', 'MD/MS', 'PhD', 'CA', 'Other',
  ];
  final _salaries = [
    'Below ₹3L', '₹3L - ₹6L', '₹6L - ₹10L', '₹10L - ₹20L',
    '₹20L - ₹50L', 'Above ₹50L',
  ];

  Future<void> _saveAndNext() async {
    setState(() => _isLoading = true);
    try {
      await ref.read(apiClientProvider).updateProfile({
        'education': _education,
        'occupation': _occupationController.text.trim(),
        'salary': _salary,
      });
      if (mounted) context.go('/onboarding/lifestyle');
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
  void dispose() {
    _occupationController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Education & Career')),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              StepIndicator(current: 3, total: 6),
              const SizedBox(height: 24),
              Text('Education & Career',
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(fontWeight: FontWeight.w700)),
              const SizedBox(height: 24),
              DropdownButtonFormField<String>(
                value: _education,
                decoration: const InputDecoration(labelText: 'Education'),
                items: _educations.map((e) => DropdownMenuItem(value: e, child: Text(e))).toList(),
                onChanged: (v) => setState(() => _education = v),
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _occupationController,
                decoration: const InputDecoration(labelText: 'Occupation'),
              ),
              const SizedBox(height: 16),
              DropdownButtonFormField<String>(
                value: _salary,
                decoration: const InputDecoration(labelText: 'Annual Income'),
                items: _salaries.map((s) => DropdownMenuItem(value: s, child: Text(s))).toList(),
                onChanged: (v) => setState(() => _salary = v),
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
                onPressed: () => context.go('/onboarding/lifestyle'),
                child: const Text('Skip'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
