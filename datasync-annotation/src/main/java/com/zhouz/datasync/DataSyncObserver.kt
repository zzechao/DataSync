package com.zhouz.datasync

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class DataSyncObserver(
    val threadName: Dispatcher = Dispatcher.Main, // 线程模式
)
