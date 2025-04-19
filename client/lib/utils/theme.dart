import 'dart:math';

import 'package:flutter/material.dart';

extension ThemeUtils on ThemeData {
  static Color defaultColor = Colors.orange.shade300;

  static ThemeData get defaultTheme {
    return fromColor(defaultColor, Brightness.light);
  }

  static ThemeData _fromColorScheme(ColorScheme colorScheme) => ThemeData(
    appBarTheme: AppBarTheme(
      backgroundColor: colorScheme.primary,
      titleTextStyle: TextStyle(fontSize: 20, color: colorScheme.onPrimary),
      iconTheme: IconThemeData(color: colorScheme.onPrimary),
      actionsIconTheme: IconThemeData(color: colorScheme.onPrimary),
    ),
    switchTheme: SwitchThemeData(
      trackColor: WidgetStateProperty.resolveWith((states) {
        return states.contains(WidgetState.selected)
            ? colorScheme.primary
            : colorScheme.surface;
      }),
      thumbColor: WidgetStateProperty.resolveWith((states) {
        return states.contains(WidgetState.selected)
            ? colorScheme.onPrimary
            : colorScheme.primary;
      }),
    ),
    colorScheme: colorScheme,
  );

  ///
  /// primary 亮色的主题色
  ///
  static ThemeData fromColor(Color primary, Brightness brightness) {
    final dark = brightness == Brightness.dark;
    if (dark) {
      primary = primary.dark;
    }
    final error = primary.errorColor;
    final secondary = dark ? primary.darkSecondary : primary.secondary;
    final tertiary = dark ? primary.darkTertiary : primary.tertiary;
    final surface =
        brightness == Brightness.light
            ? Colors.grey.shade100
            : Colors.grey.shade900;
    return _fromColorScheme(
      ColorScheme(
        brightness: brightness,
        primary: primary,
        onPrimary: primary.onColor,
        secondary: secondary,
        onSecondary: secondary.onColor,
        tertiary: tertiary,
        onTertiary: tertiary.onColor,
        error: error,
        onError: error.onColor,
        surface: surface,
        onSurface: surface.onColor,
      ),
    );
  }
}

extension _OnColor on Color {
  static final Color error = HSVColor.fromAHSV(1, 0, 0.6, 0.95).toColor();
  static final Color errorHighSaturation =
      HSVColor.fromAHSV(1, 0, 0.8, 0.95).toColor();

  Color get onColor {
    final hsv = HSVColor.fromColor(this);
    if (hsv.value > 0.8 && hsv.saturation > 0.8) {
      return Colors.white;
    } else if (hsv.value > 0.9 && hsv.saturation < 0.3) {
      return Colors.black87;
    } else {
      return Colors.white;
    }
  }

  Color get dark {
    final hsv = HSVColor.fromColor(this);
    return hsv.withValue(hsv.value * 0.6).toColor();
  }

  Color get secondary {
    final hsv = HSVColor.fromColor(this);
    return hsv.withSaturation(hsv.saturation * 0.7).toColor();
  }

  Color get darkSecondary {
    final hsv = HSVColor.fromColor(this);
    final v = min(hsv.value + 0.1, 1.0);
    return hsv.withValue(v).toColor();
  }

  Color get tertiary {
    final hsv = HSVColor.fromColor(this);
    return hsv.withSaturation(hsv.saturation * 0.4).toColor();
  }

  Color get darkTertiary {
    final hsv = HSVColor.fromColor(this);
    final v = min(hsv.value + 0.2, 1.0);
    return hsv.withValue(v).toColor();
  }

  Color get errorColor {
    final hsv = HSVColor.fromColor(this);
    if (hsv.hue < 5 && hsv.value > 0.9) {
      return hsv.saturation > 0.75 ? error : errorHighSaturation;
    } else {
      return error;
    }
  }
}
