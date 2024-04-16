package com.zhouz.datasync

import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2024/3/28 19:14
 * description：对象池接口
 */
interface IPools {

    fun <D : IDataEvent> obtain(
        dataSyncSubscriberInfo: DataSyncSubscriberInfo<out IDataEvent>,
        dataClazz: KClass<out IDataEvent>,
        data: D
    ): Work

    fun recycler(data: Work)
}