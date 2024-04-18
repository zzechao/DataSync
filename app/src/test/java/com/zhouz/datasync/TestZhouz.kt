package com.zhouz.datasync

import com.alibaba.fastjson2.JSON
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


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
//        val format2 = "{\"value\":\"2\",\"data\"${format2}:}"
//        val data = JSON.parseObject("{\"value\":\"1\",\"data\":$format2}", TestData::class.java)
//        System.out.println(data)
    }
}


data class TestData(
    var value: Int = 1,
    var data: TestData? = TestData()
)


