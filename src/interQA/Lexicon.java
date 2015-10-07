package interQA;

import java.util.HashMap;
import java.util.List;
import de.citec.sc.matoll.core.LexicalEntry;


public class Lexicon {

	HashMap<String,HashMap<String,List<LexicalEntry>>> lexicon_index;
	
	
	public Lexicon(de.citec.sc.matoll.core.Lexicon lexicon)
	{
		lexicon_index = new HashMap<String,HashMap<String,List<LexicalEntry>>>();
		
		// create indices
		
	}
	
	public List<LexicalEntry> getEntriesbyType(String type)
	{
		// return all entries by type
		return null;
	}
	
	public List<String> getLemmasByType(String type)
	{
		// return all canonical Forms for a given type
		return null;
	}
	
	
}
