package org.jgll_staged.util

import org.jgll_staged.parser.GSSNode
import org.jgll_staged.sppf.SPPFNode
import org.jgll_staged.util.dot.GSSToDot
import org.jgll_staged.util.dot.GraphVizUtil
import org.jgll_staged.util.dot.SPPFToDot
import org.jgll_staged.util.dot.ToDotWithoutIntermediateNodes
import org.jgll_staged.util.dot.ToDotWithoutIntermeidateAndLists
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
