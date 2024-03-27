package com.test.datasync

import com.zhouz.datasync.DataSyncBuild


/**
 * @author:zhouz
 * @date: 2024/3/19 17:19
 * description：TODO
 */
class DataController1 : SuperController() {
    @DataSyncBuild(filedNames = ["test"])
    fun onDataChange(data: Data) {
    }
}