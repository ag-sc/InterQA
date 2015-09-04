import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstanceElement implements ParsableElement {
    Map<String, String> mapping;  //"Tarantino" --> "http://dbpedia.org/resourse/Quentin_Tarantino"
	
    ParsableElement element;
	
    int distance;

	/**
	 *
	 * @param pn
	 * @param distance is the number of previous parseable elements to reach the propertyNoun literal
	 */
	public InstanceElement(ParsableElement element) {
	   //this();
		mapping = new HashMap<String, String>();
	    this.element = element;
    }

	public String parse(String string) {

		for (String str : lookahead(null)){
			if (string.startsWith(str)){
				return string.substring(str.length(), string.length());
			}
		}
		return null;
	}

	/**
	 * If previousSelections is null it returns all the instances available for the PropertyNoun associated.
	 * If previousSelections is NOT null it returns the instances for the last selection
	 * @param previousSelections
	 * @return
	 */
	public List<String> lookahead(List<String> previousSelections) {

	// get all necessary info from element	
		
		//		List<String> instances = new ArrayList<String>();
//		if (previousSelections.size() > 0){
//		    switch (previousSelections.get(previousSelections.size() - distance)){ //last selection
//				case "movie by":
//					instances.add("Tarantino");
//					instances.add("Almodovar");
//					break;
//				case "movie with":
//					instances.add("John Travolva");
//					instances.add("Robert De Niro");
//					break;
//			}
//		}
			return element.getInstances();
	}

	@Override
	public List<String> getInstances() {
		
		return element.getInstances();
	}

}
