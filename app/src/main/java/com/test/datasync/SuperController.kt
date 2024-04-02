package com.test.datasync

import android.util.Log
import com.zhouz.datasync.DataObserver
import com.zhouz.datasync.Dispatcher


/**
 * @author:zhouz
 * @date: 2024/3/22 13:17
 * description：测试父类的订阅关系
 */
open class SuperController {

    @DataObserver()
    fun onSuperDataChange(data: Data) {
    }

    @DataObserver(Dispatcher.Origin)
    open fun onData2Change(data: Data) {
        Log.i("DataController", "super onData2Change data:$data $this")
    }
}