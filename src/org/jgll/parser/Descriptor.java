package org.jgll.parser;

import org.jgll.grammar.GrammarSlot;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.hashing.Decomposer;
import org.jgll.util.hashing.Level;

/**
 * A {@code Descriptor} is used by the GLL parser to keep track of the 
 * nonterminals that have been encountered during the parsing process but that
 * have not been processed yet.
 * <br />
 * A descriptor is a 4-tuple that contains a {@code label} that is used to
 * indicate the code that has to be executed to parse the encountered 
 * nonterminal, a {@code gssNode} which represents the current top in the 
 * Graph Structured Stack, an {@code index}, which is the current location in 
 * the input string and an {@code sppfNode} which is the SPPF node that was
 * created for the nonterminal.
 * 
 * Note: this class has a natural ordering that is inconsistent with equals.
 * 
 * @author Maarten Manders
 * @author Ali Afroozeh
 * 
 */

public class Descriptor implements Level {
	
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
	
	/**
	 * The SPPF node that was created before parsing the encountered 
	 * nonterminal.
	 */
	private final SPPFNode sppfNode;
	
	public Descriptor(GrammarSlot slot, GSSNode gssNode, int inputIndex, SPPFNode sppfNode) {
		assert slot != null;
		assert gssNode != null;
		assert inputIndex >= 0;
		assert sppfNode != null;
		
		this.slot = slot;
		this.gssNode = gssNode;
		this.inputIndex = inputIndex;
		this.sppfNode = sppfNode;
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

	public SPPFNode getSPPFNode() {
		return sppfNode;
	}
	
	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction().hash(slot.getId(), 
												   sppfNode.getGrammarSlot().getId(), 
												   gssNode.getGrammarSlot().getId(),
												   gssNode.getInputIndex(),
												   inputIndex);
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
		
		return slot == other.slot &&
			   sppfNode.getGrammarSlot() == other.sppfNode.getGrammarSlot() &&
			   gssNode.getGrammarSlot() == other.gssNode.getGrammarSlot() &&
			   gssNode.getInputIndex() == other.gssNode.getInputIndex() &&
			   inputIndex == other.getInputIndex();
	}
	
	@Override
	public String toString() {
		return "(" + slot + ", " + inputIndex + ", " + gssNode.getGrammarSlot() + ", " + sppfNode + ")";
	}

	@Override
	public int getLevel() {
		return inputIndex;
	}
	
	public static class DescriptorDecomposer implements Decomposer<Descriptor> {

		private int[] components = new int[5];
		
		@Override
		public int[] toIntArray(Descriptor descriptor) {
			components[0] = descriptor.slot.getId();
			components[1] = descriptor.sppfNode.getGrammarSlot().getId();
			components[2] = descriptor.gssNode.getGrammarSlot().getId();
			components[3] = descriptor.gssNode.getInputIndex();
			components[4] = descriptor.inputIndex;
			return components;
		}
		
	}
}