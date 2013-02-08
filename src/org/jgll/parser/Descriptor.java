package org.jgll.parser;

import org.jgll.grammar.GrammarSlot;
import org.jgll.sppf.SPPFNode;

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
	
	/**
	 * The SPPF node that was created before parsing the encountered 
	 * nonterminal.
	 */
	private final SPPFNode sppfNode;
	
	/**
	 * Precomputed hash code
	 */
	private final int hash;
	
	public Descriptor(GrammarSlot slot, GSSNode gssNode, int inputIdex, SPPFNode sppfNode) {
		
		this.slot = slot;
		this.gssNode = gssNode;
		this.inputIndex = inputIdex;
		this.sppfNode = sppfNode;
		
		int result = 17;
		result += 31 * result + slot.getId();
		result += 31 * result + sppfNode.hashCode();
		result += 31 * result + inputIdex;
		result += 31 * result + gssNode.hashCode();
		hash = result;
	}
	
	public GrammarSlot getLabel() {
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
		return hash;
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

		return hash == other.hash &&
			   inputIndex == other.getInputIndex() &&
			   slot.equals(other.slot) &&
			   sppfNode.equals(other.getSPPFNode()) &&
			   gssNode.equals(other.getGSSNode());
	}
	
}
