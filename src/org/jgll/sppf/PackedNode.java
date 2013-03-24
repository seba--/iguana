package org.jgll.sppf;

import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.GrammarSlot;
import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class PackedNode extends SPPFNode {
	
	private final GrammarSlot slot;

	private final int pivot;
	
	// TODO: check to see how to remove this
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
		
		children = new ArrayList<>(2);
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
		
		return  slot.equals(other.slot) &&
		        pivot == other.pivot &&
		        parent.equals(other.parent);
	}
	
	public int getPivot() {
		return pivot;
	}
	
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
		int result = 17;
		result += 31 * result + slot.getId();
		result += 31 * result + pivot;
		result += 31 * result + parent.hashCode();
		return result;
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
	public String getId() {
		return parent.getId() + "," + slot.getId() + "," + pivot;
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

}