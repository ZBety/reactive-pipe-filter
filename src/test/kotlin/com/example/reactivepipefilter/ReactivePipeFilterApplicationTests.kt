package com.example.reactivepipefilter

import com.example.reactivepipefilter.filter.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono


@SpringBootTest
class ReactivePipeFilterApplicationTests {

	@Test
	fun contextLoads() {
		/*
        * 输入代码,请保留
        * 数组inputs对应控制台输入
        *  a-输入数据；
        *  p-过滤器1中的乘数因子；
        *  t-过滤器2中的乘数因子；
        *  */
		val nums = listOf(1.9,2.0,2.1,2.0,1.9).toMono()
		val inputs = listOf(1.9,2.0,2.1,2.0,1.9)
		val p = 0.1
		val t = 1.2

		//创建数据发生器（com.example.reactivepipefilter.filter.Source）、过滤器（com.example.reactivepipefilter.filter.Filter）、数据接收器（com.example.reactivepipefilter.filter.Sink），并完成连接
		val gen = Source()
		gen.list = inputs
		val filter1: Filter = Filter1(p)
		val filter2: Filter = Filter2(t)
		val sink = Sink()

		gen.pipedOutputStream.connect(filter1.pipedInputStream)
		filter1.pipedOutputStream.connect(filter2.pipedInputStream)
		filter2.pipedOutputStream.connect(sink.inputStream)
		filter2.returnOutputData.connect(filter1.returnInputData)
		gen.start()
		filter1.start()
		filter2.start()
		sink.start()

		Thread.sleep(2000)
	}

}
