package com.test.datasync

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhouz.datasync.DataObserver
import com.zhouz.datasync.R


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
}