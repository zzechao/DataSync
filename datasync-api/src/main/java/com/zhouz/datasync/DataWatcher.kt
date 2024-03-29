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

    private val constant by lazy {
        WorkerConstant()
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
        constant.addFactory(factory)
    }

    /**
     * 初始化或者更新订阅数据对象，用来进行
     */
    fun updateSubscriberData(observer: Any, vararg dataDiffer: DataDiffer<IDataEvent>) {
    }


    /**
     * 订阅
     */
    fun subscribe(observer: Any) {
        constant.dataSyncFactories.forEach {
            it.getSubInfoBySubscriberClazz(observer::class)
        }
    }

    /**
     * 取消订阅
     */
    fun unSubscribe(observer: Any) {
    }
}