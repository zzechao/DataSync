package com.zhouz.lib2

import com.google.auto.service.AutoService
import com.zhouz.baselib.ILib2ModuleApi
import com.zhouz.datasync.IDataSyncSubscriber


/**
 * @author:zhouz
 * @date: 2024/3/29 21:07
 * description：模块2
 */
@AutoService(ILib2ModuleApi::class)
class Lib2ModuleApi : ILib2ModuleApi {
    override fun loadFactory(): IDataSyncSubscriber {
        return DataSyncFactory()
    }
}