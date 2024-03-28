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

    internal fun manualInstall(application: Application) {
        this.application = application
    }

    /**
     * 設置log文件
     */
    fun attachLog(log: ILog) {
        this.log = log
    }
}