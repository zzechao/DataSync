package com.zhouz.lib1

import com.silencedut.hub_annotation.HubInject
import com.zhouz.baselib.ILib1ModuleApi
import com.zhouz.datasync.IDataSyncSubscriber


/**
 * @author:zhouz
 * @date: 2024/3/29 21:05
 * description：模块1
 */
@HubInject(api = [ILib1ModuleApi::class])
class Lib1ModuleApi : ILib1ModuleApi {
    override fun onCreate() {
    }

    override fun loadFactory(): IDataSyncSubscriber {
        return DataSyncFactory()
    }
}