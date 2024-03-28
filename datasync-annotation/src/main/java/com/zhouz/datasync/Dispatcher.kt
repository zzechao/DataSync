package com.zhouz.datasync


/**
 * @author:zhouz
 * @date: 2024/3/19 17:55
 * description：线程模式
 */
enum class Dispatcher {
    /**
     * 主线程执行，如果当前在主线程则马上执行，否则post回主线程执行
     */
    Main,

    /**
     * post回主线程执行，如果当前在主线程则会延迟到下一次looper才执行
     */
    MainPost,

    /**
     * 异步线程并发执行
     */
    Async,

    /**
     * 异步线程有序执行
     */
    AsyncOrder,

    /**
     * 发通知的当前线程执行 若制定有生命周期则忽略制定的生命周期阶段声明
     */
    Origin
}
