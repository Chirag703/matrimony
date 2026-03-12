import 'package:flutter/material.dart';

import '../theme/app_theme.dart';

/// A horizontal step progress indicator used across onboarding screens.
class StepIndicator extends StatelessWidget {
  /// The current step (1-based).
  final int current;

  /// The total number of steps.
  final int total;

  const StepIndicator({super.key, required this.current, required this.total});

  @override
  Widget build(BuildContext context) {
    return Row(
      children: List.generate(
        total,
        (i) => Expanded(
          child: Container(
            height: 4,
            margin: const EdgeInsets.symmetric(horizontal: 2),
            decoration: BoxDecoration(
              color: i < current ? AppTheme.primary : AppTheme.divider,
              borderRadius: BorderRadius.circular(2),
            ),
          ),
        ),
      ),
    );
  }
}
