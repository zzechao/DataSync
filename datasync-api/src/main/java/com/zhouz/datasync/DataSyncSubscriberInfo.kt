package com.zhouz.datasync

import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2024/2/28 16:04
 * description：数据同步承载类
 */
class DataSyncSubscriberInfo<T : IDataDiffer<*>>(
    val methodName: String, // 方法
    val dataClazz: KClass<T>, // 数据KClass
    val subscriberClazz: KClass<*>, // 承载方法的KClass
    val dispatcher: Dispatcher, // 线程模式
    var curData: T? = null // 当前的值
)