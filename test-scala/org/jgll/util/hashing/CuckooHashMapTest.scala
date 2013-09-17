package org.jgll.util.hashing

import java.util.Random
import org.junit.Assert._
import org.jgll.util.RandomUtil
import org.junit.Test
//remove if not needed
import scala.collection.JavaConversions._

class CuckooHashMapTest {

  @Test
  def test() {
    val map = new CuckooHashMap[Integer, Integer](IntegerExternalHasher.getInstance)
    map.put(1, 2)
    map.put(2, 3)
    assertEquals(new java.lang.Integer(2), map.get(1))
    assertEquals(new java.lang.Integer(3), map.get(2))
  }

  @Test
  def testInsertOneMillionEntries() {
    val map = new CuckooHashMap[Integer, Integer](IntegerExternalHasher.getInstance)
    val rand = RandomUtil.random
    for (i <- 0 until 1000000) {
      var key = rand.nextInt(java.lang.Integer.MAX_VALUE)
      var value = rand.nextInt(java.lang.Integer.MAX_VALUE)
      while (map.get(key) != null) {
        key = rand.nextInt(java.lang.Integer.MAX_VALUE)
        value = rand.nextInt(java.lang.Integer.MAX_VALUE)
      }
      map.put(key, value)
    }
    assertEquals(1000000, map.size)
  }
}
