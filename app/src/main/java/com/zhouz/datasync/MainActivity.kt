package com.zhouz.datasync

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repeat(10) {
            Thread {

            }.start()
        }
    }


    @DataSyncBuild
    fun onDataChange(data: Data) {
    }
}