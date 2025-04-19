import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/utils/api.dart';

abstract class UserS {
  static const String keyUser = 'user';

  static final ValueNotifier<UserInfo?> _user = ValueNotifier(null);

  /// 管理员模式，每次必须手动启用
  static final ValueNotifier<bool> _adminMode = ValueNotifier(false);

  static UserInfo? get user => _user.value;

  static bool get adminMode => _adminMode.value;

  static void load() {
    var str = Prefs.getString(keyUser);
    if (str != null) {
      try {
        _user.value = UserInfo.fromJson(jsonDecode(str));
      } catch (e) {
        if (kDebugMode) {
          print('load user error: $e');
        }
      }
    }
  }

  static set user(UserInfo? user) {
    if (user != null) {
      Prefs.setString(keyUser, jsonEncode(user.toJson()));
    } else {
      Prefs.remove(keyUser);
    }
    _user.value = user;
    _adminMode.value = false;
  }

  static bool enableAdminMode() {
    if (_adminMode.value) return true;
    if (_user.value?.admin != true) {
      return false;
    }
    _adminMode.value = true;
    return true;
  }

  static bool disableAdminMode() {
    if (!_adminMode.value) return false;
    _adminMode.value = false;
    return true;
  }

  static void addUserListener(VoidCallback listener) {
    _user.addListener(listener);
  }

  static void removeUserListener(VoidCallback listener) {
    _user.removeListener(listener);
  }

  static void addAdminModeListener(VoidCallback listener) {
    _adminMode.addListener(listener);
  }

  static void removeAdminModeListener(VoidCallback listener) {
    _adminMode.removeListener(listener);
  }

  static void dispose() {
    _user.dispose();
    _adminMode.dispose();
  }
}
