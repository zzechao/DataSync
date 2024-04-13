package com.zhouz.datasync

import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2024/3/28 19:13
 * description：任务对象池
 */
class WorkPools : IPools<Work<out IDataEvent>> {
    override fun <D : IDataEvent> obtain(
        dataSyncSubscriberInfo: DataSyncSubscriberInfo<out IDataEvent>,
        dataClazz: KClass<out IDataEvent>,
        data: D
    ): Work<out IDataEvent> {
        return Work<IDataEvent>().apply {
            this.dataSyncSubscriberInfo = dataSyncSubscriberInfo
            this.data = data
            this.dataClazz = dataClazz
        }
    }

    override fun recycler(data: Work<out IDataEvent>) {
        data.release()
    }
}