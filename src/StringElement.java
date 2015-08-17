import java.util.ArrayList;
import java.util.List;

/**
 * StringElement is a valid set of strings. You can add strings, check is a string is valid....
 */
public class StringElement implements ParsableElement {

	List<String> elements = new ArrayList<String>();
	
	
	public boolean matches(String string) {

		for (String element: elements) {
			if (string.equals(element)) {
				return true;
			}
		}
		return false;
	}


	public void add(String token) {
		elements.add(token);
	}

	/**
	 * Eats as much as it can from string (eating left to right). Checks all the valid starts and selects the bigger.
	 * @param string
	 * @return the rest (right side) of the string
	 */
	public String parse(String string) {
		List<String> candidates = new ArrayList<>();
		String res;
		for (String element: elements) {
			if (string.startsWith(element)) { //if we can eat it
				candidates.add(string.substring(element.length())); //Store the rest
			}
		}
		if (candidates.size() > 0){
			//Choose the one that produces a smaller rest
			int smallerLength = candidates.get(0).length(); //Start with any (e.g. the first one)
			int smallerIdx = 0;
			int counter = 0;
			for (String ca: candidates){
				if (ca.length() < smallerLength){
					smallerLength = ca.length();
					smallerIdx = counter;
				}
				counter++;
			}
			res = candidates.get(smallerIdx);
		}else {
			res = null;
		}
		return res;
	}


	public List<String> lookahead(List<String> selections) {
		return elements;
	}
}
