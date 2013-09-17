package org.jgll.util.hashing

import java.io.Serializable
import java.util.Iterator
import org.jgll.util.RandomUtil
import org.jgll.util.hashing.hashfunction.HashFunction
import org.jgll.util.hashing.hashfunction.MurmurHash3
import CuckooHashSet._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

object CuckooHashSet {

  val DEFAULT_INITIAL_CAPACITY = 16

  private val DEFAULT_LOAD_FACTOR = 0.49f

  def from[T >: Null](decomposer: ExternalHasher[T], elements: T*)
                     (implicit m: Manifest[T]): CuckooHashSet[T] = {
    val set = new CuckooHashSet[T](decomposer)
    for (e <- elements) {
      set.add(e)
    }
    set
  }
}

@SerialVersionUID(1L)
class CuckooHashSet[T >: Null](@BeanProperty var initialCapacity: Int, private var loadFactor: Float, decomposer: ExternalHasher[T])
                              (implicit val manifest: Manifest[T])
    extends Serializable with java.lang.Iterable[T] {

  if (initialCapacity < 8) {
    initialCapacity = 8
  }

  private var capacity: Int = 1
  while (capacity < initialCapacity) {
    capacity *= 2
  }

  protected var tableSize: Int = capacity / 2

  protected var _size: Int = _

  private var threshold: Int = (loadFactor * capacity).toInt

  protected var rehashCount: Int = _

  protected var enlargeCount: Int = _

  private var function1: HashFunction = _

  private var function2: HashFunction = _

  protected var table1: Array[T] = new Array[T](tableSize)

  protected var table2: Array[T] = new Array[T](tableSize)

  private var externalHasher: ExternalHasher[T] = decomposer

  generateNewHashFunctions()

  def this(decomposer: ExternalHasher[T])(implicit m: Manifest[T]) {
    this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, decomposer)
  }

  def this(initalCapacity: Int, decomposer: ExternalHasher[T])(implicit m: Manifest[T]) {
    this(initalCapacity, DEFAULT_LOAD_FACTOR, decomposer)
  }

  private def generateNewHashFunctions() {
    function1 = new MurmurHash3(RandomUtil.random.nextInt(java.lang.Integer.MAX_VALUE))
    function2 = new MurmurHash3(RandomUtil.random.nextInt(java.lang.Integer.MAX_VALUE))
  }

  def contains(key: T): Boolean = get(key) != null

  def get(key: T): T = {
    var index = indexFor(externalHasher.hash(key, function1))
    var value = table1(index)
    if (!isEntryEmpty(value) && externalHasher.equals(key, value)) {
      return value
    }
    index = indexFor(externalHasher.hash(key, function2))
    value = table2(index)
    if (!isEntryEmpty(value) && externalHasher.equals(key, value)) {
      return value
    }
    null
  }

  def add(_key: T): T = {
    var key = _key
    if (_size >= threshold) {
      enlargeTables()
    }
    val e = get(key)
    if (e != null) {
      return e
    }
    key = tryInsert(key)
    while (key != null) {
      generateNewHashFunctions()
      rehash()
      key = tryInsert(key)
    }
    _size += 1
    null
  }

  protected def isEntryEmpty(e: T): Boolean = e == null

  private def tryInsert(_key: T): T = {
    var key = _key
    var i = 0
    while (i < _size + 1) {
      i += 1
      key = insert(key)
      if (key == null) {
        return null
      }
    }
    key
  }

  private def insert(_key: T): T = {
    var key: T = _key
    var index = indexFor(externalHasher.hash(key, function1))
    if (isEntryEmpty(table1(index))) {
      table1(index) = key
      return null
    }
    var tmp = table1(index)
    table1(index) = key
    key = tmp
    index = indexFor(externalHasher.hash(key, function2))
    if (isEntryEmpty(table2(index))) {
      table2(index) = key
      return null
    }
    tmp = table2(index)
    table2(index) = key
    key = tmp
    key
  }

  private def rehash() {
    rehashCount += 1
    for (i <- 0 until table1.length) {
      val key = table1(i)
      if (!isEntryEmpty(key)) {
        if (indexFor(externalHasher.hash(key, function1)) != i) {
          var tmp = table1(i)
          table1(i) = null
          tmp = tryInsert(tmp)
          if (tmp != null) {
            putInEmptySlot(tmp)
            generateNewHashFunctions()
            rehash()
            return
          }
        }
      }
    }

    for (i <- 0 until table2.length) {
      val key = table2(i)
      if (!isEntryEmpty(key)) {
        if (indexFor(externalHasher.hash(key, function2)) != i) {
          var tmp = table2(i)
          table2(i) = null
          tmp = tryInsert(tmp)
          if (tmp != null) {
            putInEmptySlot(tmp)
            generateNewHashFunctions()
            rehash()
            return
          }
        }
      }
    }
  }

  private def putInEmptySlot(key: T) {
    for (i <- 0 until table1.length if table1(i) == null) {
      table1(i) = key
      return
    }
    for (i <- 0 until table2.length if table2(i) == null) {
      table2(i) = key
      return
    }
    throw new IllegalStateException("Shouldn't reach here")
  }

  private def enlargeTables() {
    val newTable1 = Array.ofDim[Any](capacity).asInstanceOf[Array[T]]
    val newTable2 = Array.ofDim[Any](capacity).asInstanceOf[Array[T]]
    tableSize = capacity
    capacity *= 2
    threshold = (loadFactor * capacity).toInt
    System.arraycopy(table1, 0, newTable1, 0, table1.length)
    System.arraycopy(table2, 0, newTable2, 0, table2.length)
    table1 = newTable1
    table2 = newTable2
    enlargeCount += 1
    rehash()
  }

  def size(): Int = _size

  def getRehashCount(): Int = rehashCount

  def getEnlargeCount(): Int = enlargeCount

  protected def indexFor(hash: Int): Int = {
    val s = (tableSize - 1)
    val r = hash & s
    r
  }

  def isEmpty(): Boolean = _size == 0

  override def iterator(): Iterator[T] = {
    new Iterator[T]() {

      var it: Int = 0

      var index1: Int = 0

      var index2: Int = 0

      override def hasNext(): Boolean = it < _size

      override def next(): T = {
        while (index1 < table1.length) {
          if (!isEntryEmpty(table1(index1))) {
            it += 1
            index1 += 1
            return table1(index1-1)
          }
          index1 += 1
        }
        while (index2 < table2.length) {
          if (!isEntryEmpty(table2(index2))) {
            it += 1
            index2 += 1
            return table2(index2-1)
          }
          index2 += 1
        }
        throw new RuntimeException("There is no next.")
      }

      override def remove() {
        throw new UnsupportedOperationException()
      }
    }
  }

  def remove(key: T): Boolean = {
    var index = indexFor(externalHasher.hash(key, function1))
    if (externalHasher.equals(key, table1(index))) {
      table1(index) = null
      _size -= 1
      return true
    }
    index = indexFor(externalHasher.hash(key, function2))
    if (externalHasher.equals(key, table2(index))) {
      table2(index) = null
      _size -= 1
      return true
    }
    false
  }

  def clear() {
    for (i <- 0 until table1.length) {
      table1(i) = null
      table2(i) = null
    }
    _size = 0
    rehashCount = 0
    enlargeCount = 0
  }

  def addAll(c: java.lang.Iterable[T]): Boolean = {
    var changed = false
    for (e <- c) {
      changed |= (add(e) == null)
    }
    changed
  }

  override def toString(): String = {
    val sb = new StringBuilder()
    sb.append("{")
    for (t <- table1 if !isEntryEmpty(t)) sb.append(t).append(", ")
    for (t <- table2 if !isEntryEmpty(t)) sb.append(t).append(", ")
    if (sb.length > 2) sb.delete(sb.length - 2, sb.length)
    sb.append("}")
    sb.toString
  }
}
