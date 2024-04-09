package com.zhouz.datasync

import com.zhouz.datasync.work.Work
import java.util.PriorityQueue


/**
 * @author:zhouz
 * @date: 2024/3/28 19:22
 * description：description：Dispatcher.Background执行队列
 */
class AsyncWorker : suspend () -> Unit {

    private val queue by lazy { PriorityQueue<Work>() }

    fun emit(work: Work): Boolean {
        queue.offer(work)
        return true
    }

    override suspend fun invoke() {
        while (true) {

        }
    }
}