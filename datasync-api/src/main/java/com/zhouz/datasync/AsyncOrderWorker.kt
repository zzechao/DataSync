package com.zhouz.datasync

import java.util.PriorityQueue
import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2024/3/28 19:23
 * description：Dispatcher.BackgroundPost执行队列
 */
class AsyncOrderWorker : suspend () -> Unit {

    private val queue by lazy { PriorityQueue<Work>() }

    fun emit(work: Work): Boolean {
        queue.offer(work)
        return true
    }

    override suspend fun invoke() {
        val worker = queue.poll()
        worker?.let {
            invokeFunc(worker.dataSyncSubscriberInfo, worker.dataClazz, worker.data)
        }
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
                if (!DataDifferUtil.checkData(it, data)) {
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