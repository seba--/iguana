package org.jgll.util.hashing

import java.util.Random
import org.jgll.util.hashing.hashfunction.HashFunction
import scala.collection.mutable.ListBuffer

class BenchmarkHashFunctions(private val targets: HashFunction*) {

  def bench(size: Int, iterations: Int) {
    val data = generateData(size)
    var r = 0
    for (i <- 0 until 5) {
      r += run5(data)
      r += run4(data)
      r += run3(data)
    }
    println("Warmed up: " + r)
    for (f <- this.targets) {
      println(f.getClass.getName)
      var measurements = ListBuffer[Long]()
      for (it <- 0 until iterations) {
        val start = System.nanoTime()
        r = 0
        for (i <- 0 until data.length) {
          val d = data(i)
          r += f.hash(d(0), d(1), d(2))
        }
        val stop = System.nanoTime()
        measurements += ((stop - start) / (1000 * 1000))
      }
      printAvgStdDev("\t 3 numbers", measurements)
      measurements = ListBuffer()
      for (it <- 0 until iterations) {
        val start = System.nanoTime()
        r = 0
        for (i <- 0 until data.length) {
          val d = data(i)
          r += f.hash(d(0), d(1), d(2), d(3))
        }
        val stop = System.nanoTime()
        measurements += ((stop - start) / (1000 * 1000))
      }
      printAvgStdDev("\t 4 numbers", measurements)
      measurements = ListBuffer()
      for (it <- 0 until iterations) {
        val start = System.nanoTime()
        r = 0
        for (i <- 0 until data.length) {
          val d = data(i)
          r += f.hash(d(0), d(1), d(2), d(3), d(4))
        }
        val stop = System.nanoTime()
        measurements += ((stop - start) / (1000 * 1000))
      }
      printAvgStdDev("\t 5 numbers", measurements)
    }
  }

  private def printAvgStdDev(name: String, measurements: ListBuffer[Long]) {
    var sum = 0l
    for (l <- measurements) {
      sum += l
    }
    val avg: Long = (sum / measurements.size.toDouble).toLong
    var sd = 0l
    for (l <- measurements) {
      sd = sd + (l - avg) * (l - avg)
    }
    sd = sd / (measurements.size - 1)
    println("%s\t %.2f (%.2f)".format(name, avg, sd))
  }

  private def measure(x: Runnable): Long = {
    val start = System.nanoTime()
    x.run()
    val stop = System.nanoTime()
    (stop - start) / (1000 * 1000)
  }

  private def run3(data: Array[Array[Int]]): Int = {
    var result = 0
    for (f <- this.targets; i <- 0 until data.length) {
      val d = data(i)
      result += f.hash(d(0), d(1), d(2))
    }
    result
  }

  private def run4(data: Array[Array[Int]]): Int = {
    var result = 0
    for (f <- this.targets; i <- 0 until data.length) {
      val d = data(i)
      result += f.hash(d(0), d(1), d(2))
    }
    result
  }

  private def run5(data: Array[Array[Int]]): Int = {
    var result = 0
    for (f <- this.targets; i <- 0 until data.length) {
      val d = data(i)
      result += f.hash(d(0), d(1), d(2), d(3), d(4))
    }
    result
  }

  private def generateData(size: Int): Array[Array[Int]] = {
    val r = new Random(size)
    val result = Array.ofDim[Int](size, 5)
    for (i <- 0 until size) {
      result(i)(0) = if (r.nextBoolean()) r.nextInt(0xFF) else r.nextInt(0xFFFF)
      result(i)(1) = if (r.nextInt(5) <= 3) r.nextInt(0xFFF) else r.nextInt(0xFFFF)
      result(i)(2) = if (r.nextBoolean()) r.nextInt(0xFFFF) else r.nextInt(0x1FFFF)
      result(i)(3) = if (r.nextBoolean()) r.nextInt(0xFFF) else r.nextInt(0x3FFFF)
      result(i)(4) = r.nextInt(Int.MaxValue)
    }
    result
  }
}
