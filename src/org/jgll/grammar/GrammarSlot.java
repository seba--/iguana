package org.jgll.grammar;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import org.jgll.parser.GLLParser;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.util.Input;

/**
 * A GrammarSlot is the position immediately before or after
 * any symbol in an alternate. They are denoted by LR(0) items. 
 * In X ::= alpha . beta, the grammar symbol denoted by . is after
 * alpha and before beta.
 * 
 * Note: the equality of two grammar slots is based on their
 * referential equality, i.e., if they refer to the same object.
 * Thet's why these classes inherit the equals and hash code implementations 
 * from the Object class.
 * 
 * @author Ali Afroozeh
 *
 */
public abstract class GrammarSlot implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * A unique integer specifying this grammar slot.
	 * The id field is used later for looking up grammar slots.
	 */
	protected int id;
	
	/**
	 * A string representation of this grammar slot
	 */
	protected String label;
	
	public GrammarSlot(int id, String label) {
		this.id = id;
		this.label = label;
	}
	
	public abstract void codeParser(Writer writer) throws IOException;
	
	public abstract GrammarSlot parse(GLLParser parser, Input input);
	
	public abstract void recognize(GLLRecognizer recognizer, Input input);
	
	/**
	 * The name of the grammar symbol immediately after this grammar slot
	 */
	public abstract String getSymbolName();
	
	public int getId() {
		return id;
	}
	
	public String getLabel() {
		return label;
	}
	
	@Override
	public String toString() {
		return label;
	}
	
}
