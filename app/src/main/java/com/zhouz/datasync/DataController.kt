package com.zhouz.datasync


/**
 * @author:zhouz
 * @date: 2024/3/19 17:19
 * description：TODO
 */
class DataController {
    @DataSyncBuild(
        threadName = ThreadMode.AYSN, filedNames = ["test", "name"]
    )
    fun onDataChange(data: Data) {
    }

    @DataSyncBuild
    fun onData2Change(data1: Data1) {

    }
}