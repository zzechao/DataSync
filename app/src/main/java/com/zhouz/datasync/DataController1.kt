package com.zhouz.datasync


/**
 * @author:zhouz
 * @date: 2024/3/19 17:19
 * description：TODO
 */
class DataController1 {
    @DataSyncBuild(filedNames = ["test"])
    fun onDataChange(data: Data) {
    }
}