package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.jgll.parser.GrammarInterpreter;

/**
 * 
 * @author Ali Afroozeh
 * 
 */
public class Nonterminal extends GrammarSlot implements Symbol {
	
	private static final long serialVersionUID = 1L;

	private ArrayList<BodyGrammarSlot> alternates;
	private final boolean nullable;
	private String name;

	public Nonterminal(int id, String name, boolean nullable) {
		super(id);
		this.name = name;
		this.nullable = nullable;
		this.alternates = new ArrayList<>();
	}

	public void addAlternate(BodyGrammarSlot slot) {
		alternates.add(slot);
	}

	public boolean isNullable() {
		return nullable;
	}
	
	@Override
	public void execute(GrammarInterpreter parser) {
		for(BodyGrammarSlot slot : alternates) {
			if(slot.checkAgainstTestSet(parser.getCurrentInputValue())) {
				parser.add(slot);
			}
		}
	}

	@Override
	public void code(Writer writer) throws IOException {
		writer.append("// " + name + "\n");
		writer.append("private void parse_" + id + "() {\n");
		for (BodyGrammarSlot slot : alternates) {
			writer.append("   //" + slot + "\n");
			slot.codeIfTestSetCheck(writer);			
			writer.append("   add(grammar.getGrammarSlot(" + slot.id + "), cu, ci, DummyNode.getInstance());\n");
			writer.append("}\n");
		}
		writer.append("   label = L0;\n");
		writer.append("}\n");

		for (BodyGrammarSlot slot : alternates) {
			writer.append("// " + slot + "\n");
			writer.append("private void parse_" + slot.id + "() {\n");
			slot.code(writer);
		}
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@SuppressWarnings("unchecked")
	public List<BodyGrammarSlot> getAlternates() {
		return (List<BodyGrammarSlot>) alternates.clone();
	}

}
