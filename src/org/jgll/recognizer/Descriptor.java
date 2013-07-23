package org.jgll.recognizer;

import org.jgll.grammar.GrammarSlot;
import org.jgll.parser.HashFunctions;
import org.jgll.util.hashing.Decomposer;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class Descriptor {
	
	/**
	 * The label that indicates the parser code to execute for the encountered
	 * nonterminal.
	 */
	private final GrammarSlot slot;
	
	/**
	 * The associated GSSNode.
	 */
	private final GSSNode gssNode;
	
	/**
	 * The current index in the input string.
	 */
	private final int inputIndex;
	
	
	public Descriptor(GrammarSlot slot, GSSNode gssNode, int inputIndex) {
		
		assert slot != null;
		assert gssNode != null;
		assert inputIndex >= 0;
		
		this.slot = slot;
		this.gssNode = gssNode;
		this.inputIndex = inputIndex;
	}
	
	public GrammarSlot getGrammarSlot() {
		return slot;
	}

	public GSSNode getGSSNode() {
		return gssNode;
	}
	
	public int getInputIndex() {
		return inputIndex;
	}
	
	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction().hash(slot.getId(), inputIndex, gssNode.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) { 
			return true;
		}
		
		if(! (obj instanceof Descriptor)) {
			return false;
		}
		
		Descriptor other = (Descriptor) obj;

		return inputIndex == other.getInputIndex() &&
			   slot.equals(other.slot) &&
			   gssNode.equals(other.getGSSNode());
	}
	
	@Override
	public String toString() {
		return "(" + slot + ", " + inputIndex + ", " + gssNode + ")";
	}

	public static class DescriptorDecomposer implements Decomposer<Descriptor> {

		private int[] components = new int[4];
		
		@Override
		public int[] toIntArray(Descriptor descriptor) {
			components[0] = descriptor.slot.getId();
			components[1] = descriptor.inputIndex;
			components[2] = descriptor.gssNode.getGrammarSlot().getId();
			components[3] = descriptor.gssNode.getInputIndex();
			return components;
		}
	}
	
}
