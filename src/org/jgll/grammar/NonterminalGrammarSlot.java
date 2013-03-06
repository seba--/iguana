package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import org.jgll.parser.GrammarInterpreter;

/**
 * A grammar slot immediately before a nonterminal.
 * 
 * @author Ali Afroozeh
 *
 */
public class NonterminalGrammarSlot extends BodyGrammarSlot {
	
	private static final long serialVersionUID = 1L;

	private HeadGrammarSlot pointer;
	
	private Set<Terminal> testSet;
	
	public NonterminalGrammarSlot(int id, int position, BodyGrammarSlot previous, HeadGrammarSlot head, HeadGrammarSlot pointer) {
		super(id, position, previous, head);
		if(pointer == null) {
			throw new IllegalArgumentException("Nonterminal cannot be null.");
		}
		this.pointer = pointer;
		pointer.addInstance(this);
	}
	
	public HeadGrammarSlot getNonterminal() {
		return pointer;
	}
	
	public void setNonterminal(HeadGrammarSlot nonterminal) {
		pointer.getInstances().remove(this);
		nonterminal.getInstances().add(this);
		this.pointer = nonterminal;
	}
	
	@Override
	public void execute(GrammarInterpreter parser) {
		if(checkAgainstTestSet(parser.getCurrentInputValue())) {
			parser.setCU(parser.create(next));
			pointer.execute(parser);
		} else {
			parser.newParseError(this, parser.getCurrentInpuIndex());
		}
	}
	
	@Override
	public void code(Writer writer) throws IOException {
		
		if(previous == null) {
			codeIfTestSetCheck(writer);
			writer.append("   cu = create(grammar.getGrammarSlot(" + next.id + "), cu, ci, cn);\n");
			writer.append("   label = " + pointer.getId() + ";\n");
			codeElseTestSetCheck(writer);
			writer.append("}\n");
			
			writer.append("// " + next + "\n");
			writer.append("private void parse_" + next.id + "() {\n");
			
			BodyGrammarSlot slot = next;
			while(slot != null) {
				slot.code(writer);
				slot = slot.next;
			}
		} 
		
		else { 
		
			// TODO: add the testSet check
			// code(A ::= α · Xl β) = 
			//						if(test(I[cI ], A, Xβ) {
			// 							cU :=create(RXl,cU,cI,cN); 
			//							gotoLX 
			//						}
			// 						else goto L0
			// RXl:
			codeIfTestSetCheck(writer);
			writer.append("   cu = create(grammar.getGrammarSlot(" + next.id + "), cu, ci, cn);\n");
			writer.append("   label = " + pointer.getId() + ";\n");
			codeElseTestSetCheck(writer);
			writer.append("}\n");
			
			writer.append("// " + next + "\n");
			writer.append("private void parse_" + next.id + "(){\n");
		}
	}
	
	@Override
	public void codeIfTestSetCheck(Writer writer) throws IOException {
		writer.append("if (");
		int i = 0;
		for(Terminal terminal : testSet) {
			writer.append(terminal.getMatchCode());
			if(++i < testSet.size()) {
				writer.append(" || ");
			}
		}
		writer.append(") {\n");
	}

	@Override
	public boolean checkAgainstTestSet(int i) {
		if(testSet.isEmpty()) {
			return true;
		}
		for(Terminal t : testSet) {
			if(t.match(i)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterable<Terminal> getTestSet() {
		return testSet;
	}
	
	@Override
	public String getName() {
		return pointer.getName();
	}
	
}