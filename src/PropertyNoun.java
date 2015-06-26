import java.util.HashMap;
import java.util.List;


public class PropertyNoun implements ParsableElement{

	HashMap<String,List<LexicalEntry>> map;
	
	String property;
	
	String prepositions;

	public boolean matches(String token) {
		// TODO Auto-generated method stub
		return false;
	}
	
	List<String> lookahead()
	{
		return null;
	}
	
	List<String> getPrepositions()
	{
		return null;
	}

	public void setInstances(InstanceElement instance) {
		// TODO Auto-generated method stub
		
	}

	public void setPreposition(StringElement element) {
		// TODO Auto-generated method stub
		
	}
}
