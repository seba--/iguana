package org.jgll.util

import scala.reflect.BeanProperty

class KeyValue[K, V](@BeanProperty val key: K, @BeanProperty var vvalue: V) {

  def getValue = vvalue
  def setValue(v: V) = {
    val old = vvalue
    vvalue = v
    old
  }

  override def equals(obj: Any): Boolean = {
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
