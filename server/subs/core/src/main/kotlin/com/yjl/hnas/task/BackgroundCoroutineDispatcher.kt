package com.yjl.hnas.task

import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.Runnable
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

/**
 * 线程池
 *
 * @property corePoolSize 核心线程数。默认为1
 * @property maximumPoolSize 最大线程数。默认为CPU线程数的一半，避免占满CPU
 * @property keepAliveTime 线程空闲时间。默认60
 * @property timeUnit 时间单位。默认秒
 * @property capacity 队列容量。默认100
 * @author YJL
 */
class BackgroundCoroutineDispatcher(
    val corePoolSize: Int = 1,
    val maximumPoolSize: Int = Runtime.getRuntime().availableProcessors() / 2,
    val keepAliveTime: Long = 60,
    val timeUnit: TimeUnit = TimeUnit.SECONDS,
    val capacity: Int = 100
) : ExecutorCoroutineDispatcher() {

    override val executor = ThreadPoolExecutor(
        corePoolSize,
        maximumPoolSize,
        keepAliveTime,
        timeUnit,
        LinkedBlockingQueue(capacity)
    )

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        executor.execute(block)
    }

    override fun close() {
        executor.shutdown()
    }
}