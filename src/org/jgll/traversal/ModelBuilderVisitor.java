package org.jgll.traversal;

import org.jgll.grammar.LastGrammarSlot;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import static org.jgll.traversal.SPPFVisitorUtil.*;

/**
 * ModelBuilderVisitor builds a data model by visiting an SPPF and 
 * calling appropriate models to the provided {@link NodeListener} 
 * interface.
 * 
 * The meta-data uneeded for building models, stored in grammar slots,
 * is automatically retrieved and passed to the given node listener.
 * 
 * @author Ali Afroozeh
 * 
 * @see NodeListener
 *
 */

// TODO: check if the conversions are really type safe! 
@SuppressWarnings("unchecked")
public class ModelBuilderVisitor<T, U> implements SPPFVisitor {
	
	private NodeListener<T, U> listener;
	private Input input;
	
	public ModelBuilderVisitor(Input input, NodeListener<T, U> listener) {
		this.input = input;
		this.listener = listener;
	}

	@Override
	public void visit(TerminalSymbolNode terminal) {
		if(!terminal.isVisited()) {
			terminal.setVisited(true);
			if(terminal.getMatchedChar() == TerminalSymbolNode.EPSILON) {
				terminal.setObject(Result.skip());
			} else {
				Result<U> result = listener.terminal(terminal.getMatchedChar(), PositionInfo.fromNode(terminal, input));
				terminal.setObject(result);
			}
		}		
	}

	@Override
	public void visit(NonterminalSymbolNode nonterminalSymbolNode) {
		removeIntermediateNode(nonterminalSymbolNode);
		
		if(!nonterminalSymbolNode.isVisited()) {
		
			nonterminalSymbolNode.setVisited(true);
			
			if(nonterminalSymbolNode.isAmbiguous()) {
				
				buildAmbiguityNode(nonterminalSymbolNode);
				
			} else {
				LastGrammarSlot slot = (LastGrammarSlot) nonterminalSymbolNode.getFirstPackedNodeGrammarSlot();
				listener.startNode((T) slot.getObject());
				visitChildren(nonterminalSymbolNode, this);
				Result<U> result = listener.endNode((T) slot.getObject(), (Iterable<U>) nonterminalSymbolNode.childrenValues(), PositionInfo.fromNode(nonterminalSymbolNode, input));
				nonterminalSymbolNode.setObject(result);
			}
		}
	}

	private void buildAmbiguityNode(NonterminalSymbolNode nonterminalSymbolNode) {
		int nPackedNodes = 0;
		
		for(SPPFNode child : nonterminalSymbolNode.getChildren()) {
			PackedNode packedNode = (PackedNode) child;
			LastGrammarSlot slot = (LastGrammarSlot) packedNode.getGrammarSlot();
			listener.startNode((T) slot.getObject());
			packedNode.accept(this);
			Result<U> result = listener.endNode((T) slot.getObject(), (Iterable<U>) packedNode.childrenValues(), PositionInfo.fromNode(packedNode, input));
			packedNode.setObject(result);
			if(result != Result.filter()) {
				nPackedNodes++;
			}
		}
		
		if(nPackedNodes > 1) {
			Result<U> result = listener.buildAmbiguityNode((Iterable<U>) nonterminalSymbolNode.childrenValues(), PositionInfo.fromNode(nonterminalSymbolNode,  input));
			nonterminalSymbolNode.setObject(result);
		}
	}

	@Override
	public void visit(IntermediateNode node) {
		// Intermediate nodes should be removed when visiting their parents.
		throw new RuntimeException("Should not be here!");
	}

	@Override
	public void visit(PackedNode packedNode) {
		removeIntermediateNode(packedNode);
		if(!packedNode.isVisited()) {
			packedNode.setVisited(true);
			visitChildren(packedNode, this);
		}
	}

	@Override
	public void visit(ListSymbolNode listNode) {
		removeListSymbolNode(listNode);
		if(!listNode.isVisited()) {
			listNode.setVisited(true);
			if(listNode.isAmbiguous()) {
				buildAmbiguityNode(listNode);
			} else {
				LastGrammarSlot slot = (LastGrammarSlot) listNode.getFirstPackedNodeGrammarSlot();
				listener.startNode((T) slot.getObject());
				visitChildren(listNode, this);
				Result<U> result = listener.endNode((T) slot.getObject(), (Iterable<U>) listNode.childrenValues(), PositionInfo.fromNode(listNode,  input));
				listNode.setObject(result);
			}
		}
	}
	
}
