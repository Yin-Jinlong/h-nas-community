part of 'api.dart';

abstract class AIAPIURL extends AIAPI {
  static String get root => '${API.API_ROOT}/ai';

  static String chat() => '$root/chat';
}
