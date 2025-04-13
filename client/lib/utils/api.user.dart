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
              Prefs.token=token;
            }
          },
        )
        .then((data) {
          return data == null ? null : UserInfo.fromJson(data);
        });
  }

}
