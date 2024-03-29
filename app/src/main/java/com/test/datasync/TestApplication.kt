package com.test.datasync

import android.app.Application
import android.util.Log
import com.zhouz.baselib.ILib1ModuleApi
import com.zhouz.baselib.ILib2ModuleApi
import com.zhouz.datasync.DataWatcher
import java.util.ServiceLoader


/**
 * @author:zhouz
 * @date: 2024/3/29 17:39
 * description：TestApplication
 */
class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val factory = ServiceLoader.load(ILib1ModuleApi::class.java).firstOrNull()?.loadFactory()
        val factory2 = ServiceLoader.load(ILib2ModuleApi::class.java).firstOrNull()?.loadFactory()
        Log.i("zzc", "factories:${factory} $factory2")
        factory?.let {
            if (factory2 != null) {
                DataWatcher.setFactory(DataSyncFactory(), com.zhouz.baselib.DataSyncFactory(), it, factory2)
            }
        }
    }
}