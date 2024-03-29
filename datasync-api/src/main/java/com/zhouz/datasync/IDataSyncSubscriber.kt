package com.zhouz.datasync

import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2024/3/18 12:31
 * description: 数据同步处理定义接口
 */
interface IDataSyncSubscriber {
    fun getSubscriberClazzByDataClazz(clazz: KClass<out IDataEvent>): MutableList<KClass<*>>?

    fun getSubInfoBySubscriberClazz(clazz: KClass<*>): MutableList<DataSyncSubscriberInfo<out IDataEvent>>?
}