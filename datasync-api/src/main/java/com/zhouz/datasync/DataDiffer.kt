package com.zhouz.datasync


/**
 * @author:zhouz
 * @date: 2024/3/29 16:45
 * description：差分数据模型
 */
class DataDiffer<T : IDataEvent>(val curData: T, val differ: IDataDiffer<T>? = null)