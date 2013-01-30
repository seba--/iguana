package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import org.jgll.parser.ParserInterpreter;
import org.jgll.sppf.NonPackedNode;

/**
 * A grammar slot whose next immediate symbol is a terminal.
 * 
 * @author Ali Afroozeh	<afroozeh@gmail.com>
 *
 */
public class TerminalGrammarSlot extends BodyGrammarSlot {
	
	private final Terminal terminal;

	public TerminalGrammarSlot(int id, String label, int position, BodyGrammarSlot previous, Terminal terminal) {
		super(id, label, position, previous);
		this.terminal = terminal;
	}
	
	public Terminal getTerminal() {
		return terminal;
	}

	@Override
	public Set<Terminal> getTestSet() {
		return null;
	}
	
	@Override
	public Object execute(ParserInterpreter parser) {
		if(previous == null && next == null) {
			NonPackedNode cr = parser.getNodeT(-2, parser.getCurrentInpuIndex());
			parser.setCR(cr);
			parser.getNodeP(this, parser.getCN(), parser.getCR());
			parser.pop();
			L0.getInstance().execute(parser);
		} 
		
		else if(previous == null && next.next == null) {
			if(terminal.match(parser.getCurrentInputValue())) {
				NonPackedNode cr = parser.getNodeT(parser.getCurrentInputValue(), parser.getCurrentInpuIndex());
				parser.setCR(cr);
				parser.moveInputPointer();
				parser.getNodeP(next);
				parser.pop();
			} else {
				L0.getInstance().execute(parser);
			}
		}
		
		else if(previous == null && !(next.next == null)) {
			if(terminal.match(parser.getCurrentInputValue())) {
				NonPackedNode cn = parser.getNodeT(parser.getCurrentInputValue(), parser.getCurrentInpuIndex());
				parser.setCN(cn);
				parser.moveInputPointer();
				next.execute(parser);
			} else {
				L0.getInstance().execute(parser);
			}
		}
		
		else {
			if(terminal.match(parser.getCurrentInputValue())) {
				NonPackedNode cr = parser.getNodeT(parser.getCurrentInputValue(), parser.getCurrentInpuIndex());
				parser.setCR(cr);
				parser.moveInputPointer();
				parser.getNodeP(next);
				next.execute(parser);
			} else {
				L0.getInstance().execute(parser);
			}
		}
		
		return null;
	}
	
	@Override
	public void code(Writer writer) throws IOException {
		
		// code(A ::= ε) = 
		// 					cR := getNodeT(ε,cI); 
		// 					cN := getNodeP(A ::= ·,cN,cR)
		// 					pop(cU , cI , cN ); 
		// 					goto L0
		if(previous == null && next == null) {
			writer.append("   cr = getNodeT(-1, ci);\n");
			writer.append("   cn = getNodeP(grammar.getGrammarSlot(" + id + "), cn, cr);\n");
			writer.append("   pop(cu, ci, cn);\n");
			writer.append("   label = L0;\n}\n");
		}
		
		// code(A::= x1) = 
		//				  cR := getNodeT(x1,cI); 
		//				  cI :=cI +1 
		// 				  cN := getNodeP(X ::= x1., cN , cR)
		//		 		  pop(cU,cI,cN); 
		//				  gotoL0
		else if(previous == null && next.next == null) {
			writer.append(checkInput(terminal));
			writer.append("   cr = getNodeT(I[ci], ci);\n");
			writer.append(elseCheckInput());
			writer.append("   ci = ci + 1;\n");
			writer.append("   cn = getNodeP(grammar.getGrammarSlot(" + next.id + "), cn, cr);\n");
			writer.append("   pop(cu, ci, cn);\n");
			writer.append("   label = L0;\n}\n");
		}
		
		// If f ≥ 2 and x1 is a terminal
		else if(previous == null && !(next.next == null)) {
			writer.append(checkInput(terminal));
			writer.append("   cn = getNodeT(I[ci], ci);\n");
			writer.append(elseCheckInput());
			writer.append("   ci = ci + 1;\n");
			
			BodyGrammarSlot slot = next;
			// while slot is one before the end, i.e, α . x
			while(slot != null) {
				slot.code(writer);
				slot = slot.next;
			}
		}
		
		// code(A::=α·aβ) = 
		//					if(I[cI] = a)
		//						cR := getNodeT(a,cI) 
		//					else gotoL0
		// 					cI :=cI +1; 
		//					cN :=getNodeP(A::=αa·β,cN,cR)
		else {
			writer.append(checkInput(terminal));
			writer.append("     cr = getNodeT(I[ci], ci);\n");
			writer.append("   } else {\n");
			writer.append("     label = L0; return;\n");
			writer.append("   }\n");
			
			writer.append("   ci = ci + 1;\n");
			writer.append("   cn = getNodeP(grammar.getGrammarSlot(" + next.getId() + "), cn, cr);\n");
		}
	}
	
	private String checkInput(Terminal terminal) {
		String s = "";
		if(terminal instanceof Range) {
			s += "   if(I[ci] >= " +  ((Range) terminal).getStart() + " + && I[ci] <= " + ((Range) terminal).getEnd() + ") {\n";	
		} else {
			s += "   if(I[ci] == " + ((Character) terminal).get() + ") {\n";
		}
		return s;
	}
	
	private String elseCheckInput() {
		return "    } else {label = L0; return; }\n";
	}

}
