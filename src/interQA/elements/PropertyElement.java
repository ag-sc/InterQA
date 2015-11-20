package interQA.elements;

import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;


public class PropertyElement extends ParsableElement {

	
	public PropertyElement(Lexicon lexicon, LexicalEntry.POS pos, String frame) {
            
            this.index = lexicon.getSubindex(pos,frame);
	}

}
