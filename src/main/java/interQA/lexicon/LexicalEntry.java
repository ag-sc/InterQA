package interQA.lexicon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author cunger
 */
public class LexicalEntry {
    
    
    public enum Language { EN, DE, ES };

    public enum SemArg  { SUBJOFPROP, OBJOFPROP };
    public enum SynArg  { SUBJECT, OBJECT };
    public enum POS     { NOUN, VERB, ADJECTIVE, PREPOSITION };
    public enum Feature { SINGULAR, PLURAL, PRESENT, PAST, FEMININE, MASCULINE, NEUTER, COMPARATIVE, SUPERLATIVE };
    
    String canonicalForm;  
    String particle;
    String marker;
    String frame;
    
    String  reference; 
    boolean literal;
    RDFNode literalNode;
    
    POS pos;

    List<Feature> inherentFeatures;
    
    HashMap<Feature,String> forms;
    HashMap<String,List<Feature>> features;
    
    HashMap<SynArg,SemArg> argumentMapping; 
    
    
    public LexicalEntry() {

        forms = new HashMap<>();
        features = new HashMap<>();
        inherentFeatures = new ArrayList<>();
        argumentMapping = new HashMap<>();
        
        literal = false; // default
    }
    
    public void setCanonicalForm(String form) {
        canonicalForm = form;
    }
    
    public void setParticle(String p) {
        particle = p;
    }
    
    public void setMarker(String m) {
        marker = m;
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
    
    public void setAsLiteral() {
        this.literal = true;
    }
    
    public void setLiteralNode(RDFNode n) {
        literalNode = n;
    }
    
    public boolean isLiteral() {
        return this.literal;
    }
    
    public void addForm(Feature f, String form) {
        
        String lemma = form;
        if (form.contains("+")) {
            String[] parts = form.split("\\+");
            particle = parts[0];
            lemma    = parts[1];
        }
        forms.put(f,lemma);
        if (!features.containsKey(lemma)) features.put(lemma,new ArrayList<>()); 
        features.get(lemma).add(f);
    }
    
    public void addInherentFeature(Feature f) {
        inherentFeatures.add(f);
    }
    
    public void addArgumentMapping(SynArg syn, SemArg sem) {
        argumentMapping.put(syn,sem);
    }   
    
    public String getCanonicalForm() {
        return canonicalForm;
    }
    
    public String getParticle() {
        return particle;
    }

    public String getForm(Feature f) {
        return forms.get(f);
    }
    
    public String getMarker() {
        return marker;
    }
    
    public List<Feature> getFeatures(String f) {
        
        List<Feature> out = features.get(f);
        
        if (out == null) {
            out = new ArrayList<>();
        }
        out.addAll(inherentFeatures);
        
        return out;
    } 
    
    public HashMap<String,List<Feature>> getFeatures() {
        
        HashMap<String,List<Feature>> out = features;
        
        for (String key : out.keySet()) {
             out.get(key).addAll(inherentFeatures);
        }
        
        return out;
    }
    
    public String getReference() {
        return reference;
    }
    
    public RDFNode getLiteralNode() {
        return literalNode;
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
    public String toString() {
    
        return particle + " + " + canonicalForm + " + " + marker + " / " + pos + " (" + frame + ") = " + reference + " ... " + forms;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.canonicalForm);
        hash = 43 * hash + Objects.hashCode(this.particle);
        hash = 43 * hash + Objects.hashCode(this.reference);
        hash = 43 * hash + Objects.hashCode(this.frame);
        hash = 43 * hash + Objects.hashCode(this.pos);
        hash = 43 * hash + Objects.hashCode(this.inherentFeatures);
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
        if (!Objects.equals(this.particle, other.particle)) {
            return false;
        }
        if (!Objects.equals(this.reference, other.reference)) {
            return false;
        }
        if (!Objects.equals(this.frame, other.frame)) {
            return false;
        }
        if (this.pos != other.pos) {
            return false;
        }
        if (!Objects.equals(this.inherentFeatures, other.inherentFeatures)) {
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
