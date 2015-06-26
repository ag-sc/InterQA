import java.util.ArrayList;
import java.util.List;


public class QueryPattern1 implements QAPattern {

	// Which/What is the population of Bielefeld?
	
	List<ParsableElement> elements;
	
	public QueryPattern1()
	{
		elements = new ArrayList<ParsableElement>();
		
		StringElement element = new StringElement();
		element.add("which");
		element.add("what");
		element.add("who");
		
		elements.add(element);
		
		element = new StringElement();
		element.add("is");
		element.add("are");
		element.add("was");
		element.add("were");
		
		elements.add(element);
		
		element = new StringElement();
		element.add("the");
		element.add("a");
		
		elements.add(element);
		
		PropertyNoun propertyNoun = new PropertyNoun();
				
		elements.add(propertyNoun);
		
		element = new StringElement();
		
		propertyNoun.setPreposition(element);
		
		elements.add(element);
		
		InstanceElement instance = new InstanceElement();
		
		propertyNoun.setInstances(instance);
		
		elements.add(instance);
		
		
		
	}
	
	
	public List<String> parse(String input) {
		
		String[] tokens = input.split(" ");
		
		for (int i=0; i < tokens.length; i++)
		{
			if (!elements.get(i).matches(tokens[i]))
				return null;
		}
		return null;
		
	}

}
