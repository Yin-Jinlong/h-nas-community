import 'package:flutter/material.dart';
import 'package:h_nas/media/media_player.dart';
import 'package:h_nas/utils/api.dart';
import 'package:shared_preferences/shared_preferences.dart';

abstract class Prefs {
  static const String keyApiHost = 'api.host';
  static const String keyToken = 'token';
  static const String keyLocale = 'locale';
  static const String keyPlayerVolume = 'player-volume';
  static const String keyPlayerPlayMode = 'player-play-mode';

  static late SharedPreferences _prefs;

  static String? get token => getString(keyToken);

  static set token(String? token) {
    if (token == null) {
      remove(keyToken);
    } else {
      setString(keyToken, token);
    }
  }

  static Locale get locale {
    final value = getString(keyLocale);
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

  static double get playerVolume {
    return getDouble(keyPlayerVolume) ?? 80;
  }

  static PlayMode get playerPlayMode {
    final value = getInt(keyPlayerPlayMode);
    return value == null || value < 0 || value > PlayMode.values.length - 1
        ? PlayMode.none
        : PlayMode.values[value];
  }

  static setLocale(Locale l) {
    setString(
      keyLocale,
      '${l.languageCode}-${l.scriptCode ?? ''}-${l.countryCode ?? ''}',
    );
  }

  static set playerVolume(double v) {
    setDouble(keyPlayerVolume, v);
  }

  static set playerPlayMode(PlayMode v) {
    setInt(keyPlayerPlayMode, v.index);
  }

  static init() async {
    _prefs = await SharedPreferences.getInstance();
    final host = _prefs.getString(keyApiHost);
    if (host != null) {
      API.API_ROOT = host;
    }
  }

  static Object? get(String key) => _prefs.get(key);

  static bool? getBool(String key) {
    try {
      return _prefs.getBool(key);
    } catch (e) {
      return null;
    }
  }

  static int? getInt(String key) {
    try {
      return _prefs.getInt(key);
    } catch (e) {
      return null;
    }
  }

  static double? getDouble(String key) {
    try {
      return _prefs.getDouble(key);
    } catch (e) {
      return null;
    }
  }

  static String? getString(String key) {
    try {
      return _prefs.getString(key);
    } catch (e) {
      return null;
    }
  }

  static List<String>? getStringList(String key) {
    try {
      return _prefs.getStringList(key);
    } catch (e) {
      return null;
    }
  }

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
