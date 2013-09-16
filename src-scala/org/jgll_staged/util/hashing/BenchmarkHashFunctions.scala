package org.jgll_staged.util.hashing

import java.util.ArrayList
import java.util.List
import java.util.Random
import org.jgll_staged.util.hashing.hashfunction.HashFunction
//remove if not needed
import scala.collection.JavaConversions._

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
      var measurements = new ArrayList[Long]()
      for (it <- 0 until iterations) {
        val start = System.nanoTime()
        r = 0
        for (i <- 0 until data.length) {
          val d = data(i)
          r += f.hash(d(0), d(1), d(2))
        }
        val stop = System.nanoTime()
        measurements.add((stop - start) / (1000 * 1000))
      }
      printAvgStdDev("\t 3 numbers", measurements)
      measurements = new ArrayList()
      for (it <- 0 until iterations) {
        val start = System.nanoTime()
        r = 0
        for (i <- 0 until data.length) {
          val d = data(i)
          r += f.hash(d(0), d(1), d(2), d(3))
        }
        val stop = System.nanoTime()
        measurements.add((stop - start) / (1000 * 1000))
      }
      printAvgStdDev("\t 4 numbers", measurements)
      measurements = new ArrayList()
      for (it <- 0 until iterations) {
        val start = System.nanoTime()
        r = 0
        for (i <- 0 until data.length) {
          val d = data(i)
          r += f.hash(d(0), d(1), d(2), d(3), d(4))
        }
        val stop = System.nanoTime()
        measurements.add((stop - start) / (1000 * 1000))
      }
      printAvgStdDev("\t 5 numbers", measurements)
    }
  }

  private def printAvgStdDev(name: String, measurements: List[Long]) {
    var sum = 0
    for (l <- measurements) {
      sum += l
    }
    val avg = sum / measurements.size.toDouble
    var sd = 0
    for (l <- measurements) {
      sd = sd + (l - avg) * (l - avg)
    }
    sd = sd / (measurements.size - 1)
    println(String.format("%s\t %.2f (%.2f)", name, avg, sd))
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
      result(i)(4) = r.nextInt(java.lang.Integer.MAX_VALUE)
    }
    result
  }
}
