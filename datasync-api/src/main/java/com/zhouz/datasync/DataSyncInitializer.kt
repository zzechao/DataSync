package com.zhouz.datasync

import android.app.Application
import android.content.ContentProvider

internal sealed class DataSyncInitializer : ContentProvider() {
    override fun onCreate(): Boolean {
        val application = context?.applicationContext as Application
        DataWatcher.manualInstall(application) //真正初始化之处
        return true
    }
}
