import java.util.List;


public class StringElement implements ParsableElement {

	List<String> elements;
	
	
	public boolean matches(String token) {
		
		for (String element: elements)
		{
			if (token.equals(element))
				return true;
		}
		return false;
	}


	public void add(String token) {
		elements.add(token);
		
	}

}
