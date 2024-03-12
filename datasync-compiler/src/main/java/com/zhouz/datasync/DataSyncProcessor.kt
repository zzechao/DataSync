package com.zhouz.datasync

import com.google.auto.service.AutoService
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement


/**
 * @author:zhouz
 * @date: 2024/3/12 18:51
 * description：数据同步处理器
 */
@AutoService(Processor::class)
class DataSyncProcessor : AbstractProcessor() {
    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        return false
    }
}