package com.zhouz.datasync

import java.util.concurrent.PriorityBlockingQueue
import kotlin.math.abs
import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2024/3/28 19:23
 * description：Dispatcher.BackgroundPost执行队列
 */
class AsyncOrderWorker : suspend () -> Unit {

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

    @Synchronized
    fun emit(work: Work): Boolean {
        queue.offer(work)
        return true
    }

    override suspend fun invoke() {
        val worker = queue.poll()
        DataWatcher.logger.i("AsyncOrderWorker invoke ${Thread.currentThread().name} workId:${worker?.workId}")
        worker?.let {
            invokeFunc(worker.dataSyncSubscriberInfo, worker.dataClazz, worker.data)
            DataWatcher.core.workerPools.recycler(it)
        }
    }

    private fun <T : IDataEvent> invokeFunc(
        dataSyncSubscriberInfo: DataSyncSubscriberInfo<out IDataEvent>?,
        dataClazz: KClass<out IDataEvent>?,
        data: T?
    ) {
        DataWatcher.logger.i("AsyncOrderWorker invokeFunc start thread:${Thread.currentThread().name}")
        dataSyncSubscriberInfo ?: return
        dataClazz ?: return
        data ?: return
        DataWatcher.core.dataWatchingMap[dataSyncSubscriberInfo.subscriberClazz]?.watchers?.forEach {
            val observer = it.objectWeak.get()
            it.findDataDiffer(dataClazz)?.let {
                if (!DataDifferUtil.checkData(it, data)) {
                    DataWatcher.logger.i("AsyncOrderWorker invokeFunc middle thread:${Thread.currentThread().name}")
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