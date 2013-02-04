package org.jgll.action;

import java.util.ArrayList;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;

public class RemoveEpsilonTerminals implements VisitAction {

	@Override
	public void execute(SPPFNode node) {
		
		if (! (node instanceof NonterminalSymbolNode)) {
			return;
		}
		
		NonterminalSymbolNode nt = (NonterminalSymbolNode) node;
				
		for(SPPFNode child : nt.getChildren()) {
			
			if(!(child instanceof TerminalSymbolNode)) {
				return;
			}
			
			TerminalSymbolNode terminalSymbolNode = (TerminalSymbolNode) child;
			
			// Leaf nodes should not be removed.
			if(terminalSymbolNode.getLabel().equals("")) {
				nt.setChildren(new ArrayList<SPPFNode>());
			}
		}
	}
	
}