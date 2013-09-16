package org.jgll.util

import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

class KeyValue[K, V](@BeanProperty val key: K, @BeanProperty var vvalue: V) extends java.util.Map.Entry[K, V] {

  override def getValue = vvalue
  override def setValue(v: V) = {
    val old = vvalue
    vvalue = v
    old
  }

  override def equals(obj: Any): Boolean = {
    if (this == obj) {
      return true
    }
    if (!(obj.isInstanceOf[KeyValue[K,V]])) {
      return false
    }
    val other = obj.asInstanceOf[KeyValue[_, _]]
    this.key == other.key && this.vvalue == other.vvalue
  }

  override def toString(): String = {
    "[" + key.toString + ", " + vvalue.toString + "]"
  }
}
