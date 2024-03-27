package com.test.datasync

import com.zhouz.datasync.DataSyncBuild
import com.zhouz.datasync.Dispatcher


/**
 * @author:zhouz
 * @date: 2024/3/19 17:19
 * description：TODO
 */
class DataController {
    @DataSyncBuild(
        threadName = Dispatcher.Async, filedNames = ["test", "name"]
    )
    fun onDataChange(data: Data) {
    }

    @DataSyncBuild
    fun onData2Change(data1: Data1) {

    }

    @DataSyncBuild
    fun onDataSameChange(data: com.test.datasync.data.Data) {

    }
}