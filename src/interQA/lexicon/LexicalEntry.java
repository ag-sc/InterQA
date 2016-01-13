package interQA.lexicon;

import java.util.HashMap;

/**
 *
 * @author cunger
 */
public class LexicalEntry {
    
    
    public enum SemArg  { SUBJOFPROP, OBJOFPROP };
    public enum SynArg  { SUBJECT, COPULATIVEARG, DIRECTOBJECT, PREPOSITIONALOBJECT, POSSESSIVEADJUNCT, PREPOSITIONALADJUNCT, ATTRIBUTIVEARG };
    public enum POS     { NOUN, VERB, ADJECTIVE, PREPOSITION };
    public enum Feature { SINGULAR, PLURAL, PRESENT, PAST, COMPARATIVE, SUPERLATIVE };
    
    String canonicalForm;     
    String reference; 
    
    POS    pos;
    String frame;
    
    HashMap<Feature,String> forms;
    HashMap<SynArg,SemArg>  argumentMapping; 
    HashMap<SynArg,String>  markers;
    
    
    public LexicalEntry() {

        forms = new HashMap<>();
        argumentMapping = new HashMap<>();
        markers = new HashMap<>();
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
    
    public void addForm(Feature f, String form) {
        forms.put(f,form);
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
    
    public String getForm(Feature f) {
        return forms.get(f);
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
