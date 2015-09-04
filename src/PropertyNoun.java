import java.util.*;

import de.citec.sc.matoll.core.LexicalEntry;
import de.citec.sc.matoll.core.SyntacticArgument;
import de.citec.sc.matoll.core.SyntacticBehaviour;
import de.citec.sc.matoll.io.LexiconLoader;

/**
 * Manages the property name, prepositions, and a map of Strings to a list of Lexical entries
 */
public class PropertyNoun implements ParsableElement{

	HashMap<String,List<LexicalEntry>> map= new HashMap<String,List<LexicalEntry>>();
	    // "movies with" --> LexEntry (dbpedia:starring)
	    // "movies by"   --> LexEntry (dbpedia:producer)

    String lang = "en";  //lang of the lexicon
	String property;
	StringElement preposition;
	InstanceElement instance;

	/**
	 * Constructor for an empty PN
	 */
	public PropertyNoun(){
		map = new HashMap<String,List<LexicalEntry>>();
	}

	/**
	 * Constructor from a set of lexical entries
	 * @param lexEntries
	 */
	public PropertyNoun(List<LexicalEntry> lexEntries) {
		this(); //Call default constructor
		for (LexicalEntry entry : lexEntries){
			for (HashSet<SyntacticBehaviour> syns : entry.getSenseBehaviours().values()) {
				for (SyntacticBehaviour syn : syns ) {
					//System.out.println(syn);
					if (syn.getFrame().equals("http://www.lexinfo.net/ontology/2.0/lexinfo#NounPPFrame")) {
						for (SyntacticArgument arg : syn.getSynArgs()) {
							if (arg.getPreposition() != null){
								this.addParseableString(entry.getCanonicalForm() +
												        " " +
												        arg.getPreposition(),
										          entry);
							}
						}

					}
				}
			}
		}
	}

	/**
	 * Constructor from lexicon file. Uses the constructor PropertyNoun(List<LexicalEntry> lexEntries)
	 * @param fileName
	 */
	public PropertyNoun(String fileName){
       this(new LexiconLoader().loadFromFile(fileName).getEntries());
	}

	/**
	 * Method used by constructor PropertyNoun(List<LexicalEntry> lexEntries)
	 * @param str
	 * @param entry
	 */
	private void addParseableString(String str, LexicalEntry entry) {
        QueryPatternCommons.addParseableString(map, str, entry);
	}

	/**
	 * If is can eat the starting of string, it will return the rest. If not, will return null
	 * @param string
	 * @return
	 */
	public String parse(String string) {
		for (String str : lookahead(null)){
			if (string.startsWith(str)){
				return string.substring(str.length(), string.length());
			}
		}
		return null;
	}

	/**
	 * Current it does NOTHING with selections. It returns the canonicalForms in the PropertyNoun
	 * E.g "movies with", "movies by"
	 * @return
	 */
	public List<String> lookahead(List<String> selections)
	{
		return QueryPatternCommons.removeLang(new ArrayList(map.keySet()),
				lang);
	}


	/**
	 * A canonicalPlus form (e.g. "movie by") can have several Lexical Entries. If the text is not found it returns null.
	 * @param canonicalPlusForm
	 * @return
	 */
	List<LexicalEntry> getLexicalEntry(String canonicalPlusForm, String lang){
		String plusLang = QueryPatternCommons.addLang(canonicalPlusForm, lang);
		List<LexicalEntry> list = null;
		if (map.containsKey(plusLang)){
			list = map.get(plusLang);
		}
		return list;
	}
	/**
	 * For a given string (canonicalForm) returns its instances associated.
	 * If the canonicalForm is null it returns all the instances.
	 * @param canonicalForm
	 * @return
	 */
	public List<String> getInstances() {
//		if (map.containsKey(canonicalForm)){
//			List<LexicalEntry> le = map.get(canonicalForm);
//			for (LexicalEntry e : le){
//				e.
//			}
//		}

		ArrayList<String> list = new ArrayList<String>();
		if (property == null){
			list.add("Tarantino");
			list.add("Almod�var");
			list.add("John Travolta");
			list.add("Robert De Niro");
		}else { //If we provide a canonicalForm
			if (property.equals("movie by")) {
				list.add("Tarantino");
				list.add("Almod�var");
			} else {
				if (property.equals("movie with")) {
					list.add("John Travolta");
					list.add("Robert De Niro");
				} else {
					return null;
				}
			}
		}
		return list;
	}


	/**
	 * Returns the ontology properties of the lexical entries
	 * @return
	 */
	List<String> getProperties()
	{
		return null;
	}

	public void setInstances(InstanceElement instance) {
		instance = instance;
		
	}

	public void setPreposition(StringElement element) {
		// TODO Auto-generated method stub
		
	}

}
