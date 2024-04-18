package com.zhouz.datasync

import java.util.concurrent.atomic.AtomicInteger
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

    private val workIdCalculate = AtomicInteger(0)

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
                mPools[mPoolSize] = instance
                ++mPoolSize
                DataWatcher.logger.i("release mPoolSize:$mPoolSize")
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
        synchronized(this) {
            return acquire()?.apply {
                this.workId = workIdCalculate.getAndIncrement()
                this.dataSyncSubscriberInfo = dataSyncSubscriberInfo
                this.data = data
                this.dataClazz = dataClazz
            } ?: Work().apply {
                this.workId = workIdCalculate.getAndIncrement()
                this.dataSyncSubscriberInfo = dataSyncSubscriberInfo
                this.data = data
                this.dataClazz = dataClazz
            }
        }

    }


    override fun recycler(data: Work) {
        synchronized(this) {
            release(data.release())
        }
    }
}