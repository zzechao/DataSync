package com.test.datasync.data

import com.zhouz.datasync.IDataDiffer


/**
 * @author:zhouz
 * @date: 2024/3/21 19:46
 * description：相同class的构造测试
 */
data class Data(val test: Int = 1) : IDataDiffer<Data>