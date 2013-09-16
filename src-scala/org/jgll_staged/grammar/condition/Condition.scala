package org.jgll_staged.grammar.condition

import java.io.Serializable
//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
abstract class Condition(protected var `type`: ConditionType) extends Serializable {

  def getType(): ConditionType = `type`
}
