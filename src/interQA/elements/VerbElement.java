package interQA.elements;

import java.util.List;
import interQA.lexicon.Lexicon;
import java.util.ArrayList;


public class VerbElement extends ParsableElement {

	
	public VerbElement(Lexicon lexicon, String frame) {
            
            this.index = lexicon.getVerbIndex(frame);
	}

	@Override
	public List<String> getOptions() {
		
            List<String> lookahead = new ArrayList<>();
            
            lookahead.addAll(index.keySet());
            // TODO filter and order
            
            return lookahead;
	}

	@Override
	public List<String> getInstances() {
		// do something really crazy
		// dobj 
		// SPARQL repository
		return null;
	}

}
