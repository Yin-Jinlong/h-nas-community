import 'package:flutter/material.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/utils/theme.dart';

abstract class ThemeS {
  static const String keyTheme = 'theme';
  static const String keyThemeMode = 'theme-mode';

  static final _themeModeListeners = <VoidCallback>{};
  static final _themeColorListeners = <VoidCallback>{};

  static ThemeMode get themeMode {
    final value = Prefs.getInt(keyThemeMode);
    return value == null || value < 0 || value > ThemeMode.values.length - 1
        ? ThemeMode.system
        : ThemeMode.values[value];
  }

  static set themeMode(ThemeMode mode) {
    Prefs.setInt(keyThemeMode, mode.index);
    _notifyThemeModeListeners();
  }

  static Color get themeColor {
    var value = Prefs.getInt(keyTheme);
    if (value == null) return ThemeUtils.defaultColor;
    return Color(value);
  }

  static set themeColor(Color color) {
    Prefs.setInt(keyTheme, color.toARGB32());
    _notifyThemeColorListeners();
  }

  static ThemeData getTheme(Brightness brightness) {
    return ThemeUtils.fromColor(themeColor, brightness);
  }

  static void addThemeModeListener(VoidCallback listener) {
    _themeModeListeners.add(listener);
  }

  static void removeThemeModeListener(VoidCallback listener) {
    _themeModeListeners.remove(listener);
  }

  static void _notifyThemeModeListeners() {
    for (final listener in _themeModeListeners) {
      listener();
    }
  }

  static void addThemeColorListener(VoidCallback listener) {
    _themeColorListeners.add(listener);
  }

  static void removeThemeColorListener(VoidCallback listener) {
    _themeColorListeners.remove(listener);
  }

  static void _notifyThemeColorListeners() {
    for (final listener in _themeColorListeners) {
      listener();
    }
  }

  static void dispose() {
    _themeModeListeners.clear();
    _themeColorListeners.clear();
  }
}
