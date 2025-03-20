import 'package:json_annotation/json_annotation.dart';

part 'api_response.g.dart';


@JsonSerializable()
class APIResponse {
  final int code;
  final String msg;
  final dynamic data;

  APIResponse({required this.code, required this.msg, required this.data});

  factory APIResponse.fromJson(Map<String, dynamic> json) =>
      _$APIResponseFromJson(json);
}
