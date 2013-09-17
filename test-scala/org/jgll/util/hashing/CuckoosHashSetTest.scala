package org.jgll.util.hashing

import org.junit.Assert._
import java.util.Random
import org.jgll.parser.HashFunctions
import org.jgll.util.RandomUtil
import org.jgll.util.hashing.hashfunction.HashFunction
import org.junit.Test
import CuckoosHashSetTest._
//remove if not needed
import scala.collection.JavaConversions._

object CuckoosHashSetTest {

  class IntegerHashKey4(val k1: Int, val k2: Int, val k3: Int, val k4: Int) {

    override def equals(obj: Any): Boolean = {
      if(this == obj) {
        return true;
      }

      if(!(obj.isInstanceOf[IntegerHashKey4])) {
        return false;
      }

      val other = obj.asInstanceOf[IntegerHashKey4];

      return k1 == other.k1 && k2 == other.k2 && k3 == other.k3 && k4 == other.k4;
    }

    override def toString() = {
      "(" + k1 + ", " + k2 + ", " + k3 + ", " + k4 + ")"
    }

    override def hashCode() = {
      HashFunctions.defaulFunction().hash(k1, k2, k3, k4);
    }
  }

  @SerialVersionUID(1L)
  class IntegerHashKey4ExternalHasher extends ExternalHasher[IntegerHashKey4] {

    override def hash(key: IntegerHashKey4, f: HashFunction): Int = f.hash(key.k1, key.k2, key.k3, key.k4)

    override def equals(key1: IntegerHashKey4, key2: IntegerHashKey4): Boolean = {
      key1.k1 == key2.k1 && key1.k2 == key2.k2 && key1.k3 == key2.k3 &&
        key1.k4 == key1.k4
    }
  }
}

class CuckoosHashSetTest {

  import CuckoosHashSetTest._

  private val externalHasher = new IntegerHashKey4ExternalHasher()

  @Test
  def testAdd() {
    val set = new CuckooHashSet(externalHasher)
    val key1 = new IntegerHashKey4(100, 12, 27, 23)
    val key2 = new IntegerHashKey4(52, 10, 20, 21)
    val add1 = set.add(key1)
    val add2 = set.add(key2)
    assertEquals(null, add1)
    assertEquals(null, add2)
    assertEquals(true, set.contains(key1))
    assertEquals(true, set.contains(key2))
    assertEquals(2, set.size)
  }

  @Test
  def testRehashing() {
    val set = new CuckooHashSet[IntegerHashKey4](8, externalHasher)
    val key1 = new IntegerHashKey4(100, 12, 27, 23)
    val key2 = new IntegerHashKey4(52, 10, 20, 21)
    val key3 = new IntegerHashKey4(10, 10, 98, 13)
    val key4 = new IntegerHashKey4(59, 7, 98, 1)
    set.add(key1)
    set.add(key2)
    set.add(key3)
    set.add(key4)
    assertEquals(true, set.contains(key1))
    assertEquals(true, set.contains(key2))
    assertEquals(true, set.contains(key3))
    assertEquals(true, set.contains(key4))
    assertEquals(1, set.getEnlargeCount)
    assertEquals(4, set.size)
  }

  @Test
  def testInsertOneMillionEntries() {
    val set = new CuckooHashSet[IntegerHashKey4](externalHasher)
    val rand = RandomUtil.random
    for (i <- 0 until 1000000) {
      val key = new IntegerHashKey4(rand.nextInt(java.lang.Integer.MAX_VALUE), rand.nextInt(java.lang.Integer.MAX_VALUE), 
        rand.nextInt(java.lang.Integer.MAX_VALUE), rand.nextInt(java.lang.Integer.MAX_VALUE))
      set.add(key)
    }
    assertEquals(1000000, set.size)
  }

  @Test
  def testAddAll() {
    val set1 = CuckooHashSet.from[Integer](IntegerExternalHasher.getInstance, 1, 2, 3)
    val set2 = CuckooHashSet.from[Integer](IntegerExternalHasher.getInstance, 4, 5, 6, 7)
    assertEquals(3, set1.size)
    assertEquals(4, set2.size)
    set1.addAll(set2)
    assertEquals(7, set1.size)
  }

  @Test
  def testClear() {
    val set = CuckooHashSet.from[Integer](IntegerExternalHasher.getInstance, 1, 2, 3, 4, 5)
    set.clear()
    assertEquals(false, set.contains(1))
    assertEquals(false, set.contains(2))
    assertEquals(false, set.contains(3))
    assertEquals(false, set.contains(4))
    assertEquals(false, set.contains(5))
    assertEquals(0, set.size)
  }

  @Test
  def testRemove() {
    val set = CuckooHashSet.from[Integer](IntegerExternalHasher.getInstance, 1, 2, 3, 4, 5)
    set.remove(3)
    set.remove(5)
    assertEquals(true, set.contains(1))
    assertEquals(true, set.contains(2))
    assertEquals(false, set.contains(3))
    assertEquals(true, set.contains(4))
    assertEquals(false, set.contains(5))
    assertEquals(3, set.size)
  }

  @Test
  def testAddAndGet() {
    val set = CuckooHashSet.from[Integer](IntegerExternalHasher.getInstance, 1, 2, 3)
    val ret1 = set.add(4)
    assertEquals(null, ret1)
    assertEquals(true, set.contains(4))
    val ret2 = set.add(3)
    assertEquals(new java.lang.Integer(3), ret2)
  }

  @Test
  def testEnlarge() {
    val set = CuckooHashSet.from[Integer](IntegerExternalHasher.getInstance, 101, 21, 398, 432, 15, 986, 737)
    set.add(891)
  }
}
