package com.zhouz.datasync

import com.google.auto.service.AutoService
import com.sun.tools.javac.code.Symbol
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement


/**
 * @author:zhouz
 * @date: 2024/3/12 18:51
 * description：数据同步处理器
 */
@AutoService(Processor::class)
class DataSyncProcessor : AbstractProcessor() {


    @Volatile
    private var isInit = false
    private lateinit var logger: Logger

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        if (!isInit) {
            isInit = true
            logger = Logger(processingEnv?.messager)
            logger.info("init end")
        }
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        val annotations: MutableSet<String> = LinkedHashSet()
        annotations.add(DataSyncBuild::class.java.canonicalName)
        return annotations
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        logger.info("start process")
        roundEnv?.getElementsAnnotatedWith(DataSyncBuild::class.java)?.forEach {
            if (it.kind != ElementKind.METHOD) {
                return@forEach
            }
            (it as Symbol.MethodSymbol).params
        }
        return false
    }
}