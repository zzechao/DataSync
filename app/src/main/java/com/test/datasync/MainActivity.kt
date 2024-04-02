package com.test.datasync

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhouz.datasync.DataObserver
import com.zhouz.datasync.DataWatcher
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    val controller = DataController()
    val controller1 = DataController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataWatcher.subscribe(this)
        setContentView(R.layout.activity_main)
        repeat(3) {
            thread { DataWatcher.sendData(Data(0, it, "zzc${it}")) }
        }
    }


    @DataObserver
    fun onDataChange(data: Data) {
    }

    @DataObserver
    fun onIntChange(value: Int) {

    }

    @DataObserver
    fun onListChange(list: List<Data>) {
    }

    @DataObserver
    fun onMutableListChange(list: MutableList<Data>) {

    }

    override fun onDestroy() {
        super.onDestroy()
        DataWatcher.unSubscribe(this)
    }
}