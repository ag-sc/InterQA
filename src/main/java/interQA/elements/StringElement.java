package interQA.elements;

import interQA.lexicon.LexicalEntry.Feature;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;


public class StringElement extends Element {

    
	List<String> elements;
        
        HashMap<String,List<Feature>> featureMap;

        
        public StringElement() {
            
            elements    = new ArrayList<>();
            agrFeatures = new ArrayList<>();
            featureMap  = new HashMap<>();
        }
        
        @Override
        public boolean isStringElement() {
            return true;
        }

	public void add(String token) {
            elements.add(token);
	}
        
        public void add(String token, Feature f) {
            add(token);
            registerFeature(token,f);
	}
        
        public void add(String token,Feature f1, Feature f2) {
            add(token);
            registerFeature(token,f1);
            registerFeature(token,f2);
        }
        
        public void add(String token,Feature f1, Feature f2, Feature f3) {
            add(token);
            registerFeature(token,f1);
            registerFeature(token,f2);
            registerFeature(token,f3);
        }
        
        public Map<String,List<Feature>> getFeatureMap() {
            return featureMap;
        }
        
        private void registerFeature(String token, Feature f) {
            if (!featureMap.containsKey(token)) featureMap.put(token,new ArrayList<>());
            featureMap.get(token).add(f);
        }

        @Override
        public String parse(String string) {
            // consumes prefix of input string

            if (elements.isEmpty()) return string;
           
            String longestMatch = "";

            for (String form : elements) {
                if (string.startsWith(form) && form.length() > longestMatch.length()) {
                    longestMatch = form;
                    
                }
            }

            if (!longestMatch.isEmpty()) {
                return StringUtils.replaceOnce(string,longestMatch,"").trim();
            } 
            
            return null;
        }

        @Override
	public List<String> getOptions() {
            
            List<String> options = new ArrayList<>();
            
            if (agrFeatures.isEmpty()) { 
                options.addAll(elements);
            }
            else {
                for (String e : elements) {
                     if (!featureMap.containsKey(e) || compatible(featureMap.get(e),agrFeatures)) {
                         options.add(e);
                     } 
                }
            }
            
            return options;
	}

        @Override
        public StringElement clone() {
            
        StringElement clone = new StringElement();
        
        clone.elements.addAll(elements);
        for (String k : featureMap.keySet()) {
             clone.featureMap.put(k,featureMap.get(k));
        }
        for (Feature f : agrFeatures) {
             clone.agrFeatures.add(f);
        }   
    
        return clone;
    }
}
