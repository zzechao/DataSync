package com.zhouz.datasync

import android.app.Application
import com.zhouz.datasync.log.DefaultLog
import com.zhouz.datasync.log.ILog
import com.zhouz.datasync.log.ILoggerFactory
import com.zhouz.datasync.log.LoggerFactory


/**
 * @author:zhouz
 * @date: 2024/3/28 18:59
 * description：Data外部调用接口
 */
object DataWatcher {

    internal var logger: ILoggerFactory = LoggerFactory.getLogger("DataWatcher")
    internal var log: ILog = DefaultLog()

    internal lateinit var application: Application

    private val core by lazy {
        WorkerCore()
    }

    internal fun manualInstall(application: Application) {
        this.application = application
    }

    /**
     * 設置log文件
     */
    fun attachLog(log: ILog) {
        this.log = log
    }

    /**
     * 设置apt生成订阅构造类
     */
    fun setFactory(vararg factory: IDataSyncSubscriber) {
        core.addFactory(factory)
    }

    /**
     * 初始化或者更新订阅数据对象，用来进行
     */
    fun updateDiffer(observer: Any, differInit: (WatcherObject.() -> Unit)? = null) {
        core.dataWatchingMap[observer::class]?.let {
            it.watchers.firstOrNull { it.objectWeak.get() == observer }?.let { differInit?.invoke(it) }
        }
    }


    /**
     * 订阅
     */
    fun subscribe(observer: Any, differInit: (WatcherObject.() -> Unit)? = null) {
        core.dataSyncFactories.firstOrNull {
            it.getSubInfoBySubscriberClazz(observer::class)?.isNotEmpty() == true
        }?.let {
            it.getSubInfoBySubscriberClazz(observer::class)?.let { it1 -> core.watching(observer, it1, differInit) }
        }
    }

    /**
     * 取消订阅
     */
    fun unSubscribe(observer: Any) {
        core.unWatching(observer)
    }


    /**
     * 发送更新
     */
    fun <T : IDataEvent> sendData(data: T) {
        logger.i("postData data:$data")
        core.sendData(data)
    }
}