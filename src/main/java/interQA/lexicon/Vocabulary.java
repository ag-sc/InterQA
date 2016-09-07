package interQA.lexicon;

/**
 *
 * @author cunger
 */
public class Vocabulary {
    
    
    public static String rdfType = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    public static String rdfs    = "http://www.w3.org/2000/01/rdf-schema#";
    public static String lemon   = "http://lemon-model.net/lemon#";
    public static String lexinfo = "http://www.lexinfo.net/ontology/2.0/lexinfo#";
    public static String xmls 	  = "http://www.w3.org/2001/XMLSchema#";
    public static String xsd_gYear = "http://www.w3.org/2001/XMLSchema#gYear";
    
    public static String TransitiveFrame = lexinfo + "TransitiveFrame";
    public static String IntransitivePPFrame = lexinfo + "IntransitivePPFrame";
    public static String NounPPFrame = lexinfo + "NounPPFrame";
    public static String NounPossessiveFrame = lexinfo + "NounPossessiveFrame";
    public static String AdjectivePPFrame = lexinfo + "AdjectivePPFrame";
    public static String PrepositionalFrame = lexinfo + "PrepositionalFrame";
    
    public String sortal_predicate = rdfType;
    
    int i;
    
    public Vocabulary() {
        i = 0;
    }
    
    public void reset() {
        i = 0;
    }
    
    public String getFreshVariable() {

        i++;
        return "v"+i;
    }
}
