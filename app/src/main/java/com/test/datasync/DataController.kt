package com.test.datasync

import com.zhouz.datasync.DataObserver
import com.zhouz.datasync.Dispatcher


/**
 * @author:zhouz
 * @date: 2024/3/19 17:19
 * description：TODO
 */
class DataController {
    @DataObserver(
        threadName = Dispatcher.AsyncOrder
    )
    fun onDataChange(data: Data) {
    }

    @DataObserver
    fun onData2Change(data1: Data1) {

    }

    @DataObserver
    fun onDataSameChange(data: com.test.datasync.data.Data) {

    }
}