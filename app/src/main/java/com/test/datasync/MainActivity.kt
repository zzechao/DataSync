package com.test.datasync

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhouz.datasync.DataSyncBuild
import com.zhouz.datasync.R


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    @DataSyncBuild
    fun onDataChange(data: Data) {
    }
}