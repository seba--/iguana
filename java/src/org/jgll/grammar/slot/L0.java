package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.parser.GLLParserInternals;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.util.Input;
import org.jgll.util.logging.LoggerWrapper;

/**
 * 
 * @author Ali Afroozeh
 * 
 * TODO: L0 is not really a grammar slot. Change it!
 *
 */
public class L0 extends GrammarSlot {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(L0.class);
	
	private static final long serialVersionUID = 1L;

	private static L0 instance;
	
	public static L0 getInstance() {
		if(instance == null) {
			instance = new L0();
		}
		return instance;
	}
	
	private L0() {
		id = -1;
	}
	
	public GrammarSlot parse(GLLParserInternals parser, Input input, GrammarSlot start) {
		
		GrammarSlot slot = start.parse(parser, input);
		
		while(slot != null) {
			slot = slot.parse(parser, input);
		}
		
		return parse(parser, input);
	}
	
	@Override
	public GrammarSlot parse(GLLParserInternals parser, Input input) {
		while(parser.hasNextDescriptor()) {
			GrammarSlot slot = parser.nextDescriptor().getGrammarSlot();
			slot = slot.parse(parser, input);
			while(slot != null) {
				slot = slot.parse(parser, input);
			}
		}
		return null;
	}
	
	public GrammarSlot recognize(GLLRecognizer recognizer, Input input, GrammarSlot start) {
		GrammarSlot slot = start.recognize(recognizer, input);
		
		while(slot != null) {
			slot = slot.recognize(recognizer, input);
		}
		
		return recognize(recognizer, input);
	}
	
	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, Input input) {
		while(recognizer.hasNextDescriptor()) {
			org.jgll.recognizer.Descriptor descriptor = recognizer.nextDescriptor();
			GrammarSlot slot = descriptor.getGrammarSlot();
			org.jgll.recognizer.GSSNode cu = descriptor.getGSSNode();
			int ci = descriptor.getInputIndex();
			recognizer.update(cu, ci);
			log.trace("Processing (%s, %s, %s)", slot, ci, cu);
			slot = slot.recognize(recognizer, input);
			while(slot != null) {
				slot = slot.recognize(recognizer, input);
			}
		}
		return null;
	}


	@Override
	public void codeParser(Writer writer) throws IOException {
		writer.append("case L0:\n");
		writer.append("if (lookupTable.hasNextDescriptor()) {\n");
		writer.append("Descriptor descriptor = lookupTable.nextDescriptor();\n");
		writer.append("log.debug(\"Processing {}\", descriptor);");
		writer.append("cu = descriptor.getGSSNode();\n");
		writer.append("ci = descriptor.getInputIndex();\n");
		writer.append("cn = descriptor.getSPPFNode();\n");
		writer.append("label = descriptor.getLabel().getId();\n");
		writer.append("break;\n");
		writer.append("} else {\n");
		writer.append("end = System.nanoTime();\n");
		writer.append("log(start, end);\n");
		writer.append("NonterminalSymbolNode root = lookupTable.getStartSymbol(startSymbol);\n");
		writer.append("if (root == null) {");
		writer.append("log.info(\"Parsing failed.\");\n");
		writer.append("throw new ParseError(errorSlot, errorIndex);\n");
		writer.append("}\n");
		writer.append("return root;\n");
		writer.append("}\n");
	}
	
	@Override
	public String toString() {
		return "L0";
	}

}
