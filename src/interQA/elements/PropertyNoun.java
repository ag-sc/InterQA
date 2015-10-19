package interQA.elements;

import interQA.lexicon.Lexicon;


public class PropertyNoun extends ParsableElement{

	InstanceElement instance;

        
	public PropertyNoun(Lexicon lexicon, String frame){
            
            index = lexicon.getNounIndex(frame);
	}


}
