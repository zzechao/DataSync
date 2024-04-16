package com.zhouz.datasync

import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2024/3/28 19:12
 * description：任务模块
 */
class Work {

    var dataSyncSubscriberInfo: DataSyncSubscriberInfo<out IDataEvent>? = null
    var dataClazz: KClass<out IDataEvent>? = null
    var data: IDataEvent? = null

    /**
     * 数据释放
     */
    fun release(): Work {
        dataSyncSubscriberInfo = null
        dataClazz = null
        data = null
        return this
    }
}