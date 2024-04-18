package com.zhouz.datasync

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.PriorityBlockingQueue
import kotlin.math.abs
import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2024/3/28 19:22
 * description：description：Dispatcher.Background执行队列
 */
class AsyncWorker : suspend () -> Unit {

    private val queue by lazy {
        PriorityBlockingQueue<Work>(20, Comparator { work1, work2 ->
            val diff = work1.priority() - work2.priority()
            return@Comparator if (diff == 0) {
                val diff2 = work1.workId - work2.workId
                if (diff2 == 0) {
                    0
                } else {
                    diff2 / abs(diff2)
                }
            } else {
                diff / abs(diff)
            }
        })
    }
    private var mCompletableDeferred: CompletableDeferred<Boolean>? = null

    @Volatile
    private var isRunning = false

    @Synchronized
    fun emit(work: Work): Boolean {
        DataWatcher.logger.i("AsyncWorker emit ${mCompletableDeferred == null} isRunning:$isRunning")
        queue.offer(work)
        if (!isRunning) {
            isRunning = true
            mCompletableDeferred?.takeIf {
                it.isActive
            }?.let {
                it.complete(true)
                return false
            } ?: return true
        }
        return false
    }

    override suspend fun invoke() {
        DataWatcher.logger.i("AsyncWorker invoke ${Thread.currentThread().name} $isRunning")
        withContext(CoroutineExceptionHandler { _, ex ->
            DataWatcher.logger.e("error", ex)
            isRunning = false
        }) {
            while (true) {
                var worker = queue.poll()
                DataWatcher.logger.i("AsyncWorker invoke 1 ${worker?.workId} ${Thread.currentThread().name}")
                if (worker == null) {
                    val value = withTimeoutOrNull(2000L) {
                        if (mCompletableDeferred == null || mCompletableDeferred?.isActive != true) {
                            mCompletableDeferred =
                                newWorkerCompletableDeferred(this.coroutineContext[Job])
                        }
                        mCompletableDeferred?.await()
                    }
                    DataWatcher.logger.i("AsyncWorker invoke 2 $value")
                    if (value == true) {
                        worker = queue.poll()
                        DataWatcher.logger.i("AsyncWorker invoke await ${worker?.workId} ${Thread.currentThread().name}")
                    } else {
                        return@withContext
                    }
                }
                DataWatcher.logger.i("AsyncWorker invoke 3 $worker")
                worker?.let {
                    invokeFunc(worker.dataSyncSubscriberInfo, worker.dataClazz, worker.data)
                    DataWatcher.core.workerPools.recycler(it)
                } ?: return@withContext
            }
        }
        isRunning = false
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
                    DataWatcher.logger.i("AsyncWorker invokeFunc method.invoke thread:${Thread.currentThread().name}")
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