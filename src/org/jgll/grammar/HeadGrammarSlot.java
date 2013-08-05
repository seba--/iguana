package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.GLLParserInternals;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.util.Input;

/**
 * 
 * The grammar slot corresponding to the head of a rule.
 * 
 * @author Ali Afroozeh
 * 
 */
public class HeadGrammarSlot extends GrammarSlot {
	
	private static final long serialVersionUID = 1L;

	private List<Alternate> alternates;
	
	private final Nonterminal nonterminal;
	
	private boolean nullable;
	
	private transient final Set<Terminal> firstSet;
	
	private transient final Set<Terminal> followSet;
	
	private transient final Set<HeadGrammarSlot> reachableNonterminals;
	
	public HeadGrammarSlot(Nonterminal nonterminal) {
		super(nonterminal.getName());
		this.nonterminal = nonterminal;
		this.alternates = new ArrayList<>();
		this.firstSet = new HashSet<>();
		this.followSet = new HashSet<>();
		this.reachableNonterminals = new HashSet<>();
	}
	
	public void addAlternate(Alternate alternate) {		
		alternates.add(alternate);
	}
	
	public void setAlternates(List<Alternate> alternates) {
		this.alternates = alternates;
	}
	
	public void removeAlternate(Alternate alternate) {
		alternates.remove(alternate);
	}
	
	public Set<Integer> getAlternateIndices() {
		Set<Integer> set = new HashSet<>();
		for(Alternate alternate : alternates) {
			set.add(alternate.getIndex());
		}
		return set;
	}
	
	public Set<Alternate> without(List<Symbol> list) {
		Set<Alternate> set = new HashSet<>(alternates);
		for(Alternate alternate : alternates) {
			if(alternate.match(list)) {
				set.remove(alternate);
				return set;
			}
		}
		return set;
	}
	
	public void remove(List<Symbol> list) {
		Iterator<Alternate> it = alternates.iterator();
		while(it.hasNext()) {
			Alternate alternate = it.next();
			if(alternate.match(list)) {
				it.remove();
			}
		}
	}
	
	public boolean isNullable() {
		return nullable;
	}
	
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	
	@Override
	public GrammarSlot parse(GLLParserInternals parser, Input input) {
		for(Alternate alternate : alternates) {
			int ci = parser.getCurrentInputIndex();
			BodyGrammarSlot slot = alternate.getFirstSlot();
			if(slot.testFirstSet(ci, input) || (slot.isNullable() && slot.testFollowSet(ci, input))) {
				parser.addDescriptor(slot);
			}
		}
		return null;
	}
	
	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, Input input) {
		for(Alternate alternate : alternates) {
			int ci = recognizer.getCi();
			BodyGrammarSlot slot = alternate.getFirstSlot();
			if(slot.testFirstSet(ci, input) || (slot.isNullable() && slot.testFollowSet(ci, input))) {
				org.jgll.recognizer.GSSNode cu = recognizer.getCu();
				recognizer.add(alternate.getFirstSlot(), cu, ci);
			}
		}
		return null;
	}
	
	@Override
	public void codeParser(Writer writer) throws IOException {
		writer.append("// " + nonterminal.getName() + "\n");
		writer.append("private void parse_" + id + "() {\n");
		for (Alternate alternate : alternates) {
			writer.append("   //" + alternate.getFirstSlot() + "\n");
			alternate.getFirstSlot().codeIfTestSetCheck(writer);			
			writer.append("   add(grammar.getGrammarSlot(" + alternate.getFirstSlot().getId() + "), cu, ci, DummyNode.getInstance());\n");
			writer.append("}\n");
		}
		writer.append("   label = L0;\n");
		writer.append("}\n");

		for (Alternate alternate : alternates) {
			writer.append("// " + alternate + "\n");
			writer.append("private void parse_" + alternate.getFirstSlot().getId() + "() {\n");
			alternate.getFirstSlot().codeParser(writer);
		}
	}
	
	public Alternate getAlternateAt(int index) {
		return alternates.get(index);
	}
	
	public List<Alternate> getAlternatesIncludingNull() {
		return alternates;
	}

	public List<Alternate> getAlternates() {
		return new ArrayList<>(alternates);
	}
	
	public Nonterminal getNonterminal() {
		return nonterminal;
	}
		
	public Set<Terminal> getFirstSet() {
		return firstSet;
	}
	
	public Set<Terminal> getFollowSet() {
		return followSet;
	}
	
	public int getCountAlternates() {
		return alternates.size();
	}
	
	public Set<HeadGrammarSlot> getReachableNonterminals() {
		return reachableNonterminals;
	}
	
	public boolean contains(List<Symbol> list) {
		for(Alternate alternate : alternates) {
			if(alternate.match(list)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return nonterminal.toString();
	}

}
