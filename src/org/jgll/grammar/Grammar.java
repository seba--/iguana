package org.jgll.grammar;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jgll.util.InputUtil;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Grammar implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final List<HeadGrammarSlot> nonterminals;
	
	private final List<BodyGrammarSlot> slots;
	
	/**
	 * A map from nonterminal names to their corresponding head slots.
	 * This map is used to locate head grammar slots by name for parsing
	 * from any arbitrary nonterminal.
	 */
	private final Map<String, HeadGrammarSlot> nameToHeadSlots;
	
	private final String name;
	
	private int longestTerminalChain;

	public Grammar(String name, List<HeadGrammarSlot> nonterminals, List<BodyGrammarSlot> slots) {
		this.name = name;
		this.nonterminals = Collections.unmodifiableList(nonterminals);
		this.slots = Collections.unmodifiableList(slots);
		this.nameToHeadSlots = new HashMap<>();
		for(HeadGrammarSlot startSymbol : nonterminals) {
			this.nameToHeadSlots.put(startSymbol.getName(), startSymbol);
		}
	}
	
	public static Grammar fromRules(String name, Iterable<Rule> rules) {
		Map<Nonterminal, HeadGrammarSlot> nonterminalMap = new HashMap<>();
		List<BodyGrammarSlot> slots = new ArrayList<>();
		List<HeadGrammarSlot> nonterminals = new ArrayList<>();

		for (Rule rule : rules) {
			nonterminalMap.put(rule.getHead(), new HeadGrammarSlot(nonterminalMap.size(), rule.getHead(), false));
		}

		for (Rule rule : rules) {
			BodyGrammarSlot slot = null;
			HeadGrammarSlot head = nonterminalMap.get(rule.getHead());
			int index = 0;
			for (Symbol symbol : rule.getBody()) {
				if (symbol instanceof Terminal) {
					slot = new TerminalGrammarSlot(slots.size() + nonterminals.size(), index, slot, (Terminal) symbol);
				} else {
					slot = new NonterminalGrammarSlot(slots.size() + nonterminals.size(), index, slot,
													  nonterminalMap.get(symbol), new HashSet<Terminal>());
				}
				slots.add(slot);

				if (index == 0) {
					head.addAlternate(slot);
				}
				index++;
			}
			slots.add(new LastGrammarSlot(slots.size() + nonterminals.size(), index, slot, head, rule.getObject()));
		}

		return new Grammar(name, nonterminals, slots);
	}
	
	public void code(Writer writer, String packageName) throws IOException {
	
		String header = InputUtil.read(this.getClass().getResourceAsStream("ParserTemplate"));
		header = header.replace("${className}", name)
					   .replace("${packageName}", packageName);
		writer.append(header);
		
		// case L0:
		writer.append("case " + L0.getInstance().getId() + ":\n");
		L0.getInstance().code(writer);
		
		for(HeadGrammarSlot nonterminal : nonterminals) {
			writer.append("// " + nonterminal + "\n");
			writer.append("case " + nonterminal.getId() + ":\n");
			writer.append("parse_" + nonterminal.getId() + "();\n");
			writer.append("break;\n");
		}
				
		for(BodyGrammarSlot slot : slots) {
			if(!(slot.previous instanceof TerminalGrammarSlot)) {
				writer.append("// " + slot + "\n");
				writer.append("case " + slot.getId() + ":\n");
				writer.append("parse_" + slot.getId() + "();\n");
				writer.append("break;\n");
			}
		}
		
		writer.append("} } }\n");
		
		for(HeadGrammarSlot nonterminal : nonterminals) {
			nonterminal.code(writer);
		}
		
		writer.append("}");
	}
	
	public String getName() {
		return name;
	}
	
	public HeadGrammarSlot getNonterminal(int id) {
		return nonterminals.get(id);
	}
		
	public BodyGrammarSlot getGrammarSlot(int id) {
		return slots.get(id - nonterminals.size());
	}
		
	public List<HeadGrammarSlot> getNonterminals() {
		return nonterminals;
	}
	
	public List<BodyGrammarSlot> getGrammarSlots() {
		return slots;
	}
	
	public HeadGrammarSlot getNonterminalByName(String name) {
		return nameToHeadSlots.get(name);
	}
	
	public int getLongestTerminalChain() {
		return longestTerminalChain;
	}
	
	public void setLongestTerminalChain(int longestTerminalChain) {
		this.longestTerminalChain = longestTerminalChain;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		for(HeadGrammarSlot nonterminal : nonterminals) {
			for(BodyGrammarSlot slot : nonterminal.getAlternates()) {
				sb.append(nonterminal.getName() + " ::= ");
				BodyGrammarSlot next = slot;
				do {
					sb.append(" ").append(next.getName());
					if(next instanceof LastGrammarSlot) {
						sb.append("\n");
					}
				} 
				while((next = next.next) != null);
			}
			
		}
		
		return sb.toString();
	}
	
}
