part of 'api.dart';

abstract class AIAPI extends API {
  static const String root = '/ai';

  static Future<List<ChatMessageItem>> getHistory() async {
    return API
        ._get<List>(
          '$root/history',
          {},
          options: Options(headers: {...API.tokenHeader()}),
        )
        .then((value) {
          return value == null
              ? []
              : value.map((e) => ChatMessageItem.fromJson(e)).toList();
        });
  }

  static Future<bool> clearHistory() {
    return API
        ._delete<bool>(
          '$root/history',
          {},
          options: Options(headers: {...API.tokenHeader()}),
        )
        .then((value) {
          return value ?? false;
        });
  }
}
