package interQA.elements;

import interQA.lexicon.LexicalEntry;
import interQA.lexicon.LexicalEntry.Feature;
import interQA.lexicon.Lexicon;
import interQA.lexicon.Vocabulary;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.jena.graph.Triple;


public abstract class Element implements Cloneable {

    
    // Active entries
    Map<String,List<LexicalEntry>> index; 
    // Usually set during initialization (based on lexicon)
    // and then reduced during parsing.
    
    // Grammatical features (number, gender, etc.)  needed for morphological agreement
    List<Feature> agrFeatures;

    // Context to keep track of which instantiation can fill which query
    Map<LexicalEntry,List<Triple>> context;

        
    public boolean isStringElement() {
        return false;
    }

    public void addEntries(Lexicon lexicon, LexicalEntry.POS pos, String frame) {
        
        addEntries(lexicon,pos,frame,false);
    }
    public void addEntries(Lexicon lexicon, LexicalEntry.POS pos, String frame, boolean withMarker) {
        
        HashMap<String,List<LexicalEntry>> subindex = lexicon.getSubindex(pos,frame,withMarker);
        for (String s : subindex.keySet()) {
             if (!this.index.containsKey(s)) {
                  this.index.put(s,new ArrayList<>());
             }
             this.index.get(s).addAll(subindex.get(s));
        }
    }

    public Map<LexicalEntry,List<Triple>> getContext() {
        return context;
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

    public List<String> getActiveEntriesKey() {
        
        List<String> active = new ArrayList<>();
        
        for (String form : index.keySet()) {
             active.add(form);
        }
        
        return active;
    }
    
    public List<String> getMarkers() {
        
        List<String> markers = new ArrayList<>(); 
        
        for (String k : index.keySet()) {
            for (LexicalEntry entry : index.get(k)) {
                if (entry.getMarker() != null) {
                     markers.add(entry.getMarker());
                }
            }
        }
        
        return markers;
    }
    
    public void addCopula(StringElement element) {
        
        boolean hasRelationalAdjective = false;
        check: for (String form : index.keySet()) {
          for (LexicalEntry entry : index.get(form)) {
               if (entry.getFrame() != null && entry.getFrame().equals(Vocabulary.AdjectivePPFrame)) {
                   hasRelationalAdjective = true;
                   break check;
               }
          }
        }
        if (hasRelationalAdjective) {
            element.add("is",Feature.PRESENT,Feature.SINGULAR);
            element.add("are",Feature.PRESENT,Feature.PLURAL);
            element.add("was",Feature.PAST,Feature.SINGULAR);
            element.add("were",Feature.PAST,Feature.PLURAL);
        }
    }
    
    public List<Feature> getAgrFeatures() {
        
        return agrFeatures;
    }
    
    public void setIndex(Map<String,List<LexicalEntry>> index) {
        this.index = index;
    }
    
    public void addToIndex(String form, LexicalEntry entry) {
        
        if (!index.containsKey(form)) {
             index.put(form,new ArrayList<>());
        }
        index.get(form).add(entry);
    }
    
    public void addToIndex(Map<String,List<LexicalEntry>> map) {        
        
        this.index.putAll(map); 
        // TODO need to make sure values are merged when keys overlap?
    }
    
    public void removeFromIndex(LexicalEntry entry) {
        
        Set<String> keys = new HashSet<>();
        
        for (String k : index.keySet()) {
            if (index.get(k).contains(entry)) {
                keys.add(k);
            }
        }
        
        for (String k : keys) {
            index.get(k).remove(entry);
            if (index.get(k).isEmpty()) {
                index.remove(k);
            }
        }
    }
    
    public void addAgrFeature(Feature f) {
        
        this.agrFeatures.add(f);
    }
    
    public String parse(String string) {
        // Consumes prefix of input string and
	// keeps only the longest match in index.
        
        if (this.isStringElement() && this.index.isEmpty()) {
            return string;
        }
                
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
                                
        if (agrFeatures.isEmpty()) options.addAll(index.keySet());
        else {
            for (String key : index.keySet()) {
                boolean include = true;
                for (LexicalEntry entry : index.get(key)) {
                    if (!compatible(entry.getFeatures(key),agrFeatures)) {
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
    
    @Override
    public Element clone() {
        return null;
    }

}
