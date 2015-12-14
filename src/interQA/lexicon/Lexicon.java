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
        
	HashMap<String,List<LexicalEntry>> index;
	
        Vocabulary vocab = new Vocabulary();
        Inflector inflector;
	
	public Lexicon(String language) {
            
            model = ModelFactory.createDefaultModel();          
            index = new HashMap<>();
            switch (language) {
               case "en": inflector = new Inflector_en(); break;
               default:   inflector = new Inflector_en();
            }
        }
        
        public void load(String filePath) {
            
            model.read(filePath);
                        
            collectCommonNouns();
            // TODO collect IntransitiveVerbs
            collectTransitiveVerbs();
            collectIntransitivePPVerbs();
            collectNounPPs(); 
            collectNounPossessives();
            // TODO collect adjectives
        }
        
        public HashMap<String,List<LexicalEntry>> getSubindex(LexicalEntry.POS pos, String frame) {
            
            HashMap<String,List<LexicalEntry>> subindex = new HashMap<>();
            
            Iterator it = index.entrySet().iterator();
            while (it.hasNext()) {
                   Map.Entry pair = (Map.Entry)it.next();
                   String key = (String) pair.getKey();
                   List<LexicalEntry> entries = (List<LexicalEntry>) pair.getValue(); 
                   for (LexicalEntry entry : entries) {
                        if ( entry.getPOS().equals(pos)
                        && ((entry.getFrame() == null && frame == null) 
                         || (entry.getFrame() != null && frame != null && entry.getFrame().equals(frame)))) {
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
                               + "SELECT DISTINCT ?canonicalForm ?sg ?pl ?reference WHERE {"
                               + " ?lexicon lemon:entry ?entry . "
                               + " ?entry   lexinfo:partOfSpeech lexinfo:commonNoun . "
                               + " ?entry   lemon:canonicalForm ?form . " 
                               + " ?form    lemon:writtenRep ?canonicalForm ."
                               + " OPTIONAL { { ?entry lemon:canonicalForm ?f1 . } UNION { ?entry lemon:otherForm ?f1 . } ?f1 lemon:writtenRep ?sg . ?f1 lexinfo:number lexinfo:singular . } "
                               + " OPTIONAL { { ?entry lemon:canonicalForm ?f2 . } UNION { ?entry lemon:otherForm ?f2 . } ?f2 lemon:writtenRep ?pl . ?f2 lexinfo:number lexinfo:plural . } "
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
                        entry.setPOS(LexicalEntry.POS.NOUN);
                                  
                        if (sol.contains("sg")) { 
                            entry.addForm(LexicalEntry.Feature.SINGULAR,sol.get("sg").asLiteral().getValue().toString());
                        } else {
                            entry.addForm(LexicalEntry.Feature.SINGULAR,canonicalForm);
                        }
                        if (sol.contains("pl")) { 
                            entry.addForm(LexicalEntry.Feature.PLURAL,sol.get("pl").asLiteral().getValue().toString());
                        } else {
                            entry.addForm(LexicalEntry.Feature.PLURAL,inflector.getPlural(canonicalForm));
                        }
                                                                        
                        if (!index.containsKey(canonicalForm)) {
                             index.put(canonicalForm,new ArrayList<>());
                        }
                        index.get(canonicalForm).add(entry);
                    }
                    catch (NullPointerException npe) {
                    }
                }
            }
        }
	
        private void collectTransitiveVerbs() {
            
            String queryString = "PREFIX lemon:   <" + vocab.lemon + "> "
                               + "PREFIX lexinfo: <" + vocab.lexinfo + "> "
                               + "SELECT DISTINCT ?canonicalForm ?pres ?past ?reference ?subjOfProp ?objOfProp ?subject ?directObject WHERE {"
                               + " ?lexicon lemon:entry ?entry . "
                               + " ?entry   lexinfo:partOfSpeech lexinfo:verb . "
                               + " ?entry   lemon:canonicalForm ?form . " 
                               + " ?form    lemon:writtenRep ?canonicalForm ."
                               + " OPTIONAL { { ?entry lemon:canonicalForm ?f1 . } UNION { ?entry lemon:otherForm ?f1 . } ?f1 lemon:writtenRep ?pres . ?f1 lexinfo:tense lexinfo:present . } "
                               + " OPTIONAL { { ?entry lemon:canonicalForm ?f2 . } UNION { ?entry lemon:otherForm ?f2 . } ?f2 lemon:writtenRep ?past . ?f2 lexinfo:tense lexinfo:past . } "
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
                        entry.setPOS(LexicalEntry.POS.VERB);
                        entry.setFrame(vocab.lexinfo + "TransitiveFrame");
                        
                        if (sol.contains("pres")) { 
                            entry.addForm(LexicalEntry.Feature.PRESENT,sol.get("pres").asLiteral().getValue().toString());
                        } else {
                            entry.addForm(LexicalEntry.Feature.PRESENT,canonicalForm);
                        }
                        if (sol.contains("past")) { 
                            entry.addForm(LexicalEntry.Feature.PAST,sol.get("past").asLiteral().getValue().toString());
                        } else {
                            entry.addForm(LexicalEntry.Feature.PAST,inflector.getPast(canonicalForm,3));
                        }
                        
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
                        
                        if (!index.containsKey(canonicalForm)) {
                             index.put(canonicalForm,new ArrayList<>());
                        }
                        index.get(canonicalForm).add(entry);
                    }
                    catch (NullPointerException npe) {
                    }
                }
            }
        }
        
        private void collectIntransitivePPVerbs() {
            
            String queryString = "PREFIX lemon:   <" + vocab.lemon + "> "
                               + "PREFIX lexinfo: <" + vocab.lexinfo + "> "
                               + "SELECT DISTINCT ?canonicalForm ?pres ?past ?reference ?subjOfProp ?objOfProp ?subject ?prepositionalObject ?marker WHERE {"
                               + " ?lexicon lemon:entry ?entry . "
                               + " ?entry   lexinfo:partOfSpeech lexinfo:verb . "
                               + " ?entry   lemon:canonicalForm ?form . " 
                               + " ?form    lemon:writtenRep ?canonicalForm ."
                               + " OPTIONAL { { ?entry lemon:canonicalForm ?f1 . } UNION { ?entry lemon:otherForm ?f1 . } ?f1 lemon:writtenRep ?pres . ?f1 lexinfo:tense lexinfo:present . } "
                               + " OPTIONAL { { ?entry lemon:canonicalForm ?f2 . } UNION { ?entry lemon:otherForm ?f2 . } ?f2 lemon:writtenRep ?past . ?f2 lexinfo:tense lexinfo:past . } "
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
                        entry.setPOS(LexicalEntry.POS.VERB);
                        entry.setFrame(vocab.lexinfo + "IntransitivePPFrame");
                        
                        if (sol.contains("pres")) { 
                            entry.addForm(LexicalEntry.Feature.PRESENT,sol.get("pres").asLiteral().getValue().toString());
                        } else {
                            entry.addForm(LexicalEntry.Feature.PRESENT,canonicalForm);
                        }
                        if (sol.contains("past")) { 
                            entry.addForm(LexicalEntry.Feature.PAST,sol.get("past").asLiteral().getValue().toString());
                        } else {
                            entry.addForm(LexicalEntry.Feature.PAST,inflector.getPast(canonicalForm,3));
                        }
                        
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
                                                
                        if (!index.containsKey(form)) {
                             index.put(form,new ArrayList<>());
                        }
                        index.get(form).add(entry);
                    }
                    catch (NullPointerException npe) {
                    }
                }
            }
        }
        
        private void collectNounPPs() {
            
            String queryString = "PREFIX lemon:   <" + vocab.lemon + "> "
                               + "PREFIX lexinfo: <" + vocab.lexinfo + "> "
                               + "SELECT DISTINCT ?canonicalForm ?sg ?pl ?reference ?subjOfProp ?objOfProp ?copArg ?prepObject WHERE {"
                               + " ?lexicon lemon:entry ?entry . "
                               + " ?entry   lexinfo:partOfSpeech lexinfo:noun . "
                               + " ?entry   lemon:canonicalForm ?form . " 
                               + " ?form    lemon:writtenRep ?canonicalForm ."
                               + " OPTIONAL { { ?entry lemon:canonicalForm ?f1 . } UNION { ?entry lemon:otherForm ?f1 . } ?f1 lemon:writtenRep ?sg . ?f1 lexinfo:number lexinfo:singular . } "
                               + " OPTIONAL { { ?entry lemon:canonicalForm ?f2 . } UNION { ?entry lemon:otherForm ?f2 . } ?f2 lemon:writtenRep ?pl . ?f2 lexinfo:number lexinfo:plural . } "
                               + " ?entry   lemon:sense ?sense . "
                               + " ?sense   lemon:reference ?reference ."
                               + " ?sense   lemon:subjOfProp ?subjOfProp ."
                               + " ?sense   lemon:objOfProp  ?objOfProp ."
                               + " ?entry   lemon:synBehavior ?frame ."
                               + " ?frame   <" + vocab.rdfType + "> lexinfo:NounPPFrame ."
                               + " ?frame   lexinfo:copulativeArg ?copArg ."
                               + " ?frame   lexinfo:prepositionalObject ?prepObject ."
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
                    String copArg        = sol.get("copArg").toString();
                    String prepObject    = sol.get("prepObject").toString();
                    
                    try {      
                        LexicalEntry entry = new LexicalEntry(); 
                        entry.setCanonicalForm(canonicalForm);
                        entry.setReference(reference);
                        entry.setPOS(LexicalEntry.POS.NOUN);
                        entry.setFrame(vocab.lexinfo + "NounPPFrame");
                        
                        if (sol.contains("sg")) { 
                            entry.addForm(LexicalEntry.Feature.SINGULAR,sol.get("sg").asLiteral().getValue().toString());
                        } else {
                            entry.addForm(LexicalEntry.Feature.SINGULAR,canonicalForm);
                        }
                        if (sol.contains("pl")) { 
                            entry.addForm(LexicalEntry.Feature.PLURAL,sol.get("pl").asLiteral().getValue().toString());
                        } else {
                            entry.addForm(LexicalEntry.Feature.PLURAL,inflector.getPlural(canonicalForm));
                        }
                        
                        if (copArg.equals(subjOfProp) && prepObject.equals(objOfProp)) {
                            entry.addArgumentMapping(LexicalEntry.SynArg.COPULATIVEARG,LexicalEntry.SemArg.SUBJOFPROP);
                            entry.addArgumentMapping(LexicalEntry.SynArg.PREPOSITIONALOBJECT,LexicalEntry.SemArg.OBJOFPROP);
                        }
                        else if (copArg.equals(objOfProp) && prepObject.equals(subjOfProp)) {
                            entry.addArgumentMapping(LexicalEntry.SynArg.COPULATIVEARG,LexicalEntry.SemArg.OBJOFPROP);
                            entry.addArgumentMapping(LexicalEntry.SynArg.PREPOSITIONALOBJECT,LexicalEntry.SemArg.SUBJOFPROP);
                        }
                        else {
                            continue;
                        }
                        
                        if (!index.containsKey(canonicalForm)) {
                             index.put(canonicalForm,new ArrayList<>());
                        }
                        index.get(canonicalForm).add(entry);
                    }
                    catch (NullPointerException npe) {
                    }
                }
            }
        }
        
        private void collectNounPossessives() {
            
            String queryString = "PREFIX lemon:   <" + vocab.lemon + "> "
                               + "PREFIX lexinfo: <" + vocab.lexinfo + "> "
                               + "SELECT DISTINCT ?canonicalForm ?sg ?pl ?reference ?subjOfProp ?objOfProp ?copArg ?possAdj WHERE {"
                               + " ?lexicon lemon:entry ?entry . "
                               + " ?entry   lexinfo:partOfSpeech lexinfo:noun . "
                               + " ?entry   lemon:canonicalForm ?form . " 
                               + " OPTIONAL { { ?entry lemon:canonicalForm ?f1 . } UNION { ?entry lemon:otherForm ?f1 . } ?f1 lemon:writtenRep ?sg . ?f1 lexinfo:number lexinfo:singular . } "
                               + " OPTIONAL { { ?entry lemon:canonicalForm ?f2 . } UNION { ?entry lemon:otherForm ?f2 . } ?f2 lemon:writtenRep ?pl . ?f2 lexinfo:number lexinfo:plural . } "
                               + " ?form    lemon:writtenRep ?canonicalForm ."
                               + " ?entry   lemon:sense ?sense . "
                               + " ?sense   lemon:reference ?reference ."
                               + " ?sense   lemon:subjOfProp ?subjOfProp ."
                               + " ?sense   lemon:objOfProp  ?objOfProp ."
                               + " ?entry   lemon:synBehavior ?frame ."
                               + " ?frame   <" + vocab.rdfType + "> lexinfo:NounPPFrame ."
                               + " ?frame   lexinfo:copulativeArg ?copArg ."
                               + " ?frame   lexinfo:possessiveAdjunct ?possAdj ."
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
                    String copArg        = sol.get("copArg").toString();
                    String possAdj       = sol.get("possAdj").toString();
                    
                    try {      
                        LexicalEntry entry = new LexicalEntry(); 
                        entry.setCanonicalForm(canonicalForm);
                        entry.setReference(reference);
                        entry.setPOS(LexicalEntry.POS.NOUN);
                        entry.setFrame(vocab.lexinfo + "NounPossessiveFrame");
                        
                        if (sol.contains("sg")) { 
                            entry.addForm(LexicalEntry.Feature.SINGULAR,sol.get("sg").asLiteral().getValue().toString());
                        } else {
                            entry.addForm(LexicalEntry.Feature.SINGULAR,canonicalForm);
                        }
                        if (sol.contains("pl")) { 
                            entry.addForm(LexicalEntry.Feature.PLURAL,sol.get("pl").asLiteral().getValue().toString());
                        } else {
                            entry.addForm(LexicalEntry.Feature.PLURAL,inflector.getPlural(canonicalForm));
                        }
                        
                        if (copArg.equals(subjOfProp) && possAdj.equals(objOfProp)) {
                            entry.addArgumentMapping(LexicalEntry.SynArg.COPULATIVEARG,LexicalEntry.SemArg.SUBJOFPROP);
                            entry.addArgumentMapping(LexicalEntry.SynArg.POSSESSIVEADJUNCT,LexicalEntry.SemArg.OBJOFPROP);
                        }
                        else if (copArg.equals(objOfProp) && possAdj.equals(subjOfProp)) {
                            entry.addArgumentMapping(LexicalEntry.SynArg.COPULATIVEARG,LexicalEntry.SemArg.OBJOFPROP);
                            entry.addArgumentMapping(LexicalEntry.SynArg.POSSESSIVEADJUNCT,LexicalEntry.SemArg.SUBJOFPROP);
                        }
                        else {
                            continue;
                        }
                                                
                        String form1 = canonicalForm + " of"; // TODO still specific for English
                        String form2 = "'s " + canonicalForm; // TODO still specific for English
                                                
                        if (!index.containsKey(form1)) {
                             index.put(form1,new ArrayList<>());
                        }
                        index.get(form1).add(entry);
                        if (!index.containsKey(form2)) {
                             index.put(form2,new ArrayList<>());
                        }
                        index.get(form2).add(entry);
                    }
                    catch (NullPointerException npe) {
                    }
                }
            }
        }
}
