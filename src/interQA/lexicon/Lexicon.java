package interQA.lexicon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;


/**
 *
 * @author cunger
 */
public class Lexicon {
    
        Model model;
        
	HashMap<String,List<LexicalEntry>> NOUN_index;
        HashMap<String,List<LexicalEntry>> VERB_index;
        HashMap<String,List<LexicalEntry>> ADJECTIVE_index;
	
        Vocabulary vocab = new Vocabulary();
        
	
	public Lexicon() {
            
            model = ModelFactory.createDefaultModel();
            
            NOUN_index = new HashMap<>();
            VERB_index = new HashMap<>();
            ADJECTIVE_index = new HashMap<>();
        }
        
        public void load(String filePath) {
            
            model.read(filePath);
            
            collectCommonNouns();
            // TODO collect IntransitiveVerbs
            collectTransitiveVerbs();
            collectIntransitivePPVerbs();
            // TODO collect NounPPs 
            // TODO collect NounPossessives
            // TODO collect adjectives
        }


        public HashMap<String,List<LexicalEntry>> getNounIndex() {
            return getSubindex(NOUN_index,null);
        }
        public HashMap<String,List<LexicalEntry>> getNounIndex(String frame) {
            return getSubindex(NOUN_index,frame);
        }
        public HashMap<String,List<LexicalEntry>> getVerbIndex(String frame) {
            return getSubindex(VERB_index,frame);
        }
        
        public HashMap<String,List<LexicalEntry>> getSubindex(HashMap<String,List<LexicalEntry>> index, String frame) {
            
            HashMap<String,List<LexicalEntry>> subindex = new HashMap<>();
            
            Iterator it = index.entrySet().iterator();
            while (it.hasNext()) {
                   Map.Entry pair = (Map.Entry)it.next();
                   String key = (String) pair.getKey();
                   List<LexicalEntry> entries = (List<LexicalEntry>) pair.getValue(); 
                   for (LexicalEntry entry : entries) {
                        if ((entry.getFrame() == null && frame == null) 
                          || entry.getFrame().equals(frame)) {
                             if (!subindex.containsKey(key)) {
                                  subindex.put(key,new ArrayList<>());
                             }
                             subindex.get(key).add(entry);
                        }
                   }
            }
            
            return subindex;
        }
        
        
        private void collectCommonNouns() {
            
            String queryString = "PREFIX lemon:   <" + vocab.lemon + "> "
                               + "PREFIX lexinfo: <" + vocab.lexinfo + "> "
                               + "SELECT DISTINCT ?canonicalForm ?reference WHERE {"
                               + " ?lexicon lemon:entry ?entry . "
                               + " ?entry   lexinfo:partOfSpeech lexinfo:commonNoun . "
                               + " ?entry   lemon:canonicalForm ?form . " 
                               + " ?form    lemon:writtenRep ?canonicalForm ."
                               + " ?entry   lemon:sense ?sense . "
                               + " ?sense   lemon:reference ?reference ."
                               + "}";
            
            Query query = QueryFactory.create(queryString) ;
  
            try (QueryExecution qexec = QueryExecutionFactory.create(query,model)) {
    
                ResultSet results = qexec.execSelect() ;
                                
                for ( ; results.hasNext() ; ) {
                    
                    QuerySolution sol = results.nextSolution() ;
                                              
                    try {
                        String canonicalForm = sol.get("canonicalForm").asLiteral().getValue().toString(); 
                        String reference     = sol.get("reference").toString(); 

                        LexicalEntry entry = new LexicalEntry(); 
                        entry.setCanonicalForm(canonicalForm);
                        entry.setReference(reference);

                        if (!NOUN_index.containsKey(canonicalForm)) {
                             NOUN_index.put(canonicalForm,new ArrayList<>());
                        }
                        NOUN_index.get(canonicalForm).add(entry);
                    }
                    catch (NullPointerException npe) {
                    }
                }
            }
        }
	
