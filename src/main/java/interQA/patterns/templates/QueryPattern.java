package interQA.patterns.templates;

import interQA.lexicon.Vocabulary;
import interQA.elements.Element;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import interQA.lexicon.SparqlQueryBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public abstract class QueryPattern {

        public Vocabulary vocab = new Vocabulary();
        public SparqlQueryBuilder sqb = new SparqlQueryBuilder();
        
        public boolean count = false;        
        
        public Lexicon lexicon;
        public DatasetConnector dataset;
        
	public List<Element> elements = new ArrayList<>();
        
	public int currentElement = -1;   
        
        public Set<String> queries = new HashSet<>();
    
        
        public void init() {
        }
        
            
        public Element getElement(int i) {
            
            return elements.get(i);
        }
        

        /**
	 * @param input The whole string from the UI (e.g."who is the")
	 * @return whether the input string matches the query pattern
	 */
	
        public boolean parses(String input) {

            init();
            
            currentElement = -1;
            
            int i = 0;
            while (!input.isEmpty() && elements.size() > i) {
                   
                    String rest = elements.get(i).parse(input);
                    if (rest == null) { return false; }
                                       
                    currentElement = i;
                    update(input.replace(rest,""));
		    input = rest;
                    i++;    
                    
            }
            return true;
	}
                
	public List<String> getNext(){
            	
            if (currentElement < elements.size()-1) {                
                
                List<String> options = elements.get(currentElement+1).getOptions();
                
                if (options.isEmpty()) {
                    currentElement += 1;
                    return getNext();
                } 
                else return options;
     
            } else {
                return new ArrayList<>();
            }
	}
        
        // update(int i) and buildSPARQLqueries() is where the pattern-specific magic happens
        
        public void update(String parsed) {
            // Needs to be overwritten by all concrete query patterns.
        }
        
        public void setFeatures(int from, int to, String parsed) {
            
            for (LexicalEntry entry : elements.get(from).getActiveEntries()){                      
                    for (LexicalEntry.Feature f : entry.getFeatures(parsed)) {
                         elements.get(to).addFeature(f);
                    }
                }
        }

	public Set<String> buildSPARQLqueries() {
            // Needs to be overwritten by all concrete query patterns.
            
            return queries;
        }

	public void checkHowMany(String s){
            
            // TODO language-dependent list of strings somewhere?
            if(s.equals("how many")){
                count=true;
            }
                
            
        }
}
