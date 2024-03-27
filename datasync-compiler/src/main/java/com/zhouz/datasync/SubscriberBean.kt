package com.zhouz.datasync

import javax.lang.model.element.Name
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror


/**
 * @author:zhouz
 * @date: 2024/3/20 14:49
 * description：订阅组装类
 */
class SubscriberBean(
    val type: TypeMirror,
    val funcName: Name,
    val dispatcher: Dispatcher,
    val filedNames: Array<String>,
    val clazzElement: TypeElement
)