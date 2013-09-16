package org.jgll_staged.traversal

//remove if not needed
import scala.collection.JavaConversions._

trait NodeListener[T, U] {

  def startNode(`type`: T): Unit

  def endNode(`type`: T, children: java.lang.Iterable[U], node: PositionInfo): Result[U]

  def buildAmbiguityNode(children: java.lang.Iterable[U], node: PositionInfo): Result[U]

  def terminal(c: Int, node: PositionInfo): Result[U]
}
