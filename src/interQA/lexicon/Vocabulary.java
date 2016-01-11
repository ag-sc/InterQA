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
    public String xmls 	  =	"http://www.w3.org/2001/XMLSchema#";
    
    
    public String TransitiveFrame;
    public String IntransitivePPFrame;
    public String NounPPFrame;
    public String NounPossessiveFrame;
    public String AdjectivePPFrame;
    
    public Vocabulary() {

        TransitiveFrame     = "http://www.lexinfo.net/ontology/2.0/lexinfo#TransitiveFrame";
        IntransitivePPFrame = "http://www.lexinfo.net/ontology/2.0/lexinfo#IntransitivePPFrame";
        NounPPFrame         = "http://www.lexinfo.net/ontology/2.0/lexinfo#NounPPFrame";
        NounPossessiveFrame = "http://www.lexinfo.net/ontology/2.0/lexinfo#NounPossessiveFrame";
        AdjectivePPFrame    = "http://www.lexinfo.net/ontology/2.0/lexinfo#AdjectivePPFrame";        
    }
}
