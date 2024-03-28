package com.zhouz.datasync

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors


/**
 * @author:zhouz
 * @date: 2024/3/28 19:02
 * description：TODO
 */
class WorkerConstant {

    private val threadName = "DataSyncThreadPool"

    private val context =
        Executors.newCachedThreadPool { r -> Thread(r, threadName) }.asCoroutineDispatcher()

    internal val workerScope =
        CoroutineScope(SupervisorJob() + context + CoroutineExceptionHandler { _, _ ->

        })
}