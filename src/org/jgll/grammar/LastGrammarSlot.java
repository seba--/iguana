package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;

import org.jgll.parser.GrammarInterpreter;


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
	 * This object will appear as a property of nonterminal symbol nodes in a parse tree.
	 * 
	 */
	private Object object;


	public LastGrammarSlot(Rule rule, int id, int position, BodyGrammarSlot previous) {
		super(rule, id, position, previous);
	}
		
	@Override
	public void execute(GrammarInterpreter parser) {
		parser.pop();
	}

	@Override
	public void code(Writer writer) throws IOException {
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
	
	public void setObject(Object object) {
		this.object = object;
	}
	
	public Object getObject() {
		return object;
	}

}
