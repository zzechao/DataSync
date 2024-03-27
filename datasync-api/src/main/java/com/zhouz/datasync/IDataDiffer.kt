package com.zhouz.datasync

/**
 * 数据观察者
 */
interface IDataDiffer<T> {

    fun areDataSame(newData: T): Boolean = true

    fun isContentChange(newData: T): Boolean = true
}