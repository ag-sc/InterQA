package interQA.lexicon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author cunger
 */
public class LexicalEntry {
    
    
    public enum SemArg { SUBJOFPROP, OBJOFPROP };
    public enum SynArg { SUBJECT, COPULATIVEARG, DIRECTOBJECT, PREPOSITIONALOBJECT };
    public enum POS    { NOUN, VERB, ADJECTIVE };
    
    
    String lemonURI;
    String wnURI;
       
    String canonicalForm;     
    String reference; 
    POS    pos;
    String frame;
    
    HashMap<SynArg,SemArg> argumentMapping; 
    HashMap<SynArg,String> markers;
    
    
    public LexicalEntry(String uri) {
        
        lemonURI = uri;
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
    
    public void setPOS(POS pos) {
        this.pos = pos;
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
    
    public POS getPOS() {
        return pos;
    }
    
    public SemArg getSemArg(SynArg syn) {
        return argumentMapping.get(syn);
    }
    
}
