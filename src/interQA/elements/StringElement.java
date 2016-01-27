package interQA.elements;

import interQA.lexicon.LexicalEntry;
import interQA.lexicon.LexicalEntry.Feature;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


public class StringElement extends ParsableElement {

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
        
        public void add(String token,Feature f) {
            elements.add(token);
            if (!featureMap.containsKey(token)) featureMap.put(token,new ArrayList<>());
            featureMap.get(token).add(f);
	}
        
        public void transferFeatures(ParsableElement e,String s) {

            if (featureMap.containsKey(s)) {
                for (LexicalEntry.Feature f : featureMap.get(s)) {
                     e.addFeature(f);
                }
            }
        }

        @Override
        public String parse(String string) {
            // consumes prefix of input string

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

            if  (features.isEmpty()) options.addAll(elements);
            else {
                for (String e : elements) {
                     if (featureMap.containsKey(e)) {
                         boolean include = true;
                         if (!compatible(featureMap.get(e),features)) {
                                  include = false;
                                  break;
                         }
                         if (include) options.add(e);
                     }
                }
            }
            
            return options;
	}

}
