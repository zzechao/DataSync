package com.test.datasync

import com.zhouz.datasync.DataSyncObserver


/**
 * @author:zhouz
 * @date: 2024/3/19 17:19
 * description：TODO
 */
class DataController1 : SuperController() {
    @DataSyncObserver()
    fun onDataChange(data: Data) {
    }
}