package com.zhouz.datasync

import com.test.DataSyncIntercept
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import javax.xml.datatype.DatatypeFactory


/**
 * @author:zhouz
 * @date: 2024/3/4 12:04
 */
@RunWith(MockitoJUnitRunner::class)
class TestZhouz {
    @Test
    fun testApi() {
//        val context = mock(Context::class.java)
//        val applicationContext = mock(Context::class.java)
//        Mockito.`when`(applicationContext.packageName).thenReturn("myPackage")
//        Mockito.`when`(context.applicationContext).thenReturn(applicationContext)
//        CoreLog.init(context)
//        main("zhouz")
        DataSyncIntercept().getSubInfoBySubscriberClazz(Any::class)
    }


    @DataObserver
    fun onTestData(testData: TestData) {
    }
}


data class TestData(var value: Int = 1)


