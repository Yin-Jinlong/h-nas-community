import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/theme.dart';
import 'package:shared_preferences/shared_preferences.dart';

abstract class Prefs {
  static const String keyApiHost = 'api.host';
  static const String keyUser = 'user';
  static const String keyAuthToken = 'auth-token';
  static const String keyToken = 'token';
  static const String keyLocale = 'locale';
  static const String keyTheme = 'theme';
  static const String keyPlayerVolume = 'player-volume';

  static late SharedPreferences _prefs;

  static String? get authToken => _prefs.getString(keyAuthToken);

  static String? get token => _prefs.getString(keyToken);

  static Locale get locale {
    final value = _prefs.getString(keyLocale);
    if (value == null) {
      return Locale('zh');
    }
    final v = value.split('-');
    return Locale.fromSubtags(
      languageCode: v[0],
      scriptCode: v[1].isEmpty ? null : v[1],
      countryCode: v[2].isEmpty ? null : v[2],
    );
  }

  static ThemeData get theme {
    final value = _prefs.getString(keyTheme);
    return value == null
        ? ThemeUtils.defaultTheme
        : ThemeUtils.fromJson(jsonDecode(value));
  }

  static double get playerVolume {
    return _prefs.getDouble(keyPlayerVolume) ?? 80;
  }

  static setLocale(Locale l) {
    _prefs.setString(
      keyLocale,
      '${l.languageCode}-${l.scriptCode ?? ''}-${l.countryCode ?? ''}',
    );
  }

  static set theme(ThemeData l) {
    _prefs.setString(keyTheme, jsonEncode(l.toJson()));
  }

  static set playerVolume(double v) {
    _prefs.setDouble(keyPlayerVolume, v);
  }

  static init() async {
    _prefs = await SharedPreferences.getInstance();
    final host = _prefs.getString(keyApiHost);
    if (host != null) {
      API.API_ROOT = host;
    }
  }

  static Object? get(String key) => _prefs.get(key);

  static bool? getBool(String key) => _prefs.getBool(key);

  static int? getInt(String key) => _prefs.getInt(key);

  static double? getDouble(String key) => _prefs.getDouble(key);

  static String? getString(String key) => _prefs.getString(key);

  static List<String>? getStringList(String key) => _prefs.getStringList(key);

  static Future<bool> setBool(String key, bool value) =>
      _prefs.setBool(key, value);

  static Future<bool> setInt(String key, int value) =>
      _prefs.setInt(key, value);

  static Future<bool> setDouble(String key, double value) =>
      _prefs.setDouble(key, value);

  static Future<bool> setString(String key, String value) =>
      _prefs.setString(key, value);

  static Future<bool> setStringList(String key, List<String> value) =>
      _prefs.setStringList(key, value);

  static Future<bool> remove(String key) => _prefs.remove(key);
}
