package interQA.lexicon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author cunger
 */
public class LexicalEntry {
    
    
    public enum Language { EN, DE, ES };

    public enum SemArg  { SUBJOFPROP, OBJOFPROP };
    public enum SynArg  { SUBJECT, OBJECT };
    public enum POS     { NOUN, VERB, ADJECTIVE, PREPOSITION };
    public enum Feature { SINGULAR, PLURAL, PRESENT, PAST, COMPARATIVE, SUPERLATIVE };
    
    
    String canonicalForm;     
    String reference; 
    
    POS    pos;
    String frame;
    
    HashMap<Feature,String> forms;
    HashMap<String,List<Feature>> features;
    
    HashMap<SynArg,SemArg> argumentMapping; 
    
    
    public LexicalEntry() {

        forms = new HashMap<>();
        features = new HashMap<>();
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
    
    public void addForm(Feature f, String form) {
        forms.put(f,form);
        if (!features.containsKey(form)) features.put(form,new ArrayList<>()); 
        features.get(form).add(f);
    }
    
    public void addArgumentMapping(SynArg syn, SemArg sem) {
        argumentMapping.put(syn,sem);
    }   
    
    public String getCanonicalForm() {
        return canonicalForm;
    }

    public String getForm(Feature f) {
        return forms.get(f);
    }
    
    public List<Feature> getFeatures(String f) {
        return features.get(f);
    } 
    
    public HashMap<String,List<Feature>> getFeatures() {
        return features;
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(this.canonicalForm);
        hash = 43 * hash + Objects.hashCode(this.reference);
        hash = 43 * hash + Objects.hashCode(this.pos);
        hash = 43 * hash + Objects.hashCode(this.frame);
        hash = 43 * hash + Objects.hashCode(this.forms);
        hash = 43 * hash + Objects.hashCode(this.argumentMapping);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LexicalEntry other = (LexicalEntry) obj;
        if (!Objects.equals(this.canonicalForm, other.canonicalForm)) {
            return false;
        }
        if (!Objects.equals(this.reference, other.reference)) {
            return false;
        }
        if (this.pos != other.pos) {
            return false;
        }
        if (!Objects.equals(this.frame, other.frame)) {
            return false;
        }
        if (!Objects.equals(this.forms, other.forms)) {
            return false;
        }
        if (!Objects.equals(this.argumentMapping, other.argumentMapping)) {
            return false;
        }
        return true;
    }
 
    
}
