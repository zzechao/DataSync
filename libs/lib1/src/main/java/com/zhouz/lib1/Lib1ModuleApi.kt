package com.zhouz.lib1

import com.google.auto.service.AutoService
import com.zhouz.baselib.ILib1ModuleApi
import com.zhouz.datasync.IDataSyncSubscriber


/**
 * @author:zhouz
 * @date: 2024/3/29 21:05
 * description：模块1
 */
@AutoService(ILib1ModuleApi::class)
class Lib1ModuleApi : ILib1ModuleApi {
    override fun loadFactory(): IDataSyncSubscriber {
        return DataSyncFactory()
    }
}