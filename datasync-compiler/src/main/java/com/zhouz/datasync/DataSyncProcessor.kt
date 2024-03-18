package com.zhouz.datasync

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
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

    private val packageName = "com.zhouz.datasync"

    @Volatile
    private var isInit = false
    private lateinit var logger: Logger
    private lateinit var mFiler: Filer

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        if (!isInit && processingEnv != null) {
            isInit = true
            mFiler = processingEnv.filer
            logger = Logger(processingEnv.messager)
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
            testMain()
        }
        return true
    }

    private fun testMain() {
        val greeterClass = ClassName(packageName, "Greeter")
        val file = FileSpec.builder(packageName, "HelloWorld")
            .addType(
                TypeSpec.classBuilder("Greeter")
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addParameter("name", String::class)
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("name", String::class)
                            .initializer("name")
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("greet")
                            .addStatement("println(%P)", "\"Hello, \$name\"")
                            .build()
                    )
                    .build()
            )
            .addFunction(
                FunSpec.builder("main")
                    .addParameter("args", String::class, KModifier.VARARG)
                    .addStatement("%T(args[0]).greet()", greeterClass)
                    .build()
            )
            .build()


        file.writeTo(mFiler)
    }
}