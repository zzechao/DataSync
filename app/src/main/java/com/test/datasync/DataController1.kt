package com.test.datasync

import com.zhouz.datasync.DataObserver


/**
 * @author:zhouz
 * @date: 2024/3/19 17:19
 * description：TODO
 */
class DataController1 : SuperController() {
    @DataObserver()
    fun onDataChange(data: Data) {
    }
}