import 'dart:convert';

import 'package:flutter/cupertino.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/utils/api.dart';

class UserModel with ChangeNotifier {
  UserInfo? _user;

  UserInfo? get user => _user;

  UserModel() {
    var str = Prefs.getString(Prefs.keyUser);
    if (str != null) {
      _user = UserInfo.fromJson(jsonDecode(str));
    }
  }

  set(UserInfo? user) {
    _user = user;
    if (user != null) {
      Prefs.setString(Prefs.keyUser, jsonEncode(user.toJson()));
    } else {
      Prefs.remove(Prefs.keyUser);
    }
    notifyListeners();
  }
}
