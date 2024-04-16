package com.zhouz.datasync

import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2024/3/28 19:13
 * description：任务对象池
 */
class WorkPools : IPools {

    private var mPools: Array<Work?> = arrayOfNulls(50)

    @Volatile
    private var mPoolSize = 50

//    override fun <T : IDataEvent> obtain(
//        dataSyncSubscriberInfo: DataSyncSubscriberInfo<out IDataEvent>,
//        dataClazz: KClass<out IDataEvent>,
//        data: IDataEvent
//    ): Work<out IDataEvent> {
//        return acquire<T>()?.apply {
//            this.dataSyncSubscriberInfo = dataSyncSubscriberInfo
//            this.data = data
//            this.dataClazz = dataClazz
//        } ?: Work<IDataEvent>().apply {
//            this.dataSyncSubscriberInfo = dataSyncSubscriberInfo
//            this.data = data
//            this.dataClazz = dataClazz
//        }
//    }
//
//    override fun recycler(data: Work<out IDataEvent>) {
//        release(data.release())
//    }


    private fun acquire(): Work? {
        return if (mPoolSize > 0) {
            DataWatcher.logger.i("acquire mPoolSize:$mPoolSize")
            val lastPooledIndex = mPoolSize - 1
            val instance: Work? = mPools[lastPooledIndex]
            mPools[lastPooledIndex] = null
            --mPoolSize
            instance
        } else {
            null
        }
    }


    private fun release(instance: Work): Boolean {
        return when {
            isInPool(instance) -> {
                DataWatcher.logger.i("release isInPool")
                false
            }

            mPoolSize < mPools.size -> {
                DataWatcher.logger.i("release mPoolSize:$mPoolSize")
                mPools[mPoolSize] = instance
                ++mPoolSize
                true
            }

            else -> {
                DataWatcher.logger.i("release")
                false
            }
        }
    }

    private fun isInPool(instance: Work): Boolean {
        for (i in 0 until mPoolSize) {
            if (mPools[i] === instance) {
                return true
            }
        }
        return false
    }

    override fun <D : IDataEvent> obtain(
        dataSyncSubscriberInfo: DataSyncSubscriberInfo<out IDataEvent>,
        dataClazz: KClass<out IDataEvent>,
        data: D
    ): Work {
        return acquire()?.apply {
            this.dataSyncSubscriberInfo = dataSyncSubscriberInfo
            this.data = data
            this.dataClazz = dataClazz
        } ?: Work().apply {
            this.dataSyncSubscriberInfo = dataSyncSubscriberInfo
            this.data = data
            this.dataClazz = dataClazz
        }
    }

    override fun recycler(data: Work) {
        release(data.release())
    }
}