package org.jgll.sppf;

import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.HashFunctions;
import org.jgll.traversal.SPPFVisitor;
import org.jgll.util.hashing.Decomposer;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class PackedNode extends SPPFNode {
	
	private final GrammarSlot slot;

	private final int pivot;

	private final SPPFNode parent;
	
	private final List<SPPFNode> children;
	
	public PackedNode(GrammarSlot slot, int pivot, NonPackedNode parent) {
		
		if(slot == null) {
			throw new IllegalArgumentException("Gramar slot cannot be null.");
		}
		
		if(pivot < 0) {
			throw new IllegalArgumentException("Pivot should be a positive integer number.");
		}
		
		if(parent == null) {
			throw new IllegalArgumentException("The parent node cannot be null.");
		}
		
		this.slot = slot;
		this.pivot = pivot;
		this.parent = parent;
		
		this.children = new ArrayList<>(2);
	}
			
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}

		if (!(obj instanceof PackedNode)) {
			return false;
		}
		
		PackedNode other = (PackedNode) obj;
		
		return  slot == other.slot &&
		        pivot == other.pivot &&
		        parent.getGrammarSlot() == other.parent.getGrammarSlot() &&
		        parent.getLeftExtent() == other.parent.getLeftExtent() &&
		        parent.getRightExtent() == other.parent.getRightExtent();
	}
	
	public int getPivot() {
		return pivot;
	}
	
	@Override
	public GrammarSlot getGrammarSlot() {
		return slot;
	}
	
	public SPPFNode getParent() {
		return parent;
	}
	
	public void addChild(SPPFNode node) {
		children.add(node);
	}

	public void removeChild(SPPFNode node) {
		children.remove(node);
	}
	
	public void replaceWithChildren(SPPFNode node) {
		int index = children.indexOf(node);
		children.remove(node);
		if(index >= 0) {
			for(SPPFNode child : node.getChildren()) {
				children.add(index++, child);				
			}
		}
	}

	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction().hash(slot.getId(), pivot, parent.hashCode());
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %d)", getLabel(), getPivot());
	}
	
	@Override
	public String getLabel() {
		return slot.toString();
	}
	
	@Override
	public int getLeftExtent() {
		return parent.getLeftExtent();
	}

	@Override
	public int getRightExtent() {
		return parent.getRightExtent();
	}

	@Override
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}

	@Override
	public SPPFNode getChildAt(int index) {
		if(children.size() > index) {
			return children.get(index);
		}
		return null;
	}

	@Override
	public int childrenCount() {
		return children.size();
	}

	@Override
	public Iterable<SPPFNode> getChildren() {
		return children;
	}

	@Override
	public boolean isAmbiguous() {
		return false;
	}

	@Override
	public int getLevel() {
		return parent.getRightExtent();
	}
	
	public static class PackedNodeDecomposer implements Decomposer<PackedNode> {

		private int[] components = new int[5];
		
		@Override
		public int[] toIntArray(PackedNode packedNode) {
			components[0] = packedNode.slot.getId();
			components[1] = packedNode.pivot;
			components[2] = packedNode.parent.getGrammarSlot().getId();
			components[3] = packedNode.parent.getLeftExtent();
			components[4] = packedNode.parent.getRightExtent();
			return components;
		}
		
	}


}