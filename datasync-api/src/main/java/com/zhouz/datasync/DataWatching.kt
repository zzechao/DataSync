package com.zhouz.datasync

import android.util.Log
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2024/4/1 14:44
 * description：订阅者的对象和当前对象数据
 */
data class DataWatching(val infos: MutableList<DataSyncSubscriberInfo<out IDataEvent>>, val watchers: MutableList<WatcherObject>)


data class WatcherObject(val objectWeak: WeakReference<Any>) {
    val dataDifferMap = ConcurrentHashMap<KClass<out IDataEvent>, DataDiffer<out IDataEvent>>()

    inline fun <reified T : IDataEvent> setDataDiffer(data: T?, differ: IDataDiffer<T>) {
        Log.i("DataWatcher", "setDataDiffer ${T::class}")
        dataDifferMap[T::class] = DataDiffer(data, differ)
    }
}

/**
 * 找到对应的Differ
 */
internal fun WatcherObject.findDataDiffer(clazz: KClass<out IDataEvent>): DataDiffer<out IDataEvent>? = dataDifferMap[clazz]


/**
 * 差分数据模型
 */
data class DataDiffer<T : IDataEvent>(var curData: T?, val differ: IDataDiffer<out IDataEvent>? = null)