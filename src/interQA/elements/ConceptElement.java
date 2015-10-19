package interQA.elements;

import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;


public class ConceptElement extends ParsableElement {

	
	public ConceptElement(Lexicon lexicon, LexicalEntry.POS pos, String frame) {
            
            this.index = lexicon.getSubindex(pos,frame);
	}

}
