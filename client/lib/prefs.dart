import 'package:h_nas/utils/api.dart';
import 'package:shared_preferences/shared_preferences.dart';

class Prefs {
  static const String keyApiHost = 'api.host';

  static late SharedPreferences _prefs;

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
