class APIResponse {
  final int code;
  final String msg;
  final dynamic data;

  APIResponse({required this.code, required this.msg, required this.data});

  factory APIResponse.fromJson(Map<String, dynamic> json) =>APIResponse(
    code: (json['code'] as num).toInt(),
    msg: json['msg'] as String,
    data: json['data'],
  );
}
