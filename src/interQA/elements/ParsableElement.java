package interQA.elements;

import interQA.lexicon.LexicalEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class ParsableElement {

    Map<String,List<LexicalEntry>> index; 
    // = all active entries
    // Usually set during initialization (based on lexicon)
    // and then reduced during parsing.

    
    public Map<String,List<LexicalEntry>> getIndex() {        
        return index;
    }  
    
    public List<LexicalEntry> getActiveEntries() {
        
        List<LexicalEntry> active = new ArrayList<>();
        
        for (String form : index.keySet()) {
             active.addAll(index.get(form));
        }
        
        return active;
    }
    
    public void setIndex(Map<String,List<LexicalEntry>> index) {
        this.index = index;
    }
    
    public void addToIndex(Map<String,List<LexicalEntry>> map) {        
        this.index.putAll(map); 
        // TODO need to make sure values are merged when keys overlap?
    }
    
    public String parse(String string) {
        // Consumes prefix of input string and
	// keeps only the longest match in index.
                
        String longestMatch = "";
            
	for (String form : index.keySet()) {
            if (string.startsWith(form) && form.length() > longestMatch.length()) {
                longestMatch = form;
            }
        }
                
        if (!longestMatch.isEmpty()) {
            
            List<LexicalEntry> entries = index.get(longestMatch);
            index = new HashMap<>();
            index.put(longestMatch,entries);
            
            return string.replaceFirst(longestMatch,"").trim();
        }                
	return null;
    }
    
    public List<String> getOptions() {
            
        List<String> options = new ArrayList<>();
        
        options.addAll(index.keySet());
        // TODO Filter and order them?
        
        return options;     
    }

}
