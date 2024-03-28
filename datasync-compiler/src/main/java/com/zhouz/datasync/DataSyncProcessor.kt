package com.zhouz.datasync

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MUTABLE_LIST
import com.squareup.kotlinpoet.MUTABLE_MAP
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.zhouz.datasync.Constant.clazz_data_differ_name
import com.zhouz.datasync.Constant.clazz_data_sync_interface
import com.zhouz.datasync.Constant.clazz_info_name
import com.zhouz.datasync.Constant.default_factory_name
import com.zhouz.datasync.Constant.packageName
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
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2024/3/12 18:51
 * description：数据同步处理器
 */
@AutoService(Processor::class)
@SupportedAnnotationTypes("com.zhouz.datasync.DataSyncBuild")
class DataSyncProcessor : AbstractProcessor() {

    private var clazzName: String? = null

    private val OPTION_DATA_SYNC_CLAZZ_NAME = "dataSyncClazzName"

    @Volatile
    private var isInit = false
    private lateinit var logger: Logger
    private lateinit var mFiler: Filer
    private lateinit var elementUtils: Elements
    private lateinit var typeUtils: Types

    private val subscriberBeansByType = mutableMapOf<TypeMirror, MutableList<SubscriberBean>>()
    private val subscriberBeansBySubType = mutableMapOf<TypeElement, MutableList<SubscriberBean>>()

