package com.zhouz.datasync

import android.os.Handler
import android.os.Looper
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2024/3/28 19:02
 * description：订阅逻辑核心类
 */
class WorkerCore {
    private val threadName = "DataSyncThreadPool"

    private val context =
        Executors.newCachedThreadPool { r -> Thread(r, threadName) }.asCoroutineDispatcher()

    internal val workerScope =
        CoroutineScope(SupervisorJob() + context + CoroutineExceptionHandler { _, _ ->

        })

    /**
     * 订阅查找类
     */
    internal val dataSyncFactories = CopyOnWriteArraySet<IDataSyncSubscriber>()

    /**
     * 缓存data和订阅Clazz的数据
     */
    private val cacheMapObserverByData:
            Cache<KClass<out IDataEvent>, MutableList<DataSyncSubscriberInfo<out IDataEvent>>> =
        CacheBuilder.newBuilder()
            .concurrencyLevel(4)
            .maximumSize(10)
            .initialCapacity(5)
            .expireAfterWrite(60 * 2, TimeUnit.SECONDS)
            .expireAfterAccess(60 * 2, TimeUnit.SECONDS)
            .build()

    /**
     * 订阅中的clazz和对象信息列表
     */
    internal val dataWatchingMap = ConcurrentHashMap<KClass<*>, DataWatching>()

    /**
     * 引用队列
     */
    internal val referenceQueue = ReferenceQueue<Any>()

    /**
     * 注解处理factory
     */
    fun addFactory(factory: Array<out IDataSyncSubscriber>) {
        dataSyncFactories.addAll(factory)
    }

    /**
     * 正在订阅的对象以及func信息
     */
    fun watching(
        observer: Any, infos: MutableList<DataSyncSubscriberInfo<out IDataEvent>>,
        differInit: (WatcherObject.() -> Unit)? = null
    ) {
        DataWatcher.logger.i("watching $observer ${infos.size}")
        dataWatchingMap[observer::class]?.let {
            if (it.watchers.none { it.objectWeak.get() == observer }) {
                it.watchers.add(
                    WatcherObject(
                        WeakReference(
                            observer,
                            referenceQueue
                        )
                    ).apply { differInit?.invoke(this) })
            }
        } ?: kotlin.run {
            val watchers = mutableListOf<WatcherObject>()
            watchers.add(
                WatcherObject(
                    WeakReference(
                        observer,
                        referenceQueue
                    )
                ).apply { differInit?.invoke(this) })
            val dataWatching = DataWatching(infos, watchers)
            dataWatchingMap[observer::class] = dataWatching
        }
    }

    /**
     * 取消订阅
     */
    fun unWatching(observer: Any) {
        dataWatchingMap[observer::class]?.let { dataWatch ->
            dataWatch.watchers.firstOrNull { it.objectWeak.get() == observer }?.let {
                dataWatch.watchers.remove(it)
            }
        }
    }

    /**
     * post数据
     */
    fun <T : IDataEvent> sendData(data: T) {
        tryCatch {
            val dataClazz = data::class
            DataWatcher.logger.i("sendData start data:$data dataClazz:$dataClazz")
            cacheMapObserverByData.get(dataClazz) {
                DataWatcher.logger.i("sendData loader")
                val listObserver = mutableListOf<KClass<*>>()
                dataSyncFactories.forEach {
                    it.getSubscriberClazzByDataClazz(dataClazz)?.let {
                        listObserver.addAll(it)
                    }
                }
                val dataSyncSubscriberInfoList =
                    mutableListOf<DataSyncSubscriberInfo<out IDataEvent>>()
                listObserver.forEach { observerClazz ->
                    dataSyncFactories.forEach {
                        it.getSubInfoBySubscriberClazz(observerClazz)
                            ?.filter { it.dataClazz == dataClazz }?.let {
                                dataSyncSubscriberInfoList.addAll(it)
                            }
                    }
                }
                dataSyncSubscriberInfoList
            }.let {
                DataWatcher.logger.i("sendData end dataClazz:$dataClazz data:$data dataSyncSubscriberInfoList:${it.map { it.toString() }} dataWatchingMap:${dataWatchingMap.size}")
                it.forEach { dataSyncSubscriberInfo ->
                    if (dataWatchingMap.containsKey(dataSyncSubscriberInfo.subscriberClazz)) {
                        DataWatcher.logger.i("sendData subscriberClazz:${dataSyncSubscriberInfo.subscriberClazz} method:${dataSyncSubscriberInfo.methodName} dispatch:${dataSyncSubscriberInfo.dispatcher}")
                        when (dataSyncSubscriberInfo.dispatcher) {
                            Dispatcher.Origin -> { // 当前线程
                                invokeFunc(dataSyncSubscriberInfo, dataClazz, data)
                            }

                            Dispatcher.Main -> { // 主线程
                                if (Thread.currentThread() == Looper.getMainLooper().thread) { // 主线程直接运行
                                    invokeFunc(dataSyncSubscriberInfo, dataClazz, data)
                                } else {
                                    val handler = Handler(Looper.getMainLooper())
                                    handler.post {
                                        invokeFunc(dataSyncSubscriberInfo, dataClazz, data)
                                    }
                                }
                            }

                            Dispatcher.MainPost -> {
                                val handler = Handler(Looper.getMainLooper())
                                handler.post {
                                    invokeFunc(dataSyncSubscriberInfo, dataClazz, data)
                                }
                            }

                            Dispatcher.Async -> {

                            }

                            Dispatcher.AsyncOrder -> {

                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun <T : IDataEvent> invokeFunc(
        dataSyncSubscriberInfo: DataSyncSubscriberInfo<out IDataEvent>,
        dataClazz: KClass<out T>,
        data: T
    ) {
        dataWatchingMap[dataSyncSubscriberInfo.subscriberClazz]?.watchers?.forEach {
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