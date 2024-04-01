package com.test.datasync

import com.zhouz.datasync.DataObserver
import com.zhouz.datasync.DataWatcher
import com.zhouz.datasync.Dispatcher
import com.zhouz.datasync.IDataDiffer


/**
 * @author:zhouz
 * @date: 2024/3/19 17:19
 * description：TODO
 */
class DataController {

    private var curData: Data = Data(0, 0, "zzc0")
    private var curData1: Data1 = Data1(0)

    init {
        DataWatcher.subscribe(this) {
            setDataDiffer(curData, object : IDataDiffer<Data> {
                override fun areDataSame(oloData: Data, newData: Data): Boolean {
                    return oloData.id == newData.id
                }

                override fun isContentChange(oloData: Data, newData: Data): Boolean {
                    return oloData.test == newData.test
                }
            })
        }
    }


    @DataObserver(threadName = Dispatcher.Origin)
    fun onDataChange(data: Data) {

    }

    @DataObserver(threadName = Dispatcher.Origin)
    fun onData2Change(data1: Data1) {

    }

    @DataObserver(threadName = Dispatcher.Origin)
    fun onDataSameChange(data: com.test.datasync.data.Data) {

    }
}