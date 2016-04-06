package interQA.elements;

import interQA.lexicon.LexicalEntry;
import interQA.lexicon.LexicalEntry.Feature;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


public class StringElement extends Element {

	List<String> elements;
        HashMap<String,List<Feature>> featureMap;

        public StringElement() {
            
            elements   = new ArrayList<>();
            features   = new ArrayList<>();
            featureMap = new HashMap<>();
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
        
        private void registerFeature(String token, Feature f) {
            if (!featureMap.containsKey(token)) featureMap.put(token,new ArrayList<>());
            featureMap.get(token).add(f);
        }
        
        public void transferFeatures(Element e,String s) {

            if (featureMap.containsKey(s)) {
                for (LexicalEntry.Feature f : featureMap.get(s)) {
                     e.addFeature(f);
                }
            }
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
            
            if (features.isEmpty()) { 
                options.addAll(elements);
            }
            else {
                for (String e : elements) {
                     if (!featureMap.containsKey(e) || compatible(featureMap.get(e),features)) {
                         options.add(e);
                     } 
                }
            }
            
            return options;
	}

}