        private void collectTransitiveVerbs() {
            
            String queryString = "PREFIX lemon:   <" + vocab.lemon + "> "
                               + "PREFIX lexinfo: <" + vocab.lexinfo + "> "
                               + "SELECT DISTINCT ?canonicalForm ?reference ?subjOfProp ?objOfProp ?subject ?directObject WHERE {"
                               + " ?lexicon lemon:entry ?entry . "
                               + " ?entry   lexinfo:partOfSpeech lexinfo:verb . "
                               + " ?entry   lemon:canonicalForm ?form . " 
                               + " ?form    lemon:writtenRep ?canonicalForm ."
                               + " ?entry   lemon:sense ?sense . "
                               + " ?sense   lemon:reference ?reference ."
                               + " ?sense   lemon:subjOfProp ?subjOfProp ."
                               + " ?sense   lemon:objOfProp  ?objOfProp ."
                               + " ?entry   lemon:synBehavior ?frame ."
                               + " ?frame   <" + vocab.rdfType + "> lexinfo:TransitiveFrame ."
                               + " ?frame   lexinfo:subject ?subject ."
                               + " ?frame   lexinfo:directObject ?directObject ."
                               + "}";
            
            Query query = QueryFactory.create(queryString) ;
  
            try (QueryExecution qexec = QueryExecutionFactory.create(query,model)) {
    
                ResultSet results = qexec.execSelect() ;
                                
                for ( ; results.hasNext() ; ) {
                    
                    QuerySolution sol = results.nextSolution() ;
                    
                    String canonicalForm = sol.get("canonicalForm").asLiteral().getValue().toString(); 
                    String reference     = sol.get("reference").toString(); 
                    String subjOfProp    = sol.get("subjOfProp").toString();
                    String objOfProp     = sol.get("objOfProp").toString();
                    String subject       = sol.get("subject").toString();
                    String directObject  = sol.get("directObject").toString();
                    
                    try {      
                        LexicalEntry entry = new LexicalEntry(); 
                        entry.setCanonicalForm(canonicalForm);
                        entry.setReference(reference);
                        entry.setFrame(vocab.lexinfo + "TransitiveFrame");
                        
                        if (subject.equals(subjOfProp) && directObject.equals(objOfProp)) {
                            entry.addArgumentMapping(LexicalEntry.SynArg.SUBJECT,LexicalEntry.SemArg.SUBJOFPROP);
                            entry.addArgumentMapping(LexicalEntry.SynArg.DIRECTOBJECT,LexicalEntry.SemArg.OBJOFPROP);
                        }
                        else if (subject.equals(objOfProp) && directObject.equals(subjOfProp)) {
                            entry.addArgumentMapping(LexicalEntry.SynArg.SUBJECT,LexicalEntry.SemArg.OBJOFPROP);
                            entry.addArgumentMapping(LexicalEntry.SynArg.DIRECTOBJECT,LexicalEntry.SemArg.SUBJOFPROP);
                        }
                        else {
                            continue;
                        }
                        
                        if (!VERB_index.containsKey(canonicalForm)) {
                             VERB_index.put(canonicalForm,new ArrayList<>());
                        }
                        VERB_index.get(canonicalForm).add(entry);
                    }
                    catch (NullPointerException npe) {
                    }
                }
            }
        }
        
        private void collectIntransitivePPVerbs() {
            
            String queryString = "PREFIX lemon:   <" + vocab.lemon + "> "
                               + "PREFIX lexinfo: <" + vocab.lexinfo + "> "
                               + "SELECT DISTINCT ?canonicalForm ?reference ?subjOfProp ?objOfProp ?subject ?prepositionalObject ?marker WHERE {"
                               + " ?lexicon lemon:entry ?entry . "
                               + " ?entry   lexinfo:partOfSpeech lexinfo:verb . "
                               + " ?entry   lemon:canonicalForm ?form . " 
                               + " ?form    lemon:writtenRep ?canonicalForm ."
                               + " ?entry   lemon:sense ?sense . "
                               + " ?sense   lemon:reference ?reference ."
                               + " ?sense   lemon:subjOfProp ?subjOfProp ."
                               + " ?sense   lemon:objOfProp  ?objOfProp ."
                               + " ?entry   lemon:synBehavior ?frame ."
                               + " ?frame   <" + vocab.rdfType + "> lexinfo:IntransitivePPFrame ."
                               + " ?frame   lexinfo:subject ?subject ."
                               + " ?frame   lexinfo:prepositionalObject ?prepositionalObject ."
                               + " ?prepositionalObject lemon:marker ?mentry ."
                               + " ?mentry  lemon:canonicalForm ?mform ."
                               + " ?mform   lemon:writtenRep ?marker . "
                               + "}";
            // TODO marker string might be given directly, not via marker entry
            
            Query query = QueryFactory.create(queryString) ;
  
            try (QueryExecution qexec = QueryExecutionFactory.create(query,model)) {
    
                ResultSet results = qexec.execSelect() ;
                                
                for ( ; results.hasNext() ; ) {
                    
                    QuerySolution sol = results.nextSolution() ;
                    
                    String canonicalForm = sol.get("canonicalForm").asLiteral().getValue().toString(); 
                    String reference     = sol.get("reference").toString(); 
                    String subjOfProp    = sol.get("subjOfProp").toString();
                    String objOfProp     = sol.get("objOfProp").toString();
                    String subject       = sol.get("subject").toString();
                    String prepObject    = sol.get("prepositionalObject").toString();
                    String marker        = sol.get("marker").toString();
                    
                    try {      
                        LexicalEntry entry = new LexicalEntry(); 
                        entry.setCanonicalForm(canonicalForm);
                        entry.setReference(reference);
                        entry.setFrame(vocab.lexinfo + "IntransitivePPFrame");
                        
                        if (subject.equals(subjOfProp) && prepObject.equals(objOfProp)) {
                            entry.addArgumentMapping(LexicalEntry.SynArg.SUBJECT,LexicalEntry.SemArg.SUBJOFPROP);
                            entry.addArgumentMapping(LexicalEntry.SynArg.PREPOSITIONALOBJECT,LexicalEntry.SemArg.OBJOFPROP);
                        }
                        else if (subject.equals(objOfProp) && prepObject.equals(subjOfProp)) {
                            entry.addArgumentMapping(LexicalEntry.SynArg.SUBJECT,LexicalEntry.SemArg.OBJOFPROP);
                            entry.addArgumentMapping(LexicalEntry.SynArg.PREPOSITIONALOBJECT,LexicalEntry.SemArg.SUBJOFPROP);
                        }
                        else {
                            continue;
                        }
                        
                        entry.addMarker(LexicalEntry.SynArg.PREPOSITIONALOBJECT,marker);
                        
                        String form = canonicalForm + " " + marker;
                        
                        if (!VERB_index.containsKey(form)) {
                             VERB_index.put(form,new ArrayList<>());
                        }
                        VERB_index.get(form).add(entry);
                    }
                    catch (NullPointerException npe) {
                    }
                }
            }
        }
}
