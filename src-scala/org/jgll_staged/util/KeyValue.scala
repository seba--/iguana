package org.jgll_staged.util

import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

class KeyValue[K, V](@BeanProperty val key: K, @BeanProperty var value: V) extends java.util.Map.Entry[K, V] {

  override def equals(obj: Any): Boolean = {
    if (this == obj) {
      return true
    }
    if (!(obj.isInstanceOf[KeyValue])) {
      return false
    }
    val other = obj.asInstanceOf[KeyValue[_, _]]
    this.key == other.key && this.value == other.value
  }

  override def setValue(value: V): V = {
    this.value = value
    value
  }

  override def toString(): String = {
    "[" + key.toString + ", " + value.toString + "]"
  }
}
