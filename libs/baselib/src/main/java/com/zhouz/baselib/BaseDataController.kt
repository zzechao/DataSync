package com.zhouz.baselib

import com.zhouz.datasync.DataObserver
import com.zhouz.datasync.Dispatcher


/**
 * @author:zhouz
 * @date: 2024/3/29 12:11
 * description：模块测试查看build文件
 */
class BaseDataController {
    @DataObserver(
        threadName = Dispatcher.AsyncOrder
    )
    fun onDataChange(data: BaseData) {
    }
}