    private var writerRoundDone = false

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        if (!isInit && processingEnv != null) {
            isInit = true
            mFiler = processingEnv.filer
            logger = Logger(processingEnv.messager)
            elementUtils = processingEnv.elementUtils
            typeUtils = processingEnv.typeUtils
            clazzName = processingEnv.options[OPTION_DATA_SYNC_CLAZZ_NAME]
            logger.info("init end")
        }
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        val annotations: MutableSet<String> = LinkedHashSet()
        annotations.add(DataObserver::class.java.canonicalName)
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
            if (subscriberBeansByType.isNotEmpty()) {
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
    @OptIn(DelicateKotlinPoetApi::class)
    private fun createInfoFile() {
        val lastPeriod: Int = clazzName?.lastIndexOf('.') ?: -1
        val indexPackage: String = if (lastPeriod != -1) clazzName?.substring(0, lastPeriod)
            ?: packageName else packageName
        val clazzName =
            if (lastPeriod != -1) clazzName?.substring(lastPeriod + 1, clazzName?.length ?: 0)
                ?: default_factory_name else default_factory_name

        val interfaceClazz = ClassName(packageName, clazz_data_sync_interface)
        val infoClazz = ClassName(packageName, clazz_info_name)
        val dataClazzDiffer = ClassName(packageName, clazz_data_differ_name)

        // property mapSubscriber
        val propertyFieldMap = {
            val fieldValueClazz =
                MUTABLE_LIST.parameterizedBy(
                    KClass::class.asClassName()
                        .parameterizedBy(TypeVariableName("*"))
                )
            val fieldKeyClazz =
                KClass::class.asClassName().parameterizedBy(
                    WildcardTypeName.producerOf(
                        dataClazzDiffer.parameterizedBy(TypeVariableName("*"))
                    )
                )
            val fieldMapClazz = MUTABLE_MAP.parameterizedBy(fieldKeyClazz, fieldValueClazz)
            PropertySpec.builder("mapSubscriberByData", fieldMapClazz)
                .addModifiers(KModifier.PRIVATE)
                .addKdoc("数据同步订阅Map")
                .initializer("mutableMapOf()")
                .build()
        }


        // property mapSubscriberBySub
        val propertyMapSubscriberBySub = {
            val fieldValueClazz =
                MUTABLE_LIST.parameterizedBy(
                    infoClazz.parameterizedBy(
                        WildcardTypeName.producerOf(
                            dataClazzDiffer.parameterizedBy(TypeVariableName("*"))
                        )
                    )
                )
            val fieldKeyClazz =
                KClass::class.asClassName().parameterizedBy(TypeVariableName("*"))
            val fieldMapClazz = MUTABLE_MAP.parameterizedBy(fieldKeyClazz, fieldValueClazz)
            PropertySpec.builder("mapSubInfoBySubscriberClazz", fieldMapClazz)
                .addModifiers(KModifier.PRIVATE)
                .addKdoc("数据同步订阅Map")
                .initializer("mutableMapOf()")
                .build()
        }

        // func getSubscriberClazzByDataClazz
        val funcGetSubscriberClazzByDataClazz = {
            val returnsFuncGetDataSyncSubscriberInfo =
                MUTABLE_LIST.parameterizedBy(
                    KClass::class.asClassName()
                        .parameterizedBy(TypeVariableName("*"))
                )
            val clazzParameterSpecGetDataSyncSubscriberInfo =
                KClass::class.asClassName().parameterizedBy(
                    WildcardTypeName.producerOf(
                        dataClazzDiffer.parameterizedBy(TypeVariableName("*"))
                    )
                )
            val funcParameterSpecGetDataSyncSubscriberInfo =
                ParameterSpec.builder("clazz", clazzParameterSpecGetDataSyncSubscriberInfo)
                    .build()
            FunSpec.builder("getSubscriberClazzByDataClazz")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(funcParameterSpecGetDataSyncSubscriberInfo)
                .returns(returnsFuncGetDataSyncSubscriberInfo.copy(true))
                .addStatement("return mapSubscriberByData.get(clazz)")
                .build()
        }


        // func getDataSyncSubscriberInfo
        val funcGetDataSyncSubscriberInfo = {
            val returnsFuncGetDataSyncSubscriberInfo =
                MUTABLE_LIST.parameterizedBy(
                    infoClazz.parameterizedBy(
                        WildcardTypeName.producerOf(
                            dataClazzDiffer.parameterizedBy(
                                TypeVariableName("*")
                            )
                        )
                    )
                )
            val clazzParameterSpecGetDataSyncSubscriberInfo =
                KClass::class.asClassName().parameterizedBy(TypeVariableName("*"))
            val funcParameterSpecGetDataSyncSubscriberInfo =
                ParameterSpec.builder("clazz", clazzParameterSpecGetDataSyncSubscriberInfo)
                    .build()
            FunSpec.builder("getSubInfoBySubscriberClazz")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(funcParameterSpecGetDataSyncSubscriberInfo)
                .returns(returnsFuncGetDataSyncSubscriberInfo.copy(true))
                .addStatement("return mapSubInfoBySubscriberClazz.get(clazz)")
                .build()
        }

        // func initSubscriberBeansBySubType
        val funcInitSubscriberBeansByType = {
            val mapBlock = CodeBlock.builder()
            subscriberBeansByType.forEach { (type, list) ->
                val dataCode = CodeBlock.builder()
                list.forEachIndexed { index, subscriberBean ->
                    if (index != 0) {
                        dataCode.add(",")
                    }
                    dataCode.add(
                        "%T::class",
                        subscriberBean.clazzElement.asType()
                    )
                }
                mapBlock.addStatement(
                    "mapSubscriberByData[%T::class]=mutableListOf(%L)",
                    type,
                    dataCode.build()
                )
            }
            FunSpec.builder("initSubscriberBeansByType")
                .addModifiers(KModifier.PRIVATE)
                .addCode(mapBlock.build()).build()
        }

        // func initSubscriberBeansBySubType
        val funcInitSubscriberBeansBySubType = {
            val mapBlock = CodeBlock.builder()
            subscriberBeansBySubType.forEach { (type, list) ->
                val dataCode = CodeBlock.builder()
                list.forEachIndexed { index, subscriberBean ->
                    if (index != 0) {
                        dataCode.add(",")
                    }
                    dataCode.add(
                        "%T(methodName=%S,dataClazz=%T::class,subscriberClazz=%T::class,dispatcher=Dispatcher.%L)",
                        infoClazz.parameterizedBy(subscriberBean.type.asTypeName()),
                        subscriberBean.funcName,
                        subscriberBean.type.asTypeName(),
                        subscriberBean.clazzElement.asType(),
                        subscriberBean.dispatcher
                    )
                }
                mapBlock.addStatement(
                    "mapSubInfoBySubscriberClazz[%T::class]=mutableListOf(%L)",
                    type,
                    dataCode.build()
                )
            }
            FunSpec.builder("initSubscriberBeansBySubType")
                .addModifiers(KModifier.PRIVATE)
                .addCode(mapBlock.build()).build()
        }

        // func init
        val initBlock = CodeBlock.builder()
        initBlock.addStatement("initSubscriberBeansByType()")
        initBlock.addStatement("initSubscriberBeansBySubType()")

        // class DataSync_Index
        val clazz = TypeSpec.classBuilder(clazzName)
            .addKdoc("数据同步的订阅处理类")
            .addSuperinterface(interfaceClazz)
            .addProperty(propertyFieldMap())
            .addProperty(propertyMapSubscriberBySub())
            .addInitializerBlock(initBlock.build())
            .addFunction(funcGetSubscriberClazzByDataClazz())
            .addFunction(funcGetDataSyncSubscriberInfo())
            .addFunction(funcInitSubscriberBeansByType())
            .addFunction(funcInitSubscriberBeansBySubType())
            .build()

        // file DataSync_Index
        val file = FileSpec.builder(indexPackage, clazzName)
            .addImport("com.zhouz.datasync", "DataSyncSubscriberInfo")
            .addImport("com.zhouz.datasync", "Dispatcher")
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
                        val funcName = element.simpleName
                        val annotationElement = element.getAnnotation(DataObserver::class.java)
                        val threadName = annotationElement.threadName
                        val param = element.parameters.firstOrNull() ?: return
                        val type = param.asType()
                        val typeDifferClazz = typeUtils.erasure(elementUtils.getTypeElement("$packageName.$clazz_data_differ_name").asType())
                        if (typeUtils.isAssignable(type, typeDifferClazz)) {
                            val typeInit = {
                                val list = subscriberBeansByType[type] ?: mutableListOf()
                                list.add(
                                    SubscriberBean(
                                        type,
                                        funcName,
                                        threadName,
                                        classElement
                                    )
                                )
                                subscriberBeansByType[type] = list
                            }
                            typeInit.invoke()

                            val subInit = {
                                val list = subscriberBeansBySubType[classElement] ?: mutableListOf()
                                list.add(
                                    SubscriberBean(
                                        type,
                                        funcName,
                                        threadName,
                                        classElement
                                    )
                                )
                                subscriberBeansBySubType[classElement] = list
                            }
                            subInit.invoke()
                        } else {
                            logger.warning("type $type not interface $packageName.$clazz_data_differ_name")
                        }
                    }
                } else {
                    logger.error("@DataObserver is only valid for methods")
                }
            }
        }
    }

    /**
     * 检查注解类型
     */
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