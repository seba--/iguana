package org.jgll.util;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.traversal.SPPFVisitor;
import org.jgll.traversal.SPPFVisitorUtil;

/**
 * Creates a Graphviz's dot format representation of an SPPF node.
 * 
 * @author Ali Afroozeh
 * 
 * @see SPPFVisitor
 */
public class ToDotWithoutIntermediateNodes extends ToDot {
	
	@Override
	public void visit(PackedNode node, StringBuilder sb) {
		SPPFVisitorUtil.removeIntermediateNode(node);
		super.visit(node, sb);
	}
	
	@Override
	public void visit(NonterminalSymbolNode node, StringBuilder sb) {
		SPPFVisitorUtil.removeIntermediateNode(node);
		super.visit(node, sb);
	}
}
