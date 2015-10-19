package interQA.elements;

import interQA.lexicon.LexicalEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class ParsableElement {

    Map<String,List<LexicalEntry>> index = new HashMap<>(); 
    // usually set during initialization (based on lexicon)
        
    List<LexicalEntry> activeEntries = new ArrayList<>(); 
    // usually set during parsing

    
    public List<LexicalEntry> getActiveEntries() {
        
        return activeEntries;
    }   
    
    public void addToIndex(Map<String,List<LexicalEntry>> map) {
        
        this.index.putAll(map); // TODO need to make sure values are merged when keys overlap?
    }
    
    public String parse(String string) {
        // consumes prefix of input string and
	// adds all possible lexical entries to the list above
                
        String longestMatch = "";
            
	for (String form : index.keySet()) {
            if (string.startsWith(form) && form.length() > longestMatch.length()) {
                longestMatch = form;
            }
        }
                
        if (!longestMatch.isEmpty()) {
            activeEntries = index.get(longestMatch);
            return string.replaceFirst(longestMatch,"").trim();
        }                
	return null;
    }
    
    public List<String> getOptions() {
        // Needs to be overwritten by concrete elements.
            
        return null;
    }

    public List<String> getInstances() {
        // Needs to be overwritten by concrete elements.
            
        return null;
    }
        
}
