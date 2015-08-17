import java.util.List;


public interface QAPattern {

	public boolean parses(String input);
	public List<String> getNext();
	public String getSPARQLQuery();

	
}
