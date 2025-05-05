part of '../api/api.dart';

abstract class UserAPI extends API {
  static final String root = '/user';

  static Future<UserInfo?> login(String logid, String password) {
    return API
        ._post<JsonObject>(
          '$root/login',
          query: {'logId': logid, 'password': password},
          contentType: ExtraHeaders.contentTypeFormUrlEncoded,
          onResp: (res) {
            final token = res.headers[ExtraHeaders.authorization];
            if (token != null) {
              Prefs.token = token;
            }
          },
        )
        .then((data) {
          return UserInfo.fromJson(data);
        });
  }

  static Future<bool?> logon(String username, String password) {
    return API
        ._post(
          '$root/logon',
          query: {'username': username, 'password': password},
          contentType: ExtraHeaders.contentTypeFormUrlEncoded,
        )
        .then(API._boolThen);
  }

  static Future<UserInfo> getUserInfo() {
    return API._get<JsonObject>('$root/info', headers: API.tokenHeader()).then((
      data,
    ) {
      return UserInfo.fromJson(data);
    });
  }

  static Future<String> requestLoginQR() {
    return API._post<String>('$root/login/qr/request').then((data) {
      return data;
    });
  }

  static Future<QRGrantInfo> getQRGrantInfo(String id) {
    return API
        ._post<JsonObject>(
          '$root/grant/qr/info',
          query: {'id': id},
          headers: {...API.tokenHeader()},
          contentType: ExtraHeaders.contentTypeFormUrlEncoded,
        )
        .then((data) {
          return QRGrantInfo.fromJson(data);
        });
  }

  static Future<bool> grant(String id, bool grant) {
    return API
        ._post<bool>(
          '$root/grant/qr',
          query: {'id': id, 'grant': grant},
          headers: {...API.tokenHeader()},
          contentType: ExtraHeaders.contentTypeFormUrlEncoded,
        )
        .then(API._boolThen);
  }

  static Future<LoginQRResult> loginQR(String id) {
    return API
        ._post<JsonObject>(
          '$root/login/qr',
          query: {'id': id},
          contentType: ExtraHeaders.contentTypeFormUrlEncoded,
        )
        .then((data) {
          return LoginQRResult.fromJson(data);
        });
  }

  static Future<int> getUserCount() {
    return API
        ._get<int>('$root/count', headers: {...API.tokenHeader()})
        .then((data) {
          return data;
        });
  }

  static Future<List<UserInfo>> getUsers(int stratId, int count) {
    return API
        ._get<List>(
          '$root/users',
          query: {'startId': stratId, 'count': count},
          headers: API.tokenHeader(),
        )
        .then((data) {
          return data.map((e) => UserInfo.fromJson(e)).toList();
        });
  }

  static Future<bool> setNick(String nick) {
    return API
        ._patch('$root/nick', query: {'nick': nick}, headers: API.tokenHeader())
        .then(API._boolThen);
  }
}
