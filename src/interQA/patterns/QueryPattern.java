package interQA.patterns;

import interQA.lexicon.Vocabulary;
import interQA.elements.ParsableElement;
import java.util.ArrayList;
import java.util.List;


public abstract class QueryPattern {

        Vocabulary vocab = new Vocabulary();
        	
	List<ParsableElement> elements = new ArrayList<>();
        
	private int currentElement = -1;    
    
        /**
	 * @param input The whole string from the UI (e.g."who is the")
	 * @return whether the input string matches the query pattern
	 */
	public boolean parses(String input) {

            int i = 0;
            currentElement = -1;

            while (!input.isEmpty() && elements.size() >= i-1) {
		    
                    String rest  = elements.get(i).parse(input); 
                    
                    if (rest == null) { return false; }
                    
                    currentElement = i;
                    updateAt(currentElement);

		    input = rest;
                    i++;                   
            }
            return true;
	}
                
	public List<String> getNext(){
            	
            if (currentElement < elements.size()-1) {
                return elements.get(currentElement+1).getOptions();
            } else {
                return null;
            }
	}
        
        public boolean isComplete() { 
            
            return currentElement == elements.size()-1;
        }
        
        // update(int i) and buildSPARQLqueries() is where the pattern-specific magic happens
        
        public void updateAt(int i) {
            // Needs to be overwritten by all concrete query patterns.
        }

	public List<String> buildSPARQLqueries() {
            // Needs to be overwritten by all concrete query patterns.
            
            return new ArrayList<>();
        }

	
}
