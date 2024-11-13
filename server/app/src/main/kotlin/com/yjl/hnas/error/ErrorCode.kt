package com.yjl.hnas.error

/**
 * @author YJL
 */
enum class ErrorCode(
    val code: Int,
    val msg: String
) {
    OK(0, "OK"),

    BAD_TOKEN(100, "token无效"),

    BAD_REQUEST(1, "Bad Request"),
    BAD_ARGUMENTS(2, "参数错误"),
    BAD_HEADER(3, "Header错误"),

    NO_SUCH_FILE(1001, "文件不存在"),
    FILE_EXISTS(1002, "文件已存在"),

    USER_LOGIN_ERROR(2000, "用户名/id/密码错误"),
    NO_SUCH_USER(2001, "用户不存在"),
    USER_EXISTS(2002, "用户已存在"),
    NO_PERMISSION(2003, "没有权限"),
    USER_NOT_LOGIN(2000, "没有登录"),

    SERVER_ERROR(10000, "Server Error");

    val error: ClientError
        get() = ClientError(this)

    fun data(data: Any) = ClientError(this, data)

    operator fun invoke(data: Any) = ClientError(this, data)
}