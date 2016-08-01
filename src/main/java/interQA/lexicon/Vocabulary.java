package interQA.lexicon;

/**
 *
 * @author cunger
 */
public class Vocabulary {
    
    
    public String rdfType = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    public String rdfs    = "http://www.w3.org/2000/01/rdf-schema#";
    public String lemon   = "http://lemon-model.net/lemon#";
    public String lexinfo = "http://www.lexinfo.net/ontology/2.0/lexinfo#";
    public String xmls 	  = "http://www.w3.org/2001/XMLSchema#";
    
    
    public String TransitiveFrame = lexinfo + "TransitiveFrame";
    public String IntransitivePPFrame = lexinfo + "IntransitivePPFrame";
    public String NounPPFrame = lexinfo + "NounPPFrame";
    public String NounPossessiveFrame = lexinfo + "NounPossessiveFrame";
    public String AdjectivePPFrame = lexinfo + "AdjectivePPFrame";
    public String PrepositionalFrame = lexinfo + "PrepositionalFrame";
    
    
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
