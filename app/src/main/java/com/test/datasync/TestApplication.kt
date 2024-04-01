package com.test.datasync

import android.app.Application
import android.util.Log
import com.silencedut.hub.Hub
import com.zhouz.baselib.ILib1ModuleApi
import com.zhouz.baselib.ILib2ModuleApi
import com.zhouz.datasync.DataWatcher


/**
 * @author:zhouz
 * @date: 2024/3/29 17:39
 * description：TestApplication
 */
class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val factory = Hub.getImpl(ILib1ModuleApi::class.java).loadFactory()
        val factory2 = Hub.getImpl(ILib2ModuleApi::class.java).loadFactory()
        Log.i("zzc", "factories:${factory} $factory2")
        DataWatcher.setFactory(DataSyncFactory(), com.zhouz.baselib.DataSyncFactory(), factory, factory2)
    }
}