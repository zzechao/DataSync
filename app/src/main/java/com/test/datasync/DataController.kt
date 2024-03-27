package com.test.datasync

import com.zhouz.datasync.DataSyncObserver
import com.zhouz.datasync.Dispatcher


/**
 * @author:zhouz
 * @date: 2024/3/19 17:19
 * description：TODO
 */
class DataController {
    @DataSyncObserver(
        threadName = Dispatcher.Async
    )
    fun onDataChange(data: Data) {
    }

    @DataSyncObserver
    fun onData2Change(data1: Data1) {

    }

    @DataSyncObserver
    fun onDataSameChange(data: com.test.datasync.data.Data) {

    }
}