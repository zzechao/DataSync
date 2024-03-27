package com.test.datasync

import com.zhouz.datasync.IDataDiffer


/**
 * @author:zhouz
 * @date: 2024/3/15 17:37
 * description：数据模型
 */
data class Data(val id: Int, val test: Int = 1, val name: String = "") : IDataDiffer<Data> {
    override fun areDataSame(newData: Data): Boolean {
        return this.id == newData.id
    }

    override fun isContentChange(newData: Data): Boolean {
        return this.test == newData.test
    }
}


data class Data1(val test2: Int = 1) : IDataDiffer<Data1>