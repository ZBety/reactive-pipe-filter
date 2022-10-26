package com.example.reactivepipefilter.filter

import java.io.IOException
import java.io.PipedInputStream
import java.io.PipedOutputStream
import kotlin.system.exitProcess


abstract class Filter : Thread() {
    var c = 0.0 //返回参数
    var p = 0.1
    var t = 1.2
    var pipedInputStream = PipedInputStream()
    var pipedOutputStream = PipedOutputStream()
    var returnInputData = PipedInputStream()
    var returnOutputData = PipedOutputStream()
    override fun run() {
        while (true) {
            try {
                if (pipedInputStream.available() != 0) {
                    val buf = ByteArray(1024)
                    val len = pipedInputStream.read(buf)
                    val s = String(buf, 0, len)
                    doTask(s.toDouble())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            yield()
        }
    }

    abstract fun doTask(num: Double)
}

class Filter1(p: Double) : Filter() {
    override fun doTask(num: Double) {
        var num1 = num
        try {
            try {
                if (returnInputData.available() != 0) {
                    val buf = ByteArray(1024)
                    val len = returnInputData.read(buf)
                    val s = String(buf, 0, len)
                    c = s.toDouble()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            num1 -= c * this.p!!
            pipedOutputStream.write((num1.toString() + "").toByteArray())
            yield()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

class Filter2(t: Double) : Filter() {
    override fun doTask(num: Double) {
        var num1 = num
        try {
            num1 *= this.t
            pipedOutputStream.write((num1.toString() + "").toByteArray())
            returnOutputData.write((num1.toString() + "").toByteArray()) //回去管道
            pipedOutputStream.flush()
            returnOutputData.flush()
            c = num1
            yield()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

internal class Sink : Thread() {
    var inputStream = PipedInputStream()

    override fun run() {
        var index = 0
        while (true) {
            index++
            //            System.out.println("Sink运行");
            try {
                /**
                 * 1: 2.28
                 * 2: 2.13
                 * 3: 2.26
                 * 4: 2.13
                 * 5: 2.02
                 */
                val buf = ByteArray(1024)
                val len = inputStream.read(buf)
                val s = String(buf, 0, len)
                System.out.printf("%d: %.2f", index, s.toDouble())
                println()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            yield()
        }
    }
}

internal class Source : Thread() {
    //输入数据
    var pipedOutputStream = PipedOutputStream()
    var list: List<Double>? = null

    override fun run() {
        for (item in list!!) {
            try {
                pipedOutputStream.write((item.toString() + "").toByteArray())
                pipedOutputStream.flush()
                try {
                    sleep(3)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            yield()
        }
        try {
            sleep(200)
            exitProcess(0)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}