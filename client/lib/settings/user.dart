import 'dart:convert';
import 'dart:ui';

import 'package:h_nas/prefs.dart';
import 'package:h_nas/utils/api.dart';

abstract class UserS {
  static const String keyUser = 'user';

  static UserInfo? _user;

  static final _listeners = <VoidCallback>{};

  static UserInfo? get user => _user;

  static void load() {
    var str = Prefs.getString(keyUser);
    if (str != null) {
      _user = UserInfo.fromJson(jsonDecode(str));
    }
  }

  static set user(UserInfo? user) {
    _user = user;
    if (user != null) {
      Prefs.setString(keyUser, jsonEncode(user.toJson()));
    } else {
      Prefs.remove(keyUser);
    }
    _notifyListeners();
  }

  static void addListener(VoidCallback listener) {
    _listeners.add(listener);
  }

  static void removeListener(VoidCallback listener) {
    _listeners.remove(listener);
  }

  static void _notifyListeners() {
    for (final listener in _listeners) {
      listener();
    }
  }

  static void dispose() {
    _listeners.clear();
  }
}
