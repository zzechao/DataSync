package com.zhouz.datasync

import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2024/3/28 19:13
 * description：任务对象池
 */
class WorkPools : IPools<Work<out IDataEvent>> {

    private var mPools: Array<Work<out IDataEvent>?> = arrayOfNulls<Work<out IDataEvent>?>(50)

    @Volatile
    private var mPoolSize = 50

    override fun <D : IDataEvent> obtain(
        dataSyncSubscriberInfo: DataSyncSubscriberInfo<out IDataEvent>,
        dataClazz: KClass<out IDataEvent>,
        data: D
    ): Work<out IDataEvent> {
        return acquire()?.apply {
            this.dataSyncSubscriberInfo = dataSyncSubscriberInfo
            this.data = data
            this.dataClazz = dataClazz
        } ?: Work<IDataEvent>().apply {
            this.dataSyncSubscriberInfo = dataSyncSubscriberInfo
            this.data = data
            this.dataClazz = dataClazz
        }
    }

    override fun recycler(data: Work<out IDataEvent>) {
        release(data.release())
    }


    private fun acquire(): Work<out IDataEvent>? {
        return if (mPoolSize > 0) {
            DataWatcher.logger.i("acquire mPoolSize:$mPoolSize")
            val lastPooledIndex = mPoolSize - 1
            val instance: Work<out IDataEvent>? = mPools[lastPooledIndex]
            mPools[lastPooledIndex] = null
            --mPoolSize
            instance
        } else {
            null
        }
    }


    private fun release(instance: Work<out IDataEvent>): Boolean {
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

    private fun isInPool(instance: Work<out IDataEvent>): Boolean {
        for (i in 0 until mPoolSize) {
            if (mPools[i] === instance) {
                return true
            }
        }
        return false
    }
}