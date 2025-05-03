part of 'api.dart';

abstract class UserAPI extends API {
  static final String root = '/user';

  static Future<UserInfo?> login(String logid, String password) {
    return API
        ._post<Map<String, dynamic>>(
          '$root/login',
          {'logId': logid, 'password': password},
          options: Options(
            contentType: Headers.formUrlEncodedContentType,
            responseType: ResponseType.json,
          ),
          onResp: (res) {
            final token = res.headers.value(ExtraHeaders.authorization);
            if (token != null) {
              Prefs.token = token;
            }
          },
        )
        .then((data) {
          return data == null ? null : UserInfo.fromJson(data);
        });
  }

  static Future<bool?> logon(String username, String password) {
    return API._post<bool>(
      '$root/logon',
      {'username': username, 'password': password},
      options: Options(contentType: Headers.formUrlEncodedContentType),
    );
  }

  static Future<UserInfo?> getUserInfo() {
    return API
        ._get(
          '$root/info',
          {},
          options: Options(headers: {...API.tokenHeader()}),
        )
        .then((data) {
          return data == null ? null : UserInfo.fromJson(data);
        });
  }

  static Future<String?> requestLoginQR() {
    return API._post<String>('$root/login/qr/request', {}).then((data) {
      return data;
    });
  }

  static Future<QRGrantInfo?> getQRGrantInfo(String id) {
    return API
        ._post<Map<String, dynamic>>(
          '$root/grant/qr/info',
          {'id': id},
          options: Options(
            headers: {...API.tokenHeader()},
            contentType: Headers.formUrlEncodedContentType,
            responseType: ResponseType.json,
          ),
        )
        .then((data) {
          return data == null ? null : QRGrantInfo.fromJson(data);
        });
  }

  static Future<bool?> grant(String id, bool grant) {
    return API._post<bool>(
      '$root/grant/qr',
      {'id': id, 'grant': grant},
      options: Options(
        headers: {...API.tokenHeader()},
        contentType: Headers.formUrlEncodedContentType,
        responseType: ResponseType.json,
      ),
    );
  }

  static Future<LoginQRResult?> loginQR(String id) {
    return API
        ._post<Map<String, dynamic>>(
          '$root/login/qr',
          {'id': id},
          options: Options(
            contentType: Headers.formUrlEncodedContentType,
            responseType: ResponseType.json,
          ),
        )
        .then((data) {
          return data == null ? null : LoginQRResult.fromJson(data);
        });
  }

  static Future<int?> getUserCount() {
    return API
        ._get<int>(
          '$root/count',
          {},
          options: Options(headers: {...API.tokenHeader()}),
        )
        .then((data) {
          return data;
        });
  }

  static Future<List<UserInfo>> getUsers(int stratId, int count) {
    return API
        ._get<List<dynamic>>('$root/users', {
          'startId': stratId,
          'count': count,
        }, options: Options(headers: {...API.tokenHeader()}))
        .then((data) {
          if (data == null) return [];
          final r = <UserInfo>[];

          for (final d in data) {
            r.add(UserInfo.fromJson(d));
          }

          return r;
        });
  }

  static Future<bool> setNick(String nick) {
    return API
        ._patch(
          '$root/nick',
          null,
          parms: {'nick': nick},
          options: Options(headers: {...API.tokenHeader()}),
        )
        .then((value) => value == true)
        .catchError(_catchError);
  }
}
