package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;

import org.jgll.parser.GLLParser;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.util.Input;



/**
 * Corresponds to the last grammar slot in an alternate, e.g., X ::= alpha .
 * 
 * @author Ali Afroozeh
 *
 */
public class LastGrammarSlot extends BodyGrammarSlot {
	
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * An arbitrary data object that can be put in this grammar slot and
	 * retrieved later when traversing the parse tree.
	 * This object can be accessed via the getObject() method of a nonterminal symbol node.
	 */
	private transient Object object;
	
	public LastGrammarSlot(String label, int position, BodyGrammarSlot previous, HeadGrammarSlot head, Object object) {
		super(label, position, previous, head);
		this.object = object;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, Input input) {
		parser.pop();
		return null;
	}
	
	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, Input input) {
//		return recognizer.pop(recognizer.getCu(), recognizer.getCi());
		return null;
	}

	@Override
	public void codeParser(Writer writer) throws IOException {
		writer.append("   pop(cu, ci, cn);\n");
		writer.append("   label = L0;\n}\n");
	}

	@Override
	public boolean checkAgainstTestSet(int i) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void codeIfTestSetCheck(Writer writer) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	public Object getObject() {
		return object;
	}
	
	public void setObject(Object object) {
		this.object = object;
	}

	@Override
	public boolean isTerminalSlot() {
		return false;
	}

	@Override
	public boolean isNonterminalSlot() {
		return false;
	}

	@Override
	public boolean isLastSlot() {
		return true;
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	public Symbol getSymbol() {
		return Epsilon.getInstance();
	}

}
