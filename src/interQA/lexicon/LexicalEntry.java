package interQA.lexicon;

import java.util.HashMap;

/**
 *
 * @author cunger
 */
public class LexicalEntry {
    
    
    public enum SemArg { SUBJOFPROP, OBJOFPROP };
    public enum SynArg { SUBJECT, DIRECTOBJECT, PREPOSITIONALOBJECT };
    
    
    String canonicalForm;     
    String reference; 
    
    String frame;
    
    HashMap<SynArg,SemArg> argumentMapping; 
    HashMap<SynArg,String> markers;
    
    
    public LexicalEntry() {
        argumentMapping = new HashMap<>();
    }
    
    
    public void setCanonicalForm(String form) {
        canonicalForm = form;
    }
    
    public void setReference(String uri) {
        reference = uri;
    }
    
    public void setFrame(String uri) {
        frame = uri;
    }
    
    public void addArgumentMapping(SynArg syn, SemArg sem) {
        argumentMapping.put(syn,sem);
    }
    
    public void addMarker(SynArg syn, String marker) {
        markers.put(syn,marker);
    }
    
    
    public String getCanonicalForm() {
        return canonicalForm;
    }
    
    public String getReference() {
        return reference;
    }
    
    public String getFrame() {
        return frame;
    }
    
    public SemArg getSemArg(SynArg syn) {
        return argumentMapping.get(syn);
    }
    
}
