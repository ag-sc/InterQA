package interQA;

import de.citec.sc.matoll.core.LexicalEntry;
import de.citec.sc.matoll.core.Reference;

import java.util.*;


public class QueryPattern1_4 implements QAPattern {

	// Which/What is the capital of England?
	// Which/What are the movies by Tarantino?
	// Which/What are the movies with Travolta?
	
	List<ParsableElement> elements = new ArrayList<ParsableElement>();
    List<String> selections = new ArrayList<String>(); //List of selected elements
	private int lastElementParsed = -1; //The first element parseable is 0
	
	public QueryPattern1_4(Lexicon lexicon)
	{
		StringElement element0 = new StringElement();      //Element 0
		element0.add("who");
		element0.add("what");
		elements.add(element0);
		
		VerbElement element1 = new VerbElement(lexicon,"http://www.lexinfo.net/ontology/2.0/lexinfo#TransitiveFrame");      //Element 1
		elements.add(element1);
		
		InstanceElement element2 = new InstanceElement(element1);      //Element 2
		elements.add(element2);


	}

	/**
	 * For the given canonicalPlusForm (e.g. "movie with") obtains its property and, depending on the
	 * "direction" of the property (linear or  reverse) get the objects (case for linear) or the subjects (case
	 * for reverse) of the triples in the EP.
	 * @param EP
	 * @param canonicalPlusForm
	 * @return
	 */
	public static List<String> getInstancesForCanonicalPlusForm (String EP, String canonicalPlusForm){
		//		   String canonicalPlusForm = "movie by";
		//		   List<LexicalEntry> entries = element3pn.getLexicalEntry(canonicalPlusForm);
		//		   if (entries != null){ //There are entries for that string (canonicalPlusForm)
		//			   for (LexicalEntry entry : entries){
		//				   for (HashSet<SyntacticBehaviour> syns : entry.getSenseBehaviours().values()) {
		//					   for (SyntacticBehaviour syn : syns ) {
		//						   //System.out.println(syn);
		//						   if (syn.getFrame().equals("http://www.lexinfo.net/ontology/2.0/lexinfo#NounPPFrame")) {
		//							   for (SyntacticArgument arg : syn.getSynArgs()) {
		//								   if (arg.getPreposition() != null){
		//									   this.addParseableString(entry.getCanonicalForm() +
		//													   " " +
		//													   arg.getPreposition(),
		//											   entry);
		//								   }
		//							   }
		//
		//						   }
		//					   }
		//				   }
		//		   }
		//   String p = entry.getProperty(); //This method does not exists!!
		//   String dir = entry.getDir();    // This method does not exists!!
		//   List<String> res = queryInstances(EP, p, dir);     // This method does not exists!!
		//   // if (dir == reverse) ==> res is a list of URIs
		//   // if (dir == linear)  ==> res is a list of typed values (e.g. integers) or
		//                                     a list of Strings or
		//                                     a list of URIs (resources)
		//   List<String> texts = getReadableForm(res);  // This method does not exists!!
		//   return texts;
		// }
		String prop = null;
		String dir  = null;
		switch (canonicalPlusForm){
			case "movie by":
				prop = "producer";
				dir = "linear";
				break;
			case "movie with":
				prop = "starring";
				dir = "linear";
				break;
			case "capital of":
				prop = "capital";
				dir = "reverse";
				break;
		}
		//Make the query to get the instances
		String queryLinear =  "select ?v where{"+
	                          "?s " + prop + "?v" +
							  "}"; //values of triples with that property, I do not mind subjects
		String queryReverse = "select ?s where{"+
				              "?s " + prop + "?v" +
				              "}"; //subjects of triples with that property, I do not mind objects
		List<String> res = null;
		//res =  query over the EP with queryLinear
		//Temporal... meanwhile matool evolves
		res = new ArrayList<>();
		if (dir.equals("linear")){
			switch (canonicalPlusForm){
				case "movie by":
					res.add("http://dbpedia.org/resource/Tarantino");
					res.add("http://dbpedia.org/resource/Almodovar");
					break;
				case "movie with":
					res.add("http://dbpedia.org/resource/John_Travolta");
					res.add("http://dbpedia.org/resource/Robert_De_Niro");
					break;
			}
		}else{ //Assumes reverse
			switch (canonicalPlusForm) {
				case "capital of":
					res.add("http://dbpedia.org/resource/England");
					res.add("http://dbpedia.org/resource/Spain");
					break;
			}
		}
		return res;
	}

	/**
	 * @param input The whole string from the UI E.g."who is the"
	 * @return if the input string matches the querypattern
	 */
	public boolean parses(String input) {
		selections.clear(); //Reset selections already done
		int i = 0;
		while(!input.equals("")){                 //An element can eat more that a word
			String rest = elements.get(i).parse(input); //Feed with the rest
			String eated = input.substring(0, input.length() - rest.length());
			selections.add(eated); //Asign the selections
			input = rest;
			i++;
            if (input == null){ //The element cannot eat anything
				return false;
			}
		}
		return true;

	}

	/**
	 * Get the possible values in the current state of parsing
	 * @return A list of strings. Initially to be displayed to the user
	 */
	public List<String> getNext(){
		int currentElem = ++lastElementParsed;
//		switch (elements.get(currentElem).getClass().toString()){
//			case "class interQA.PropertyNoun": {  //Case for propertyNoun
//				       return elements.get(currentElem).lookahead(selections);
//			        }
//			default:{
//				       //List<String> res= elements.get(lastElementParsed).lookahead();
//				       List<String> res= elements.get(currentElem).lookahead(selections);
//				       //lastElementParsed++;
//				       return res;
//			        }
//		}

		List<String> res= elements.get(currentElem).lookahead(selections);
		return res;
	}

	public String getSPARQLQuery(){
		int positionPN = 3;
		int positionInstance = 4;
		PropertyNoun element3pn = (PropertyNoun)elements.get(positionPN);
		InstanceElement element4inst = (InstanceElement)elements.get(positionInstance);

		String selectedCannonicalPlus = selections.get(positionPN);
		String selectedInstance = selections.get(positionInstance);

		List<LexicalEntry> entries = element3pn.getLexicalEntry(selectedCannonicalPlus, "en");
		//Probably size(entries) will be always 1
		LexicalEntry entry = entries.get(0); //The first entry
		Set<Reference> refs = entry.getReferences();
		Iterator iterator = refs.iterator();
		String prop = "";
		while(iterator.hasNext()){ //Probably it will be just one
			Reference ref = (Reference) iterator.next();
			prop = ref.getURI();
		}
		String query = "select ?s where {?s " + prop + " " + selectedInstance + "}";
		return query;
	}
}
