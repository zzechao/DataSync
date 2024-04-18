package com.test.datasync

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.zhouz.datasync.DataWatcher
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    val controller = DataController()
    val controller1 = DataController()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataWatcher.subscribe(this)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.mTextView).setOnClickListener {
            repeat(3) {
                thread { DataWatcher.sendData(Data(0, it, "zzc${it}")) }
            }
        }
    }


//    @DataObserver
//    fun onDataChange(data: Data) {
//    }

    override fun onDestroy() {
        super.onDestroy()
        DataWatcher.unSubscribe(this)
    }
}