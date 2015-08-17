/**
 * Created by Mariano on 13/07/2015.
 */
import java.util.*;

import de.citec.sc.matoll.core.LexicalEntry;
import de.citec.sc.matoll.core.SyntacticArgument;
import de.citec.sc.matoll.core.SyntacticBehaviour;
import de.citec.sc.matoll.io.LexiconLoader;

/**
 * Manages the class name, and a map of Strings to a list of Lexical entries
 */
public class ClassNoun implements ParsableElement {

    HashMap<String, List<LexicalEntry>> map = new HashMap<String, List<LexicalEntry>>();
    // "movie" --> LexEntry (dbpedia:Film)
    //         --> LexEntry (dbpedia:Work)
    // "country" -->LexEntry(dbpedia:Country)
    //           -->LexEntry(dbpedia:Place)
    //           -->LexEntry(schema:Country)

    String lang = "en";  //lang of the lexicon
    String property;
    StringElement preposition;
    InstanceElement instance;

    /**
     * Constructor for an empty PN
     */
    public ClassNoun() {
        map = new HashMap<String, List<LexicalEntry>>();
    }

    /**
     * Constructor from a set of lexical entries
     *
     * @param lexEntries
     */
    public ClassNoun(List<LexicalEntry> lexEntries) {
        this(); //Call default constructor
//        for (LexicalEntry entry : lexEntries) {
//            for (HashSet<SyntacticBehaviour> syns : entry.getSenseBehaviours().values()) {
//                for (SyntacticBehaviour syn : syns) {
//                    //System.out.println(syn);
//                    if (syn.getFrame().equals("http://www.lexinfo.net/ontology/2.0/lexinfo#NounPPFrame")) {
//                        for (SyntacticArgument arg : syn.getSynArgs()) {
//                            if (arg.getPreposition() != null) {
//                                this.addParseableString(entry.getCanonicalForm() +
//                                                " " +
//                                                arg.getPreposition(),
//                                        entry);
//                            }
//                        }
//
//                    }
//                }
//            }
//        }
        // "movie" --> LexEntry (dbpedia:Film)
        //         --> LexEntry (dbpedia:Work)
        // "country" -->LexEntry(dbpedia:Country)
        //           -->LexEntry(dbpedia:Place)
        //           -->LexEntry(schema:Country)

    }

    /**
     * Constructor from lexicon file. Uses the constructor ClassNoun(List<LexicalEntry> lexEntries)
     * @param fileName
     */
    public ClassNoun(String fileName){
        //this(new LexiconLoader().loadFromFile(fileName).getEntries());
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
     * Current it does NOTHING with selections. It returns the canonicalForms in the PropertyNoun
     * E.g "movies with", "movies by"
     * @return
     */
    public List<String> lookahead(List<String> selections)
    {
        return QueryPatternCommons.removeLang(new ArrayList(map.keySet()),
                lang);
    }
}