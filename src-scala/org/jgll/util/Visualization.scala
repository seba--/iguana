package org.jgll.util

import org.jgll.parser.GSSNode
import org.jgll.sppf.SPPFNode
import org.jgll.util.dot.GSSToDot
import org.jgll.util.dot.GraphVizUtil
import org.jgll.util.dot.SPPFToDot
import org.jgll.util.dot.ToDotWithoutIntermediateNodes
import org.jgll.util.dot.ToDotWithoutIntermeidateAndLists
//remove if not needed
import scala.collection.JavaConversions._

object Visualization {

  def generateSPPFGraphWithoutIntermeiateNodes(outputDir: String, sppf: SPPFNode) {
    val toDot = new ToDotWithoutIntermediateNodes()
    sppf.accept(toDot)
    GraphVizUtil.generateGraph(toDot.getString, outputDir, "graph")
  }

  def generateSPPFGraph(outputDir: String, sppf: SPPFNode) {
    val toDot = new SPPFToDot()
    sppf.accept(toDot)
    GraphVizUtil.generateGraph(toDot.getString, outputDir, "graph")
  }

  def generateSPPFGraphWithIntermeiateAndListNodes(outputDir: String, sppf: SPPFNode) {
    val toDot = new ToDotWithoutIntermeidateAndLists()
    sppf.accept(toDot)
    GraphVizUtil.generateGraph(toDot.getString, outputDir, "graph")
  }

  def generateGSSGraph(outputDir: String, nodes: java.lang.Iterable[GSSNode]) {
    val toDot = new GSSToDot()
    toDot.execute(nodes)
    toDot.execute(nodes)
    GraphVizUtil.generateGraph(toDot.getString, outputDir, "gss")
  }
}
