package com.zhouz.datasync.log

import android.util.Log

/**
 * @author:zhouz
 * @date: 2/24/21
 * description：
 */
open class DefaultLog : ILog {

    override fun v(tag: String, message: () -> Any?) {
    }

    override fun v(tag: String, message: String, vararg args: Any?) {
    }


    override fun d(tag: String, message: () -> Any?) {
    }

    override fun d(tag: String, message: String, vararg args: Any?) {
    }


    override fun i(tag: String, message: () -> Any?) {
    }

    override fun i(tag: String, message: String, vararg args: Any?) {
        Log.i(tag, message.format(*args))
    }


    override fun w(tag: String, message: () -> Any?) {
    }

    override fun w(tag: String, message: String, vararg args: Any?) {
    }

    override fun e(tag: String, message: String, error: Throwable?, vararg args: Any?) {
        Log.e(tag, message.format(*args), error)
    }
}