package interQA.elements;

import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;


public class ClassElement extends ParsableElement {

	
	public ClassElement(Lexicon lexicon, LexicalEntry.POS pos, String frame) {
            
            this.index = lexicon.getSubindex(pos,frame);
	}

}
