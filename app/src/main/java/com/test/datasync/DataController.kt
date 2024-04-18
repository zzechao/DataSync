package com.test.datasync

import android.util.Log
import com.zhouz.datasync.DataObserver
import com.zhouz.datasync.DataWatcher
import com.zhouz.datasync.Dispatcher
import com.zhouz.datasync.IDataDiffer


/**
 * @author:zhouz
 * @date: 2024/3/19 17:19
 * description：TODO
 */
class DataController : SuperController() {

    private var curData: Data = Data(0, 0, "zzc0")
    private var curData1: Data1 = Data1(0)

    init {
        DataWatcher.subscribe(this) {
            setDataDiffer(curData, object : IDataDiffer<Data> {
                override fun areDataSame(oloData: Data?, newData: Data): Boolean {
                    Log.i("DataController", "areDataSame  oloData:$oloData newData:$newData")
                    return oloData?.id == newData.id
                }

                override fun isContentChange(oloData: Data?, newData: Data): Boolean {
                    Log.i("DataController", "isContentChange oloData:$oloData newData:$newData")
                    return oloData?.test == newData.test
                }
            })
        }
    }


    @DataObserver(threadName = Dispatcher.AsyncOrder)
    override fun onData2Change(data: Data) {
        Log.i("DataController", "onData2Change data:$data $this ${Thread.currentThread().name}")
    }

//    @DataObserver(threadName = Dispatcher.Main)
//    fun onData2Change(data1: Data1) {
//
//    }

//    @DataObserver(threadName = Dispatcher.Main)
//    fun onDataSameChange(data: com.test.datasync.data.Data) {
//
//    }
}