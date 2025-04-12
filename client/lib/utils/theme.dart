import 'package:flutter/material.dart';

extension ThemeUtils on ThemeData {
  static ThemeData get defaultTheme {
    final colorScheme = ColorScheme(
      brightness: Brightness.light,
      primary: Colors.orange.shade300,
      onPrimary: Colors.white,
      secondary: Colors.orange.shade200,
      onSecondary: Colors.black87,
      tertiary: Colors.orange.shade100,
      onTertiary: Colors.black54,
      error: Colors.red,
      onError: Colors.black54,
      surface: Colors.grey.shade200,
      onSurface: Colors.black87,
    );
    return fromColorScheme(colorScheme);
  }

  static ThemeData fromJson(Map<String, dynamic> json) {
    final colorScheme = ColorSchemeUtils.fromJson(json['colorScheme']);
    return fromColorScheme(colorScheme);
  }

  static ThemeData fromColorScheme(ColorScheme colorScheme) => ThemeData(
    appBarTheme: AppBarTheme(backgroundColor: colorScheme.primary),
    colorScheme: colorScheme,
    iconTheme: IconThemeData(
      color:
          HSLColor.fromColor(colorScheme.primary).withLightness(0.3).toColor(),
    ),
  );

  Map<String, dynamic> toJson() => {'colorScheme': colorScheme.toJson()};
}

extension ColorSchemeUtils on ColorScheme {
  static ColorScheme fromJson(Map<String, dynamic> json) {
    return ColorScheme(
      brightness: Brightness.light,
      primary: Color(json['primary']),
      onPrimary: Color(json['onPrimary']),
      secondary: Color(json['secondary']),
      onSecondary: Color(json['onSecondary']),
      tertiary: Color(json['tertiary']),
      onTertiary: Color(json['onTertiary']),
      surface: Color(json['surface']),
      onSurface: Color(json['onSurface']),
      error: Color(json['error']),
      onError: Color(json['onError']),
    );
  }

  Map<String, dynamic> toJson() => {
    'primary': primary.toARGB32(),
    'onPrimary': onPrimary.toARGB32(),
    'secondary': secondary.toARGB32(),
    'onSecondary': onSecondary.toARGB32(),
    'tertiary': tertiary.toARGB32(),
    'onTertiary': onTertiary.toARGB32(),
    'surface': surface.toARGB32(),
    'onSurface': onSurface.toARGB32(),
    'error': error.toARGB32(),
    'onError': onError.toARGB32(),
  };
}
