package com.zhouz.baselib

import com.silencedut.hub.IHub
import com.zhouz.datasync.IDataSyncSubscriber


/**
 * @author:zhouz
 * @date: 2024/3/29 21:06
 * description：组件化构建(Lib2)
 */
interface ILib2ModuleApi : IHub {
    fun loadFactory(): IDataSyncSubscriber
}