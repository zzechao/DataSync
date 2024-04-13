package com.zhouz.datasync


/**
 * @author:zhouz
 * @date: 2024/3/28 19:14
 * description：对象池接口
 */
interface IPools<T> {

    fun obtain(): T?

    fun recycler(data: T)
}