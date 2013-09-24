package org.jgll.traversal

//remove if not needed
import scala.collection.JavaConversions._

trait NodeListener[T, U] extends PositionInfoTrait {

  def startNode(`type`: T): Unit

  def endNode(`type`: T, children: java.lang.Iterable[U], node: Rep[PositionInfo]): Result[U]

  def buildAmbiguityNode(children: java.lang.Iterable[U], node: Rep[PositionInfo]): Result[U]

  def terminal(c: Int, node: Rep[PositionInfo]): Result[U]
}
