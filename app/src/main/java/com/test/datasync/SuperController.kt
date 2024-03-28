package com.test.datasync

import com.zhouz.datasync.DataObserver


/**
 * @author:zhouz
 * @date: 2024/3/22 13:17
 * description：测试父类的订阅关系
 */
open class SuperController {

    @DataObserver()
    fun onSuperDataChange(data: Data) {
    }
}