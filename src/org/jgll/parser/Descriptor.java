package org.jgll.parser;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.hashing.ExternalHasher;
import org.jgll.util.hashing.HashFunction;
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
	
	public static final ExternalHasher<Descriptor> externalHasher = new DescriptorExternalHasher();
	public static final ExternalHasher<Descriptor> levelBasedExternalHasher = new LevelBasedExternalHasher();
	
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
		return externalHasher.hash(this, HashFunctions.defaulFunction());
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
	
	public static class DescriptorExternalHasher implements ExternalHasher<Descriptor> {

		private static final long serialVersionUID = 1L;

		@Override
		public int hash(Descriptor d, HashFunction f) {
			return f.hash(d.slot.getId(), 
       					  d.sppfNode.getGrammarSlot().getId(), 
						  d.gssNode.getGrammarSlot().getId(),
						  d.gssNode.getInputIndex(),
						  d.inputIndex);
		}

		@Override
		public boolean equals(Descriptor d1, Descriptor d2) {
			return 	d1.inputIndex == d2.inputIndex &&
					d1.slot.getId() == d2.slot.getId() && 
 					d1.sppfNode.getGrammarSlot() == d2.sppfNode.getGrammarSlot() && 
					d1.gssNode.getGrammarSlot() == d2.gssNode.getGrammarSlot() &&
					d1.gssNode.getInputIndex() == d2.gssNode.getInputIndex();
		}
	}
	
	public static class LevelBasedExternalHasher implements ExternalHasher<Descriptor> {

		private static final long serialVersionUID = 1L;

		@Override
		public int hash(Descriptor d, HashFunction f) {
			return f.hash(d.slot.getId(), 
       					  d.sppfNode.getGrammarSlot().getId(), 
						  d.gssNode.getGrammarSlot().getId(),
						  d.gssNode.getInputIndex());
		}
		
		@Override
		public boolean equals(Descriptor d1, Descriptor d2) {
			return 	d1.slot.getId() == d2.slot.getId() && 
 					d1.sppfNode.getGrammarSlot() == d2.sppfNode.getGrammarSlot() && 
					d1.gssNode.getGrammarSlot() == d2.gssNode.getGrammarSlot() &&
					d1.gssNode.getInputIndex() == d2.gssNode.getInputIndex();
		}

	}
	
	
	
}