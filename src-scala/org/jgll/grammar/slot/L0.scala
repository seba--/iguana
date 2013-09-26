package org.jgll.grammar.slot

import java.io.Writer
import org.jgll.parser.GLLParserInternalsTrait
import org.jgll.recognizer.{DescriptorTrait, GLLRecognizerTrait}
import org.jgll.util.logging.LoggerWrapper
import scala.reflect.BeanProperty
import org.jgll.util.InputTrait

trait L0Trait {
  self: GrammarSlotTrait
   with GLLParserInternalsTrait
   with InputTrait
   with GLLRecognizerTrait
   with DescriptorTrait
   =>
  object L0 extends GrammarSlot  {

    private val log = LoggerWrapper.getLogger(L0.getClass)

    id = -1

    def parse(parser: GLLParserInternals, input: Input, start: GrammarSlot): GrammarSlot = {
      var slot = start.parse(parser, input)
      while (slot != null) {
        slot = slot.parse(parser, input)
      }
      parse(parser, input)
    }

    override def parse(parser: GLLParserInternals, input: Input): GrammarSlot = {
      while (parser.hasNextDescriptor()) {
        var slot = parser.nextDescriptor().getGrammarSlot
        slot = slot.parse(parser, input)
        while (slot != null) {
          slot = slot.parse(parser, input)
        }
      }
      null
    }

    def recognize(recognizer: GLLRecognizer, input: Input, start: GrammarSlot): GrammarSlot = {
      var slot = start.recognize(recognizer, input)
      while (slot != null) {
        slot = slot.recognize(recognizer, input)
      }
      recognize(recognizer, input)
    }

    override def recognize(recognizer: GLLRecognizer, input: Input): GrammarSlot = {
      while (recognizer.hasNextDescriptor()) {
        val descriptor = recognizer.nextDescriptor()
        var slot = descriptor.getGrammarSlot
        val cu = descriptor.getGSSNode
        val ci = descriptor.getInputIndex
        recognizer.update(cu, ci)
        log.trace("Processing (%s, %s, %s)", slot, ci, cu)
        slot = slot.recognize(recognizer, input)
        while (slot != null) {
          slot = slot.recognize(recognizer, input)
        }
      }
      null
    }

    override def codeParser(writer: Writer) {
      writer.append("case L0:\n")
      writer.append("if (lookupTable.hasNextDescriptor()) {\n")
      writer.append("Descriptor descriptor = lookupTable.nextDescriptor();\n")
      writer.append("log.debug(\"Processing {}\", descriptor);")
      writer.append("cu = descriptor.getGSSNode();\n")
      writer.append("ci = descriptor.getInputIndex();\n")
      writer.append("cn = descriptor.getSPPFNode();\n")
      writer.append("label = descriptor.getLabel().getId();\n")
      writer.append("break;\n")
      writer.append("} else {\n")
      writer.append("end = System.nanoTime();\n")
      writer.append("log(start, end);\n")
      writer.append("NonterminalSymbolNode root = lookupTable.getStartSymbol(startSymbol);\n")
      writer.append("if (root == null) {")
      writer.append("log.info(\"Parsing failed.\");\n")
      writer.append("throw new ParseError(errorSlot, errorIndex);\n")
      writer.append("}\n")
      writer.append("return root;\n")
      writer.append("}\n")
    }

    override def toString(): String = "L0"
  }
}