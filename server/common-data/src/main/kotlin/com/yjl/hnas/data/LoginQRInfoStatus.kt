package com.yjl.hnas.data

/**
 * @author YJL
 */
enum class LoginQRInfoStatus {
    /**
     * 等待扫码
     */
    WAITING,

    /**
     * 已扫码
     */
    SCANNED,

    /**
     * 成功
     */
    SUCCESS,

    /**
     * 失败
     */
    FAILED,

    /**
     * 无效
     */
    INVALID,
}