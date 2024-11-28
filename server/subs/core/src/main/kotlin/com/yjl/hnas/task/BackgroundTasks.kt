package com.yjl.hnas.task

import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author YJL
 */
object BackgroundTasks {

    val scope = CoroutineScope(SupervisorJob() + BackgroundCoroutineDispatcher())

    val tasks = ConcurrentHashMap<Any, Job>()

    /**
     * 运行一个任务，如果任务已经存在，则返回存在的任务
     *
     * @param key 任务的键
     * @param alive 任务结束后存活时间md，用于避免任务短时间内重复执行
     * @param block 任务体
     */
    fun run(key: Any, alive: Long = 1000, block: suspend CoroutineScope.() -> Unit): Job {
        return tasks[key] ?: scope.launch {
            block()
            delay(alive)
        }.also {
            tasks[key] = it
            it.invokeOnCompletion {
                tasks.remove(key)
            }
        }
    }

    operator fun get(key: Any): Job? = tasks[key]
}
