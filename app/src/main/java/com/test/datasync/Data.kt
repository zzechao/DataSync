package com.test.datasync

import com.zhouz.datasync.IDataEvent


/**
 * @author:zhouz
 * @date: 2024/3/15 17:37
 * description：数据模型
 */
data class Data(val id: Int, val test: Int = 1, val name: String = "") : IDataEvent


data class Data1(val test2: Int = 1) : IDataEvent