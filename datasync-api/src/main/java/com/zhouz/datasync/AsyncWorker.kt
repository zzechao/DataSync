package com.zhouz.datasync

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull
import java.util.PriorityQueue
import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2024/3/28 19:22
 * description：description：Dispatcher.Background执行队列
 */
class AsyncWorker : suspend () -> Unit {

    private val queue by lazy { PriorityQueue<Work>() }
    private var mCompletableDeferred: CompletableDeferred<Boolean>? = null

    fun emit(work: Work): Boolean {
        queue.offer(work)
        mCompletableDeferred?.takeIf {
            it.isActive
        }?.let {
            it.complete(true)
            return false
        } ?: return true
    }

    override suspend fun invoke() {
        DataWatcher.logger.i("AsyncWorker invoke")
        coroutineScope {
            while (true) {
                var worker = queue.poll()
                if (worker == null) {
                    val value = withTimeoutOrNull(2000L) {
                        if (mCompletableDeferred == null || mCompletableDeferred?.isActive != true) {
                            mCompletableDeferred =
                                newWorkerCompletableDeferred(this.coroutineContext[Job])
                        }
                        mCompletableDeferred?.await()
                    }
                    if (value == true) {
                        worker = queue.poll()
                        worker?.let {
                            invokeFunc(worker.dataSyncSubscriberInfo, worker.dataClazz, worker.data)
                        } ?: return@coroutineScope
                    } else {
                        return@coroutineScope
                    }
                }
            }
        }
    }

    private fun newWorkerCompletableDeferred(job: Job? = null): CompletableDeferred<Boolean> {
        return CompletableDeferred(job)
    }

    private fun <T : IDataEvent> invokeFunc(
        dataSyncSubscriberInfo: DataSyncSubscriberInfo<out IDataEvent>?,
        dataClazz: KClass<out IDataEvent>?,
        data: T?
    ) {
        dataSyncSubscriberInfo ?: return
        dataClazz ?: return
        data ?: return
        DataWatcher.core.dataWatchingMap[dataSyncSubscriberInfo.subscriberClazz]?.watchers?.forEach {
            val observer = it.objectWeak.get()
            it.findDataDiffer(dataClazz)?.let {
                if (!DataDifferUtil.checkData(it, data).apply {
                    DataWatcher.logger.i("invokeFunc $this")
                    }) {
                    DataWatcher.logger.i("sendData method.invoke")
                    observer?.let {
                        val method = observer::class.java.getMethod(
                            dataSyncSubscriberInfo.methodName,
                            dataClazz.java
                        )
                        method.invoke(it, data)
                    }
                }
            }
        }
    }
}