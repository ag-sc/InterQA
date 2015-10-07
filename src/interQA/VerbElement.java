package interQA;

import java.util.List;

import de.citec.sc.matoll.core.LexicalEntry;

public class VerbElement implements ParsableElement {

	Lexicon lexicon;
	
	List<LexicalEntry> entries;
	
	String type;
	
	String argument; // essentially subject, object etc.
	
	public VerbElement(Lexicon lexicon, String type)
	{
		this.lexicon = lexicon;
		this.type = type;
	}
	
	@Override
	public String parse(String string) {
		// get all verbs from lexicon
		// extend lexicon by a method that return all lexical entries of a particular type
		// add all possible lexical entries to the list above
		
		String rest = null;
		
		List<String> lemmas = lexicon.getLemmasByType(type);
		
		for (String lemma: lemmas)
		{
			if (true)
			{
				return rest;
			}
		}
		
		return string;

	}

	@Override
	public List<String> lookahead(List<String> selections) {
		
		return lexicon.getLemmasByType("http://www.lexinfo.net/ontology/2.0/lexinfo#TransitiveFrame");
	}

	@Override
	public List<String> getInstances() {
		// do something really crazy
		// dobj 
		// SPARQL repository
		return null;
	}

}
