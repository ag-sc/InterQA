package interQA.elements;

import java.util.ArrayList;
import java.util.List;


public class StringElement extends ParsableElement {

	List<String> elements = new ArrayList<>();


	public void add(String token) {
            elements.add(token);
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
                return string.replaceFirst(longestMatch,"").trim();
            }                
            return null;
        }

        @Override
	public List<String> getOptions() {
            
            return elements;
	}

}
