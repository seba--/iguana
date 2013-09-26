package org.jgll.util.dot

import java.io.FileWriter
import org.jgll.util.logging.LoggerWrapper

object GraphVizUtil {

  private val log = LoggerWrapper.getLogger(GraphVizUtil.getClass)

  val SYMBOL_NODE = "[shape=box, style=rounded, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];"

  val INTERMEDIATE_NODE = "[shape=box, height=0.2, width=0.4, color=black, fontcolor=black, label=\"%s\", fontsize=10];"

  val PACKED_NODE = "[shape=circle, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];"

  val EDGE = "edge [color=black, style=solid, penwidth=0.5, arrowsize=0.7];"

  val GSS_NODE = "[shape=circle, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];"

  val GSS_EDGE = "edge [color=black, style=solid, penwidth=0.5, arrowsize=0.7, label=\"%s\"];"

  val NONTERMINAL_NODE = "[shape=circle, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];"

  val SLOT_NODE = "[shape=box, style=rounded, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];"

  val NONTERMINAL_EDGE = "edge [color=black, style=dashed, penwidth=0.5, arrowsize=0.7];"

  val END_EDGE = "edge [color=black, style=dotted, penwidth=0.5, arrowsize=0.7];"

  val TOP_DOWN = 0

  val L2R = 1

  def generateGraph(dot: String, directory: String, fileName: String) {
    generateGraph(dot, directory, fileName, TOP_DOWN)
  }

  def generateGraph(dot: String, 
      directory: String, 
      name: String, 
      layout: Int) {
    val sb = new StringBuilder()
    val lineSeparator = System.getProperty("line.separator")
    sb.append("digraph sppf {").append(lineSeparator)
    sb.append("layout=dot").append(lineSeparator)
    sb.append("nodesep=.6").append(lineSeparator)
    sb.append("ranksep=.4").append(lineSeparator)
    sb.append("ordering=out").append(lineSeparator)
    if (layout == L2R) {
      sb.append("rankdir=LR").append(lineSeparator)
    }
    sb.append(dot)
    sb.append(lineSeparator)
    sb.append("}")
    val fileName = directory + "/" + name
    try {
      val out = new FileWriter(fileName + ".txt")
      out.write(sb.toString)
      out.close()
    } catch {
      case e: Exception => e.printStackTrace()
    }
    generateImage(fileName)
  }

  private def generateImage(fileName: String) {
    val cmd = "/usr/local/bin/dot" + " -Tpdf " + "-o " + fileName + 
      ".pdf" + 
      " " + 
      fileName + 
      ".txt"
    log.info("Running " + cmd)
    try {
      val run = Runtime.getRuntime
      val pr = run.exec(cmd)
      pr.waitFor()
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
}
