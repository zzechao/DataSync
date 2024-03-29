package com.zhouz.lib1

import com.zhouz.datasync.IDataDiffer


/**
 * @author:zhouz
 * @date: 2024/3/29 12:09
 * description：模块测试查看build文件
 */
data class BaseData1(val id: Long, val test: String) : IDataDiffer<BaseData1>