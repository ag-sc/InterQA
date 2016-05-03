package interQA.elements;

import interQA.lexicon.LexicalEntry;
import interQA.lexicon.LexicalEntry.Feature;
import interQA.lexicon.Lexicon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class Element {

    // Active entries
    Map<String,List<LexicalEntry>> index; 
    // Usually set during initialization (based on lexicon)
    // and then reduced during parsing.
    
    // Grammatical features such as singular/plural, gender, etc. (needed for morphological agreement)
    List<Feature> features;
        
    
    public void addEntries(Lexicon lexicon, LexicalEntry.POS pos, String frame) {
        this.index = lexicon.getSubindex(pos,frame,true);
    }
    public void addEntries(Lexicon lexicon, LexicalEntry.POS pos, String frame, boolean withMarker) {
        this.index = lexicon.getSubindex(pos,frame,withMarker);
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
        String input = string.toLowerCase();
            
	for (String s : index.keySet()) {
            String form = s.toLowerCase();
            if (input.startsWith(form) && form.length() > longestMatch.length()) {
                longestMatch = s;
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
        
        boolean compatible = true;
        
        if (fs1.contains(Feature.SINGULAR)  && fs2.contains(Feature.PLURAL))   compatible = false;
        if (fs1.contains(Feature.PLURAL)    && fs2.contains(Feature.SINGULAR)) compatible = false;
        if (fs1.contains(Feature.PRESENT)   && fs2.contains(Feature.PAST))     compatible = false;
        if (fs1.contains(Feature.PAST)      && fs2.contains(Feature.PRESENT))  compatible = false;
        if (fs1.contains(Feature.FEMININE)  && (fs2.contains(Feature.MASCULINE) || fs2.contains(Feature.NEUTER)))   compatible = false;
        if (fs1.contains(Feature.MASCULINE) && (fs2.contains(Feature.FEMININE)  || fs2.contains(Feature.NEUTER)))   compatible = false;
        if (fs1.contains(Feature.NEUTER)    && (fs2.contains(Feature.MASCULINE) || fs2.contains(Feature.FEMININE))) compatible = false;
        
        return compatible;
    }

}
