package interQA.patterns.templates;

import interQA.lexicon.Vocabulary;
import interQA.elements.Element;
import interQA.elements.StringElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.LexicalEntry.Feature;
import interQA.lexicon.Lexicon;
import interQA.patterns.query.QueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public abstract class QueryPattern {

        // static fiels 
        // (are filled once by the QueryPatternManager and then don't change)
    
        public Lexicon lexicon;
        public DatasetConnector dataset;
        
        public Vocabulary vocab = new Vocabulary();
        
        public QueryBuilder builder = new QueryBuilder();
        public Map<Integer,Integer> agreement = new HashMap<>();
                
        public List<Element> elements = new ArrayList<>();
        
        public boolean count = false;        

        // dynamic fields 

        public int currentElement = -1; 

        
        public void init() {
        // Should be filled by each specific QueryPattern.
        }
        
        
        /* MAIN FUNCTIONALITY */ 
        
                
        public void update(String parsed) {
            // Needs to be overwritten by all concrete query patterns.
            // This is where all the pattern-specific magic happens.
        }
        

        /**
	 * @param input The whole string from the UI (e.g."who is the")
	 * @return whether the input string matches the query pattern
	 */
	
        public boolean parses(String input) {

            currentElement = -1;
                        
            int i = 0;
            while (!input.isEmpty() && elements.size() > i) {
                   
                    String rest = elements.get(i).parse(input);
                    if (rest == null) { return false; }
                                       
                    currentElement = i;
                    String parsed = input.replace(rest,"");
                    
                    update(parsed);
                    transferFeatures(parsed);

		    input = rest;
                    i++;    
                    
            }
            return true;
	}
                
	public List<String> getNext(){
            	
            if (currentElement < elements.size()-1) {                
                
                List<String> options = elements.get(currentElement+1).getOptions();
                if (options.isEmpty() && elements.get(currentElement+1).getClass().getSimpleName().equals("StringElement") ) {
                    
                    currentElement += 1;
                    return getNext();
                } 
                else return options;
     
            } else {
                return new ArrayList<>();
            }
	}
        
	public Set<String> buildSPARQLqueries() {
            
            return builder.returnQueries(true);
        }
        
        
        /* AUXILIARY STUFF  */
        
        
        public Element getElement(int i) {
            
            return elements.get(i);
        }
        
        public void addAgreementDependency(int from, int to) {
            
            agreement.put(from,to);
        }
        
        public void transferFeatures(String parsed) {
            
            if (agreement.containsKey(currentElement)) {
            
                Element e_from = elements.get(currentElement);
                Element e_to   = elements.get(agreement.get(currentElement));

                Map<String,List<Feature>> feats = new HashMap<>();

                if (e_from.isStringElement()) {
                    feats.putAll(((StringElement) e_from).getFeatureMap());
                } else {
                    for (LexicalEntry entry : e_from.getActiveEntries()) {  
                         feats.putAll(entry.getFeatures());
                    }
                }

                for (String k : feats.keySet()) {
                    if (parsed.matches(".*(\\s|^)"+k+"(\\s|$).*")) {
                        for (Feature f : feats.get(k)) {
                             e_to.addAgrFeature(f);
                        }
                    }
                }
            }
        }

	public void checkHowMany(String s){
            
            // TODO language-dependent list of strings somewhere?
            if (s.equals("how many") || s.equals("wieviel") || s.equals("wieviele")) {
                count=true;
            }            
        }
}
