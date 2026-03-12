import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/api/api_client.dart';
import '../../../core/theme/app_theme.dart';
import '../../../core/widgets/step_indicator.dart';

class PhotoUploadScreen extends ConsumerStatefulWidget {
  const PhotoUploadScreen({super.key});

  @override
  ConsumerState<PhotoUploadScreen> createState() => _PhotoUploadScreenState();
}

class _PhotoUploadScreenState extends ConsumerState<PhotoUploadScreen> {
  bool _isLoading = false;

  Future<void> _finishOnboarding() async {
    setState(() => _isLoading = true);
    try {
      await ref.read(apiClientProvider).completeOnboarding();
      if (mounted) context.go('/home');
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
      appBar: AppBar(title: const Text('Profile Photo')),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              StepIndicator(current: 6, total: 6),
              const SizedBox(height: 24),
              Text('Add Your Photo',
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(fontWeight: FontWeight.w700)),
              const SizedBox(height: 8),
              const Text('Profiles with photos get 10x more responses!'),
              const SizedBox(height: 32),
              Center(
                child: GestureDetector(
                  onTap: () {
                    // TODO: integrate image_picker and upload
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text('Photo upload: integrate image_picker')),
                    );
                  },
                  child: Container(
                    width: 160,
                    height: 160,
                    decoration: BoxDecoration(
                      color: AppTheme.divider,
                      borderRadius: BorderRadius.circular(80),
                      border: Border.all(color: AppTheme.primary, width: 2, style: BorderStyle.solid),
                    ),
                    child: const Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(Icons.add_a_photo, size: 48, color: AppTheme.primary),
                        SizedBox(height: 8),
                        Text('Add Photo', style: TextStyle(color: AppTheme.primary, fontWeight: FontWeight.w600)),
                      ],
                    ),
                  ),
                ),
              ),
              const Spacer(),
              ElevatedButton(
                onPressed: _isLoading ? null : _finishOnboarding,
                child: _isLoading
                    ? const CircularProgressIndicator(color: Colors.white, strokeWidth: 2)
                    : const Text('Finish Setup'),
              ),
              const SizedBox(height: 8),
              OutlinedButton(
                onPressed: _isLoading ? null : _finishOnboarding,
                child: const Text('Skip for now'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
