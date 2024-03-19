package com.zhouz.datasync

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class DataSyncBuild(
    val threadName: ThreadMode = ThreadMode.MAIN, // 线程模式
    val filedNames: Array<String> = [] // 差分匹配的成员变量浅比较, 默认不比较
)
