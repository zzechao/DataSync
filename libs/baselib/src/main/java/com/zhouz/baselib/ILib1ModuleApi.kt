package com.zhouz.baselib

import com.silencedut.hub.IHub
import com.zhouz.datasync.IDataSyncSubscriber


/**
 * @author:zhouz
 * @date: 2024/3/29 20:39
 * description：组件化构建(Lib1)
 */
interface ILib1ModuleApi : IHub {
    fun loadFactory(): IDataSyncSubscriber
}