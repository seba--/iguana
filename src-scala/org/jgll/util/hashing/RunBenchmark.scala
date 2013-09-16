package org.jgll.util.hashing

import java.util.Random
import org.jgll.util.hashing.hashfunction.DavyHash
import org.jgll.util.hashing.hashfunction.Jenkins
import org.jgll.util.hashing.hashfunction.JenkinsCWI
import org.jgll.util.hashing.hashfunction.MurmurHash2
import org.jgll.util.hashing.hashfunction.MurmurHash3
import org.jgll.util.hashing.hashfunction.SuperFastHash
import org.jgll.util.hashing.hashfunction.SuperFastHash16BitOnly
import org.jgll.util.hashing.hashfunction.XXHash
//remove if not needed
import scala.collection.JavaConversions._

object RunBenchmark {

  def main(args: Array[String]) {
    val r = new Random()
    val bencher = new BenchmarkHashFunctions(new MurmurHash2(r.nextInt()), new MurmurHash3(r.nextInt()), 
      new DavyHash(r.nextInt()), new XXHash(r.nextInt()), new SuperFastHash(r.nextInt()), new SuperFastHash16BitOnly(r.nextInt()), 
      new Jenkins(r.nextInt()), new JenkinsCWI(r.nextInt()))
    bencher.bench(1 * 1000 * 1000, 100)
    bencher.bench(3 * 1000 * 1000, 100)
  }
}
