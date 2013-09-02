package org.jgll.grammar.slotaction;

import java.util.List;

import org.jgll.grammar.Keyword;
import org.jgll.grammar.SlotAction;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.parser.GLLParserInternals;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.recognizer.RecognizerFactory;
import org.jgll.util.Input;
import org.jgll.util.hashing.CuckooHashSet;


public class NotMatchActions {

	  public static void fromGrammarSlot(BodyGrammarSlot slot, final BodyGrammarSlot ifNot) {

			slot.addPopAction(new SlotAction<Boolean>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public Boolean execute(GLLParserInternals parser, Input input) {
					GLLRecognizer recognizer = RecognizerFactory.contextFreeRecognizer();
					return recognizer.recognize(input, parser.getCurrentGSSNode().getInputIndex(), parser.getCurrentInputIndex(), ifNot);
				}
			});
		}
	  
		public static void fromKeywordList(BodyGrammarSlot slot, final List<Keyword> list) {
			
			if(list.size() == 1) {
				final Keyword s = list.get(0);
				
				slot.addPopAction(new SlotAction<Boolean>() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public Boolean execute(GLLParserInternals parser, Input input) {
						return input.match(parser.getCurrentGSSNode().getInputIndex(), parser.getCurrentInputIndex(), s.getChars());
					}
				});
				
			} 
			
			else if(list.size() == 2) {
				final Keyword s1 = list.get(0);
				final Keyword s2 = list.get(1);
				
				slot.addPopAction(new SlotAction<Boolean>() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public Boolean execute(GLLParserInternals parser, Input input) {
						int begin = parser.getCurrentGSSNode().getInputIndex();
						int end = parser.getCurrentInputIndex();
						return input.match(begin, end, s1.getChars()) ||
							   input.match(begin, end, s2.getChars())	;
					}
				});
			} 
			
			else {
				final CuckooHashSet<Keyword> set = new CuckooHashSet<>(Keyword.externalHasher);
				for(Keyword s : list) {
					set.add(s);
				}
				
				slot.addPopAction(new SlotAction<Boolean>() {

					private static final long serialVersionUID = 1L;
					
					@Override
					public Boolean execute(GLLParserInternals parser, Input input) {
						int begin = parser.getCurrentGSSNode().getInputIndex();
						int end = parser.getCurrentInputIndex() - 1;
						Keyword subInput = new Keyword("", input.subInput(begin, end));
						return set.contains(subInput);
					}
				});
			}
		}

	
}
