package com.zhouz.datasync

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2024/3/12 18:51
 * description：数据同步处理器
 */
@AutoService(Processor::class)
@SupportedAnnotationTypes("com.zhouz.datasync.DataSyncBuild")
class DataSyncProcessor : AbstractProcessor() {

    private val packageName = "com.zhouz.datasync"

    @Volatile
    private var isInit = false
    private lateinit var logger: Logger
    private lateinit var mFiler: Filer

    private val methodsByClass: MutableMap<TypeElement, ExecutableElement> = mutableMapOf()

    private var writerRoundDone = false

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
        try {
            if (annotations.isNullOrEmpty()) return false
            roundEnv ?: return false
            collectSubscribers(annotations, roundEnv)
            if (methodsByClass.isNotEmpty()) {
                createInfoFile()
            } else {
                logger.warning("No @DataSyncBuild annotations found")
            }
            writerRoundDone = true
        } catch (e: RuntimeException) {
            e.printStackTrace()
            logger.error("Unexpected error in DataSyncProcessor: $e")
        }
        return true
    }

    /**
     * 创建订阅的file
     */
    private fun createInfoFile() {
        val syncClass = ClassName(packageName, "DataSync_Index")

        val interface_clazz = ClassName("com.zhouz.datasync", "IDataSyncSubscriber")

        // func getdatasyncsubscriberinfo
        val returns_func_getdatasyncsubscriberinfo = ClassName("com.zhouz.datasync", "DataSyncSubscriberInfo")
        val clazz_parameterspec_getdatasyncsubscriberinfo = KClass::class.asClassName().parameterizedBy(WildcardTypeName.producerOf(Any::class))
        val func_parameterspec_getdatasyncsubscriberinfo = ParameterSpec.builder("clazz", clazz_parameterspec_getdatasyncsubscriberinfo)
            .build()
        val func_getDataSyncSubscriberInfo = FunSpec.builder("getDataSyncSubscriberInfo")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(func_parameterspec_getdatasyncsubscriberinfo)
            .returns(returns_func_getdatasyncsubscriberinfo)
            .addStatement("return %T()", returns_func_getdatasyncsubscriberinfo)
            .build()

        // class DataSync_Index
        val clazz = TypeSpec.classBuilder("DataSync_Index")
            .addKdoc("数据同步的订阅处理类")
            .addSuperinterface(interface_clazz)
            .addFunction(func_getDataSyncSubscriberInfo)
            .build()

        // file DataSync_Index
        val file = FileSpec.builder(packageName, "DataSync_Index")
            .addImport("com.zhouz.datasync", "DataSyncSubscriberInfo")
            .addType(clazz)
            .build()

        file.writeTo(mFiler)
    }

    /**
     * 收集
     */
    private fun collectSubscribers(annotations: Set<TypeElement>, env: RoundEnvironment) {
        for (annotation in annotations) {
            val elements = env.getElementsAnnotatedWith(annotation)
            for (element in elements) {
                if (element is ExecutableElement) {
                    if (checkHasNoErrors(element)) {
                        val classElement = element.enclosingElement as TypeElement
                        methodsByClass[classElement] = element
                    }
                } else {
                    logger.error("@DataSyncBuild is only valid for methods")
                }
            }
        }
    }

    private fun checkHasNoErrors(element: ExecutableElement): Boolean {
        if (element.kind != ElementKind.METHOD) {
            logger.error("@DataSyncBuild is only valid for methods")
            return false
        }
        if (element.modifiers.contains(Modifier.STATIC)) {
            logger.error("DataSyncBuild method must not be static")
            return false
        }
        if (!element.modifiers.contains(Modifier.PUBLIC)) {
            logger.error("DataSyncBuild method must be public")
            return false
        }
        val parameters = element.parameters
        if (parameters.size != 1) {
            logger.error("DataSyncBuild method must have exactly 1 parameter")
            return false
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