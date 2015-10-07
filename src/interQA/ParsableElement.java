package interQA;

import java.util.List;

public interface ParsableElement {
	/**
	 * Eats all it can. Returns the rest (string to the right side of the eated string)
	 * @param string
	 * @return If it cannot parses anything it returns null.
	 */
	String parse(String string);

	/**
	 * Despite its name, it returns the possible values of the element
	 * @param selections is the list of selections done previously
	 * @return possible values of the element
	 */
	List<String> lookahead(List<String>selections);

	List<String> getInstances();
}
