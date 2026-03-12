import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/api/api_client.dart';
import '../../../core/theme/app_theme.dart';

class PhoneLoginScreen extends ConsumerStatefulWidget {
  const PhoneLoginScreen({super.key});

  @override
  ConsumerState<PhoneLoginScreen> createState() => _PhoneLoginScreenState();
}

class _PhoneLoginScreenState extends ConsumerState<PhoneLoginScreen> {
  final _phoneController = TextEditingController();
  final _formKey = GlobalKey<FormState>();
  bool _isLoading = false;
  String _countryCode = '+91';

  @override
  void dispose() {
    _phoneController.dispose();
    super.dispose();
  }

  Future<void> _sendOtp() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isLoading = true);
    final phone = '$_countryCode${_phoneController.text.trim()}';

    try {
      await ref.read(apiClientProvider).sendOtp(phone);
      if (mounted) {
        context.go('/verify-otp?phone=${Uri.encodeComponent(phone)}');
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Failed to send OTP: ${e.toString()}'),
            backgroundColor: AppTheme.error,
          ),
        );
      }
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24),
          child: Form(
            key: _formKey,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SizedBox(height: 40),
                Center(
                  child: Container(
                    width: 80,
                    height: 80,
                    decoration: BoxDecoration(
                      gradient: const LinearGradient(
                        colors: [AppTheme.primary, AppTheme.primaryDark],
                      ),
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: const Icon(Icons.favorite, color: Colors.white, size: 44),
                  ),
                ),
                const SizedBox(height: 32),
                Text(
                  'Welcome to\nMatrimony',
                  style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                        fontWeight: FontWeight.w700,
                        color: AppTheme.textPrimary,
                        height: 1.2,
                      ),
                ),
                const SizedBox(height: 8),
                Text(
                  'Enter your phone number to get started',
                  style: Theme.of(context)
                      .textTheme
                      .bodyMedium
                      ?.copyWith(color: AppTheme.textSecondary),
                ),
                const SizedBox(height: 40),
                Text(
                  'Phone Number',
                  style: Theme.of(context)
                      .textTheme
                      .labelLarge
                      ?.copyWith(fontWeight: FontWeight.w600),
                ),
                const SizedBox(height: 8),
                Row(
                  children: [
                    // Country code picker (simplified)
                    Container(
                      decoration: BoxDecoration(
                        border: Border.all(color: AppTheme.divider),
                        borderRadius: BorderRadius.circular(12),
                        color: Colors.white,
                      ),
                      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 14),
                      child: DropdownButtonHideUnderline(
                        child: DropdownButton<String>(
                          value: _countryCode,
                          isDense: true,
                          items: const [
                            DropdownMenuItem(value: '+91', child: Text('+91 🇮🇳')),
                            DropdownMenuItem(value: '+1', child: Text('+1 🇺🇸')),
                            DropdownMenuItem(value: '+44', child: Text('+44 🇬🇧')),
                            DropdownMenuItem(value: '+971', child: Text('+971 🇦🇪')),
                            DropdownMenuItem(value: '+61', child: Text('+61 🇦🇺')),
                          ],
                          onChanged: (val) =>
                              setState(() => _countryCode = val!),
                        ),
                      ),
                    ),
                    const SizedBox(width: 8),
                    Expanded(
                      child: TextFormField(
                        controller: _phoneController,
                        keyboardType: TextInputType.phone,
                        decoration: const InputDecoration(
                          hintText: '9876543210',
                        ),
                        validator: (v) {
                          if (v == null || v.isEmpty) return 'Enter phone number';
                          if (v.length < 7) return 'Enter a valid phone number';
                          return null;
                        },
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 32),
                ElevatedButton(
                  onPressed: _isLoading ? null : _sendOtp,
                  child: _isLoading
                      ? const SizedBox(
                          height: 20,
                          width: 20,
                          child: CircularProgressIndicator(
                            color: Colors.white,
                            strokeWidth: 2,
                          ),
                        )
                      : const Text('Send OTP'),
                ),
                const SizedBox(height: 24),
                Center(
                  child: Text(
                    'By continuing, you agree to our Terms & Privacy Policy',
                    textAlign: TextAlign.center,
                    style: Theme.of(context)
                        .textTheme
                        .bodySmall
                        ?.copyWith(color: AppTheme.textSecondary),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
