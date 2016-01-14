package interQA.elements;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


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
                return StringUtils.replaceOnce(string,longestMatch,"").trim();
            }                
            return null;
        }

        @Override
	public List<String> getOptions() {
            
            return elements;
	}

}
