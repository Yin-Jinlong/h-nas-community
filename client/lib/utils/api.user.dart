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
              Prefs.setString(Prefs.keyAuthToken, token);
            }
          },
        )
        .then((data) {
          auth();
          return data == null ? null : UserInfo.fromJson(data);
        });
  }

  static Future<bool?> auth() {
    return API._post<bool>(
      '/user/auth',
      null,
      options: Options(headers: {ExtraHeaders.authorization: Prefs.authToken}),
      onResp: (res) {
        final token = res.headers.value(ExtraHeaders.authorization);
        if (token != null) {
          Prefs.setString(Prefs.keyToken, token);
        }
      },
    );
  }
}
