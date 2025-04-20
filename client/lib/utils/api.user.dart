part of 'api.dart';

extension UserAPI on API {
  static Future<UserInfo?> login(String logid, String password) {
    return API
        ._post<Map<String, dynamic>>(
          '/user/login',
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

  static Future<String?> requestLoginQR() {
    return API._post<String>('/user/login/qr/request', {}).then((data) {
      return data;
    });
  }

  static Future<QRGrantInfo?> getQRGrantInfo(String id) {
    return API
        ._post<Map<String, dynamic>>(
          '/user/grant/qr/info',
          {'id': id},
          options: Options(
            headers: {ExtraHeaders.authorization: Prefs.token},
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
      '/user/grant/qr',
      {'id': id, 'grant': grant},
      options: Options(
        headers: {ExtraHeaders.authorization: Prefs.token},
        contentType: Headers.formUrlEncodedContentType,
        responseType: ResponseType.json,
      ),
    );
  }

  static Future<LoginQRResult?> loginQR(String id) {
    return API
        ._post<Map<String, dynamic>>(
          '/user/login/qr',
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
          '/user/count',
          {},
          options: Options(headers: {ExtraHeaders.authorization: Prefs.token}),
        )
        .then((data) {
          return data;
        });
  }

  static Future<List<UserInfo>> getUsers(int stratId, int count) {
    return API
        ._get<List<dynamic>>(
          '/user/users',
          {'startId': stratId, 'count': count},
          options: Options(headers: {ExtraHeaders.authorization: Prefs.token}),
        )
        .then((data) {
          if (data == null) return [];
          final r = <UserInfo>[];

          for (final d in data) {
            r.add(UserInfo.fromJson(d));
          }

          return r;
        });
  }
}
