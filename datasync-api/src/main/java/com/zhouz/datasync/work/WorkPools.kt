package com.zhouz.datasync.work


/**
 * @author:zhouz
 * @date: 2024/3/28 19:13
 * description：任务对象池
 */
class WorkPools : IPools<Work> {
    override fun obtain(): Work? {
        return null
    }

    override fun recycler(data: Work) {
        data.release()
    }
}