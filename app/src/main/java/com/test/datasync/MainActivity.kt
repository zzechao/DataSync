package com.test.datasync

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhouz.datasync.DataSyncObserver
import com.zhouz.datasync.R


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    @DataSyncObserver
    fun onDataChange(data: Data) {
    }

    @DataSyncObserver
    fun onIntChange(value: Int) {

    }

    @DataSyncObserver
    fun onListChange(list: List<Data>) {

    }

    @DataSyncObserver
    fun onMutableListChange(list: MutableList<Data>) {

    }
}