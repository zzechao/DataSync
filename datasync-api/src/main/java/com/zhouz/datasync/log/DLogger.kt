package com.zhouz.datasync.log

import com.zhouz.datasync.DataWatcher


/**
 * @author:zhouz
 * @date: 2023/12/12 12:14
 * description：构造类
 */
internal class DLogger(private val tag: String) : ILoggerFactory, ILog {
    override fun i(message: String, vararg args: Any?) {
        DataWatcher.log.i(tag, message, args)
    }

    override fun w(message: String, vararg args: Any?) {
        DataWatcher.log.w(tag, message, args)
    }

    override fun e(message: String, error: Throwable?, vararg args: Any?) {
        DataWatcher.log.e(tag, message, error, args)
    }

    override fun v(message: String, vararg args: Any?) {
        DataWatcher.log.v(tag, message, args)
    }

    override fun d(message: String, vararg args: Any?) {
        DataWatcher.log.d(tag, message, args)
    }

    override fun i(tag: String, message: () -> Any?) {
        DataWatcher.log.i(tag, message)
    }

    override fun i(tag: String, message: String, vararg args: Any?) {
        DataWatcher.log.i(tag, message, *args)
    }

    override fun w(tag: String, message: () -> Any?) {
        DataWatcher.log.w(tag, message)
    }

    override fun w(tag: String, message: String, vararg args: Any?) {
        DataWatcher.log.w(tag, message, *args)
    }

    override fun v(tag: String, message: () -> Any?) {
        DataWatcher.log.v(tag, message)
    }

    override fun v(tag: String, message: String, vararg args: Any?) {
        DataWatcher.log.v(tag, message, *args)
    }

    override fun d(tag: String, message: () -> Any?) {
        DataWatcher.log.d(tag, message)
    }

    override fun d(tag: String, message: String, vararg args: Any?) {
        DataWatcher.log.d(tag, message, *args)
    }
}