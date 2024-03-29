package com.zhouz.lib1

import com.zhouz.datasync.DataObserver
import com.zhouz.datasync.Dispatcher


/**
 * @author:zhouz
 * @date: 2024/3/29 12:11
 * description：模块测试查看build文件
 */
class BaseDataController1 {
    @DataObserver(
        threadName = Dispatcher.AsyncOrder
    )
    fun onDataChange(data: BaseData1) {
    }
}