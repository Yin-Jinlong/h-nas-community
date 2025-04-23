package io.github.yinjinlong.hnas.error

import org.springframework.http.HttpStatus

/**
 * @author YJL
 */
enum class ErrorCode(
    val code: Int,
    val msg: String,
    val status: HttpStatus = HttpStatus.BAD_REQUEST
) {
    OK(0, "OK", HttpStatus.OK),

    BAD_TOKEN(100, "token无效"),

    BAD_REQUEST(1, "Bad Request"),
    BAD_ARGUMENTS(2, "参数错误"),
    BAD_HEADER(3, "Header错误"),

    NO_SUCH_FILE(1001, "文件不存在"),
    FILE_EXISTS(1002, "文件已存在"),
    BAD_FILE_FORMAT(1003, "文件格式错误"),
    FOLDER_NOT_EMPTY(1004, "目录不为空"),
    NOT_FOLDER(1005, "非目录"),
    TOO_MANY_CHILDREN(1006, "子文件太多"),

    USER_LOGIN_ERROR(2000, "用户名/id/密码错误"),
    NO_SUCH_USER(2001, "用户不存在"),
    USER_EXISTS(2002, "用户已存在"),
    NO_PERMISSION(2003, "没有权限", HttpStatus.FORBIDDEN),
    USER_NOT_LOGIN(2000, "没有登录", HttpStatus.FORBIDDEN),

    SERVER_ERROR(10000, "Server Error", HttpStatus.INTERNAL_SERVER_ERROR);

    val error: ClientError
        get() = ClientError(this)

    fun data(data: Any) = ClientError(this, data)

    operator fun invoke(data: Any) = ClientError(this, data)
}