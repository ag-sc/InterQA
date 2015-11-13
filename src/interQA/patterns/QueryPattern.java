package interQA.patterns;

import interQA.lexicon.Vocabulary;
import interQA.elements.ParsableElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.Lexicon;
import java.util.ArrayList;
import java.util.List;


public abstract class QueryPattern {

        Vocabulary vocab = new Vocabulary();
        Lexicon lexicon;
        InstanceSource instances;
        
	List<ParsableElement> elements = new ArrayList<>();
        
	private int currentElement = -1;    
    
        
        public void init() {
        }

        /**
	 * @param input The whole string from the UI (e.g."who is the")
	 * @return whether the input string matches the query pattern
	 */
	public boolean parses(String input) {

            init();
            
            currentElement = -1;

            int i = 0;
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
                return new ArrayList<>();
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
