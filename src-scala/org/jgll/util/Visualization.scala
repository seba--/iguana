package org.jgll.util

import org.jgll.parser.GSSNodeTrait
import org.jgll.sppf.SPPFNodeTrait
import org.jgll.util.dot._

trait VisualizationTrait {
  self: ToDotWithoutIntermediateNodesTrait
   with SPPFToDotTrait
   with SPPFNodeTrait
   with ToDotWithoutIntermeidateAndListsTrait
   with GSSNodeTrait
   with GSSToDotTrait =>
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

    def generateGSSGraph(outputDir: String, nodes: Iterable[GSSNode]) {
      val toDot = new GSSToDot()
      toDot.execute(nodes)
      toDot.execute(nodes)
      GraphVizUtil.generateGraph(toDot.getString, outputDir, "gss")
    }
  }
}