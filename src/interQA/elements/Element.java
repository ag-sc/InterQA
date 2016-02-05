package interQA.elements;

import interQA.lexicon.LexicalEntry;
import interQA.lexicon.LexicalEntry.Feature;
import interQA.lexicon.Lexicon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class Element {

    Map<String,List<LexicalEntry>> index; 
    // = all active entries
    // Usually set during initialization (based on lexicon)
    // and then reduced during parsing.
    
    List<Feature> features;
    boolean optional = false;
    
    
    public void addEntries(Lexicon lexicon, LexicalEntry.POS pos, String frame) {
            
        this.index = lexicon.getSubindex(pos,frame);
    }
    
    public void setOptional() {
        optional = true;
    }
    
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
    
    public List<Feature> getFeatures() {
        return features;
    }
    
    public void setIndex(Map<String,List<LexicalEntry>> index) {
        this.index = index;
    }
    
    public void addToIndex(Map<String,List<LexicalEntry>> map) {        
        this.index.putAll(map); 
        // TODO need to make sure values are merged when keys overlap?
    }
    
    public void addFeature(Feature f) {
        this.features.add(f);
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
                
        if (features.isEmpty()) options.addAll(index.keySet());
        else {
            for (String key : index.keySet()) {
                boolean include = true;
                for (LexicalEntry entry : index.get(key)) {
                    if (!compatible(entry.getFeatures(key),features)) {
                        include = false;
                        break;
                    }
                }
                if (include) options.add(key);
            }
        }
        
        return options;     
    }
    
    public boolean compatible(List<Feature> fs1, List<Feature> fs2) {
        
        if (fs1.contains(Feature.SINGULAR)) return !fs2.contains(Feature.PLURAL)    || fs1.contains(Feature.PLURAL);
        if (fs1.contains(Feature.PLURAL))   return !fs2.contains(Feature.SINGULAR)  || fs1.contains(Feature.SINGULAR);
        if (fs1.contains(Feature.PRESENT))  return !fs2.contains(Feature.PAST)      || fs1.contains(Feature.PAST);
        if (fs1.contains(Feature.PAST))     return !fs2.contains(Feature.PRESENT)   || fs1.contains(Feature.PRESENT);
        
        return true;
    }

}
