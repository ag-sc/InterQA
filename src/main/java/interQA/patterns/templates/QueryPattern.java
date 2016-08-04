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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public abstract class QueryPattern {

        public Lexicon lexicon;
        public DatasetConnector dataset;
        
        public Vocabulary vocab = new Vocabulary();
        
        public QueryBuilder builder = new QueryBuilder();
                
        public Set<String> queries = new HashSet<>();
        
        
        public boolean count = false;        
        
	public List<Element> elements = new ArrayList<>();
	public int currentElement = -1;   
        
        public Map<Integer,Integer> agreement = new HashMap<>();

        
        public void init() {
        }
        
            
        public Element getElement(int i) {
            
            return elements.get(i);
        }
        
        public void addAgreementDependency(int from, int to) {
            
            agreement.put(from,to);
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
        
        // update(int i) is where all the pattern-specific magic happens
        
        public void update(String parsed) {
            // Needs to be overwritten by all concrete query patterns.
        }
        
        public void transferFeatures(String parsed) {
            
            for (int from : agreement.keySet()) {
            
                Element e_from = elements.get(from);
                Element e_to   = elements.get(agreement.get(from));

                Map<String,List<Feature>> feats = new HashMap<>();

                if (e_from.isStringElement()) {
                    feats.putAll(((StringElement) e_from).getFeatureMap());
                } else {
                    for (LexicalEntry entry : e_from.getActiveEntries()) {  
                         feats.putAll(entry.getFeatures());
                    }
                }

                for (String k : feats.keySet()) {
                     if (parsed.matches("(\\s|^)"+k+"\\s|$")) {
                         for (Feature f : feats.get(k)) {
                              e_to.addAgrFeature(f);
                         }
                     }
                }
            }
        }

	public Set<String> buildSPARQLqueries() {
            
            return builder.returnQueries(true);
        }

	public void checkHowMany(String s){
            
            // TODO language-dependent list of strings somewhere?
            if (s.equals("how many") || s.equals("wieviel") || s.equals("wieviele")) {
                count=true;
            }            
        }
}
