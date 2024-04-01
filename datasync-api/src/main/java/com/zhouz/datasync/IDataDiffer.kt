package com.zhouz.datasync

/**
 * 数据观察者
 */
interface IDataDiffer<T : IDataEvent> {

    fun areDataSame(oloData: T, newData: T): Boolean = true

    fun isContentChange(oloData: T, newData: T): Boolean = false
}