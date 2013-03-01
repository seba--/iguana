package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.jgll.parser.GrammarInterpreter;

/**
 * 
 * The grammar slot corresponding to the head of a rule.
 * 
 * @author Ali Afroozeh
 * 
 */
public class HeadGrammarSlot extends GrammarSlot {
	
	private static final long serialVersionUID = 1L;

	private final ArrayList<BodyGrammarSlot> alternates;
	
	private final boolean nullable;
	
	private final Nonterminal nonterminal;

	public HeadGrammarSlot(int id, Nonterminal nonterminal, boolean nullable) {
		super(id);
		this.nonterminal = nonterminal;
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
		for(BodyGrammarSlot slot : getReverseAlternates()) {
			if(slot.checkAgainstTestSet(parser.getCurrentInputValue())) {
				parser.add(slot);
			}
		}
	}

	@Override
	public void code(Writer writer) throws IOException {
		writer.append("// " + nonterminal.getName() + "\n");
		writer.append("private void parse_" + id + "() {\n");
		for (BodyGrammarSlot slot : getReverseAlternates()) {
			writer.append("   //" + slot + "\n");
			slot.codeIfTestSetCheck(writer);			
			writer.append("   add(grammar.getGrammarSlot(" + slot.id + "), cu, ci, DummyNode.getInstance());\n");
			writer.append("}\n");
		}
		writer.append("   label = L0;\n");
		writer.append("}\n");

		for (BodyGrammarSlot slot : getReverseAlternates()) {
			writer.append("// " + slot + "\n");
			writer.append("private void parse_" + slot.id + "() {\n");
			slot.code(writer);
		}
	}

	@Override
	public String getName() {
		return nonterminal.getName();
	}
	
	@Override
	public String toString() {
		return nonterminal.getName();
	}
	
	public Iterable<BodyGrammarSlot> getAlternates() {
		return alternates;
	}
	
	public Nonterminal getNonterminal() {
		return nonterminal;
	}
	
	public Iterable<BodyGrammarSlot> getReverseAlternates() {
		return new Iterable<BodyGrammarSlot>() {
			
			@Override
			public Iterator<BodyGrammarSlot> iterator() {
				return new Iterator<BodyGrammarSlot>() {

					ListIterator<BodyGrammarSlot> listIterator = alternates.listIterator(alternates.size());
					
					@Override
					public boolean hasNext() {
						return listIterator.hasPrevious();
					}

					@Override
					public BodyGrammarSlot next() {
						return listIterator.previous();
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
	
	
	/**
	 * Returns a shallow copy of the alternates. This method is useful when
	 * one defines filters based on the existing alternates.
	 */
	@SuppressWarnings("unchecked")
	public List<BodyGrammarSlot> copyAlternates() {
		return (List<BodyGrammarSlot>) alternates.clone();
	}

}
