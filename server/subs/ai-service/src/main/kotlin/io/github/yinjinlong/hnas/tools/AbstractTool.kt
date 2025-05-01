package io.github.yinjinlong.hnas.tools

import java.util.logging.Logger

/**
 * @author YJL
 */
abstract class AbstractTool(
    protected val logger: Logger
) {

    protected val callerMethod: String
        get() {
            val stackTrace = Thread.currentThread().stackTrace
            return if (stackTrace.size < 4) "" else
                stackTrace[3].methodName
        }

    /**
     * 打印调用者（调用该方法的方法）日志
     * @param args 追加的参数
     */
    protected fun logCall(vararg args: Any?) {
        logger.info("Call $callerMethod ${args.joinToString()}")
    }

    /**
     * 打印调用者（调用该方法的方法）日志
     * @param tag 标签，在Call前面显示
     * @param args 追加的参数
     */
    protected fun logCall(tag: String, vararg args: Any?) {
        logger.info("$tag Call $callerMethod ${args.joinToString()}")
    }

}