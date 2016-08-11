package interQA.lexicon;

import interQA.lexicon.LexicalEntry.Language;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
	
	public Lexicon(Language language) {
            
            model = ModelFactory.createDefaultModel();          
            index = new HashMap<>();
            switch (language) {
               case EN: inflector = new Inflector_en(); break;
               default: inflector = new Inflector_en();
            }
        }
        
        public void load(String filePath) {
            
            model.read(filePath);
        }
        
        public void extractEntries() {
                        
            collectCommonNouns();
            collectTransitiveVerbs();
            collectIntransitivePPVerbs();
            collectNounPPs(); 
            collectNounPossessives();
            collectAdjectivePPs();
            collectPrepositions();
            
            for (String key : index.keySet()) {
                index.put(key,removeDuplicates(index.get(key)));
            }
            
            System.out.println("Lexicon: " + index.keySet()); 
        }
        
        public HashMap<String,List<LexicalEntry>> getSubindex(LexicalEntry.POS pos, String frame, boolean withMarker) {
            
            HashMap<String,List<LexicalEntry>> subindex = new HashMap<>();
            
            Iterator it = index.entrySet().iterator();
            while (it.hasNext()) {
                   Map.Entry pair = (Map.Entry)it.next();
                   String key = (String) pair.getKey();
                   List<LexicalEntry> entries = (List<LexicalEntry>) pair.getValue(); 
                   for (LexicalEntry entry : entries) {
                        if ( entry.getPOS().equals(pos)
                        && ((entry.getFrame() == null && frame == null) 
                         || (entry.getFrame() != null && frame != null && 
                             entry.getFrame().equals(frame)))) {
                            
                            String marker  = entry.getMarker();
                            if (withMarker && marker != null) {
                                key += entry.getMarker();
                            }
                            
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
                               + "SELECT DISTINCT ?canonicalForm ?sg ?pl ?gender ?reference WHERE {"
                               + " ?lexicon lemon:entry ?entry . "
                               + " ?entry   lexinfo:partOfSpeech lexinfo:commonNoun . " 
                               + " OPTIONAL { ?entry lexinfo:gender ?gender } "
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
                        
                        if (sol.contains("gender")) {
                            String gender = sol.get("gender").asResource().getLocalName();
                            switch (gender) {
                                case "feminine":  entry.addInherentFeature(LexicalEntry.Feature.FEMININE); break;
                                case "masculine": entry.addInherentFeature(LexicalEntry.Feature.MASCULINE); break;
                                case "neuter":    entry.addInherentFeature(LexicalEntry.Feature.NEUTER); break;
                            }
                        }
                        
                        String sg; String pl; 
                        
                        if (sol.contains("sg")) sg = sol.get("sg").asLiteral().getValue().toString();
                        else                    sg = canonicalForm;
                        if (sol.contains("pl")) pl = sol.get("pl").asLiteral().getValue().toString();
                        else                    pl = inflector.getPlural(canonicalForm);
                        
                        entry.addForm(LexicalEntry.Feature.SINGULAR,sg);
                        entry.addForm(LexicalEntry.Feature.PLURAL,pl);
                                                    
                        if (!index.containsKey(sg)) index.put(sg ,new ArrayList<>());
                        index.get(sg).add(entry);
                        if (!index.containsKey(pl)) index.put(pl ,new ArrayList<>());
                        index.get(pl).add(entry);
                    }
                    catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }
                }
            }
        }
	
        private void collectTransitiveVerbs() {
            
            String queryString = "PREFIX lemon:   <" + vocab.lemon + "> "
                               + "PREFIX lexinfo: <" + vocab.lexinfo + "> "
                               + "SELECT DISTINCT ?canonicalForm ?pres ?past ?sg ?pl ?reference ?subjOfProp ?objOfProp ?subject ?directObject WHERE {"
                               + " ?lexicon lemon:entry ?entry . "
                               + " ?entry   lexinfo:partOfSpeech lexinfo:verb . "
                               + " ?entry   lemon:canonicalForm ?form . " 
                               + " ?form    lemon:writtenRep ?canonicalForm ."
                               + " OPTIONAL { { ?entry lemon:canonicalForm ?f1 . } UNION { ?entry lemon:otherForm ?f1 . } ?f1 lemon:writtenRep ?pres . ?f1 lexinfo:tense lexinfo:present . } "
                               + " OPTIONAL { { ?entry lemon:canonicalForm ?f2 . } UNION { ?entry lemon:otherForm ?f2 . } ?f2 lemon:writtenRep ?past . ?f2 lexinfo:tense lexinfo:past . } "
                               + " OPTIONAL { { ?entry lemon:canonicalForm ?f3 . } UNION { ?entry lemon:otherForm ?f3 . } ?f3 lemon:writtenRep ?sg   . ?f3 lexinfo:number lexinfo:singular . } "
                               + " OPTIONAL { { ?entry lemon:canonicalForm ?f4 . } UNION { ?entry lemon:otherForm ?f4 . } ?f4 lemon:writtenRep ?pl   . ?f4 lexinfo:number lexinfo:plural . } "
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
                        
                        String pres; String past; 
                        
                        if (sol.contains("pres")) pres = sol.get("pres").asLiteral().getValue().toString();
                        else                      pres = canonicalForm;
                        if (sol.contains("past")) past = sol.get("past").asLiteral().getValue().toString();
                        else                      past = inflector.getPast(canonicalForm,3);
                        
                        entry.addForm(LexicalEntry.Feature.PRESENT,pres);
                        entry.addForm(LexicalEntry.Feature.PAST,past);
                        if (sol.contains("sg")) entry.addForm(LexicalEntry.Feature.SINGULAR,sol.get("sg").asLiteral().getValue().toString());
                        if (sol.contains("pl")) entry.addForm(LexicalEntry.Feature.PLURAL,sol.get("pl").asLiteral().getValue().toString());
                        
                        if (subject.equals(subjOfProp) && directObject.equals(objOfProp)) {
                            entry.addArgumentMapping(LexicalEntry.SynArg.SUBJECT,LexicalEntry.SemArg.SUBJOFPROP);
                            entry.addArgumentMapping(LexicalEntry.SynArg.OBJECT,LexicalEntry.SemArg.OBJOFPROP);
                        }
                        else if (subject.equals(objOfProp) && directObject.equals(subjOfProp)) {
                            entry.addArgumentMapping(LexicalEntry.SynArg.SUBJECT,LexicalEntry.SemArg.OBJOFPROP);
                            entry.addArgumentMapping(LexicalEntry.SynArg.OBJECT,LexicalEntry.SemArg.SUBJOFPROP);
                        }
                        else {
                            continue;
                        }
                        
                        if (!index.containsKey(pres)) index.put(pres ,new ArrayList<>());
                        index.get(pres).add(entry);
                        if (!index.containsKey(past)) index.put(past ,new ArrayList<>());
                        index.get(past).add(entry);
                    }
                    catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }
                }
            }
        }
        
        private void collectIntransitivePPVerbs() {
            
            String queryString = "PREFIX lemon:   <" + vocab.lemon + "> "
                               + "PREFIX lexinfo: <" + vocab.lexinfo + "> "
                               + "SELECT DISTINCT ?canonicalForm ?pres ?past ?sg ?pl ?reference ?subjOfProp ?objOfProp ?subject ?prepositionalObject ?marker WHERE {"
                               + " ?lexicon lemon:entry ?entry . "
                               + " ?entry   lexinfo:partOfSpeech lexinfo:verb . "
                               + " ?entry   lemon:canonicalForm ?form . " 
                               + " ?form    lemon:writtenRep ?canonicalForm ."
                               + " OPTIONAL { { ?entry lemon:canonicalForm ?f1 . } UNION { ?entry lemon:otherForm ?f1 . } ?f1 lemon:writtenRep ?pres . ?f1 lexinfo:tense  lexinfo:present . } "
                               + " OPTIONAL { { ?entry lemon:canonicalForm ?f2 . } UNION { ?entry lemon:otherForm ?f2 . } ?f2 lemon:writtenRep ?past . ?f2 lexinfo:tense  lexinfo:past . } "
                               + " OPTIONAL { { ?entry lemon:canonicalForm ?f3 . } UNION { ?entry lemon:otherForm ?f3 . } ?f3 lemon:writtenRep ?sg   . ?f3 lexinfo:number lexinfo:singular . } "
                               + " OPTIONAL { { ?entry lemon:canonicalForm ?f4 . } UNION { ?entry lemon:otherForm ?f4 . } ?f4 lemon:writtenRep ?pl   . ?f4 lexinfo:number lexinfo:plural . } "
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
            // TODO could, e.g., be case in German!
            
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
                    String marker        = sol.get("marker").asLiteral().getValue().toString();
                    
                    try {      
                        LexicalEntry entry = new LexicalEntry(); 
                        entry.setCanonicalForm(canonicalForm + " " + marker);
                        entry.setReference(reference);
                        entry.setPOS(LexicalEntry.POS.VERB);
                        entry.setFrame(vocab.lexinfo + "IntransitivePPFrame");
                        entry.setMarker(marker);
                        
                        String pres; String past; 
                        
                        if (sol.contains("pres")) pres = sol.get("pres").asLiteral().getValue().toString();
                        else                      pres = canonicalForm;
                        if (sol.contains("past")) past = sol.get("past").asLiteral().getValue().toString();
                        else                      past = inflector.getPast(canonicalForm,3);
                                               
                        entry.addForm(LexicalEntry.Feature.PRESENT,pres);
                        entry.addForm(LexicalEntry.Feature.PAST,past);
                        
                        if (!index.containsKey(pres)) index.put(pres,new ArrayList<>());
                        index.get(pres).add(entry);
                        if (!index.containsKey(past)) index.put(past,new ArrayList<>());
                        index.get(past).add(entry);
                        
                        if (sol.contains("sg")) {
                            String sg = sol.get("sg").asLiteral().getValue().toString();
                            entry.addForm(LexicalEntry.Feature.SINGULAR,sg);
                            if (!index.containsKey(sg)) index.put(sg,new ArrayList<>());
                            index.get(sg).add(entry);
                        }
                        if (sol.contains("pl")) {
                            String pl = sol.get("pl").asLiteral().getValue().toString();
                            entry.addForm(LexicalEntry.Feature.PLURAL,pl);
                            if (!index.containsKey(pl)) index.put(pl,new ArrayList<>());
                            index.get(pl).add(entry);
                        }
                        
                        if (subject.equals(subjOfProp) && prepObject.equals(objOfProp)) {
                            entry.addArgumentMapping(LexicalEntry.SynArg.SUBJECT,LexicalEntry.SemArg.SUBJOFPROP);
                            entry.addArgumentMapping(LexicalEntry.SynArg.OBJECT,LexicalEntry.SemArg.OBJOFPROP);
                        }
                        else if (subject.equals(objOfProp) && prepObject.equals(subjOfProp)) {
                            entry.addArgumentMapping(LexicalEntry.SynArg.SUBJECT,LexicalEntry.SemArg.OBJOFPROP);
                            entry.addArgumentMapping(LexicalEntry.SynArg.OBJECT,LexicalEntry.SemArg.SUBJOFPROP);
                        }
                                                
                    }
                    catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }
                }
            }
        }
        
        private void collectNounPPs() {
            
            String queryString = "PREFIX lemon:   <" + vocab.lemon + "> "
                               + "PREFIX lexinfo: <" + vocab.lexinfo + "> "
                               + "SELECT DISTINCT ?canonicalForm ?sg ?pl ?gender ?reference ?subjOfProp ?objOfProp ?copArg ?prepObject ?marker WHERE {"
                               + " ?lexicon lemon:entry ?entry . "
                               + " ?entry   lexinfo:partOfSpeech lexinfo:noun . "
                               + " OPTIONAL { ?entry lexinfo:gender ?gender } "
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
                               + " ?prepObject lemon:marker ?mentry ."
                               + " ?mentry  lemon:canonicalForm ?mform ."
                               + " ?mform   lemon:writtenRep ?marker . "
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
                    String marker        = sol.get("marker").asLiteral().getValue().toString();
                    
                    try {      
                        LexicalEntry entry = new LexicalEntry(); 
                        entry.setCanonicalForm(canonicalForm);
                        entry.setReference(reference);
                        entry.setPOS(LexicalEntry.POS.NOUN);
                        entry.setFrame(vocab.lexinfo + "NounPPFrame");
                        entry.setMarker(marker);
                        
                        if (sol.contains("gender")) {
                            String gender = sol.get("gender").asResource().getLocalName();
                            switch (gender) {
                                case "feminine":  entry.addInherentFeature(LexicalEntry.Feature.FEMININE); break;
                                case "masculine": entry.addInherentFeature(LexicalEntry.Feature.MASCULINE); break;
                                case "neuter":    entry.addInherentFeature(LexicalEntry.Feature.NEUTER); break;
                            }
                        }
                        
                        String sg; String pl; 
                        
                        if (sol.contains("sg")) sg = sol.get("sg").asLiteral().getValue().toString();
                        else                    sg = canonicalForm;
                        if (sol.contains("pl")) pl = sol.get("pl").asLiteral().getValue().toString();
                        else                    pl = inflector.getPlural(canonicalForm);
                                                
                        entry.addForm(LexicalEntry.Feature.SINGULAR,sg);
                        entry.addForm(LexicalEntry.Feature.PLURAL,pl);
                        
                        if (copArg.equals(subjOfProp) && prepObject.equals(objOfProp)) {
                            entry.addArgumentMapping(LexicalEntry.SynArg.SUBJECT,LexicalEntry.SemArg.SUBJOFPROP);
                            entry.addArgumentMapping(LexicalEntry.SynArg.OBJECT,LexicalEntry.SemArg.OBJOFPROP);
                        }
                        else if (copArg.equals(objOfProp) && prepObject.equals(subjOfProp)) {
                            entry.addArgumentMapping(LexicalEntry.SynArg.SUBJECT,LexicalEntry.SemArg.OBJOFPROP);
                            entry.addArgumentMapping(LexicalEntry.SynArg.OBJECT,LexicalEntry.SemArg.SUBJOFPROP);
                        }
                        else {
                            continue;
                        }
                                                
                        if (!index.containsKey(sg)) index.put(sg,new ArrayList<>());
                        index.get(sg).add(entry);
                        if (!index.containsKey(pl)) index.put(pl,new ArrayList<>());
                        index.get(pl).add(entry);                        
                    }
                    catch (NullPointerException npe) {
                        npe.printStackTrace();
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
                               + " OPTIONAL { ?entry lexinfo:gender ?gender } "
                               + " ?entry   lemon:canonicalForm ?form . " 
                               + " ?form    lemon:writtenRep ?canonicalForm ."
                               + " OPTIONAL { { ?entry lemon:canonicalForm ?f1 . } UNION { ?entry lemon:otherForm ?f1 . } ?f1 lemon:writtenRep ?sg . ?f1 lexinfo:number lexinfo:singular . } "
                               + " OPTIONAL { { ?entry lemon:canonicalForm ?f2 . } UNION { ?entry lemon:otherForm ?f2 . } ?f2 lemon:writtenRep ?pl . ?f2 lexinfo:number lexinfo:plural . } "
                               + " ?entry   lemon:sense ?sense . "
                               + " ?sense   lemon:reference ?reference ."
                               + " ?sense   lemon:subjOfProp ?subjOfProp ."
                               + " ?sense   lemon:objOfProp  ?objOfProp ."
                               + " ?entry   lemon:synBehavior ?frame ."
                               + " ?frame   <" + vocab.rdfType + "> lexinfo:NounPossessiveFrame ."
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
                        entry.setMarker("of"); // TODO specific for English
                        
                        if (sol.contains("gender")) {
                            String gender = sol.get("gender").asResource().getLocalName();
                            switch (gender) {
                                case "feminine":  entry.addInherentFeature(LexicalEntry.Feature.FEMININE); break;
                                case "masculine": entry.addInherentFeature(LexicalEntry.Feature.MASCULINE); break;
                                case "neuter":    entry.addInherentFeature(LexicalEntry.Feature.NEUTER); break;
                            }
                        }
                        
                        String sg; String pl; 
                        
                        if (sol.contains("sg")) sg = sol.get("sg").asLiteral().getValue().toString(); 
                        else                    sg = canonicalForm;                                   
                        if (sol.contains("pl")) pl = sol.get("pl").asLiteral().getValue().toString(); 
                        else                    pl = inflector.getPlural(canonicalForm);              
                        
                        entry.addForm(LexicalEntry.Feature.SINGULAR,sg);
                        entry.addForm(LexicalEntry.Feature.PLURAL,pl);
                        
                        if (copArg.equals(subjOfProp) && possAdj.equals(objOfProp)) {
                            entry.addArgumentMapping(LexicalEntry.SynArg.SUBJECT,LexicalEntry.SemArg.SUBJOFPROP);
                            entry.addArgumentMapping(LexicalEntry.SynArg.OBJECT,LexicalEntry.SemArg.OBJOFPROP);
                        }
                        else if (copArg.equals(objOfProp) && possAdj.equals(subjOfProp)) {
                            entry.addArgumentMapping(LexicalEntry.SynArg.SUBJECT,LexicalEntry.SemArg.OBJOFPROP);
                            entry.addArgumentMapping(LexicalEntry.SynArg.OBJECT,LexicalEntry.SemArg.SUBJOFPROP);
                        }
                        else {
                            continue;
                        }

                        if (!index.containsKey(sg)) index.put(sg,new ArrayList<>());
                        index.get(sg).add(entry); 
                        if (!index.containsKey(pl)) index.put(pl,new ArrayList<>());
                        index.get(pl).add(entry);
                    }
                    catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }
                }
            }
        }
        
        private void collectAdjectivePPs() {
                                    
            String queryString = "PREFIX lemon:   <" + vocab.lemon + "> "
                               + "PREFIX lexinfo: <" + vocab.lexinfo + "> "
                               + "SELECT DISTINCT ?canonicalForm ?reference ?subjOfProp ?objOfProp ?subject ?prepositionalObject ?marker WHERE {"
                               + " ?lexicon lemon:entry ?entry . "
                               + " ?entry   lexinfo:partOfSpeech lexinfo:adjective . "
                               + " ?entry   lemon:canonicalForm ?form . " 
                               + " ?form    lemon:writtenRep ?canonicalForm ."
                               + " ?entry   lemon:sense ?sense . "
                               + " ?sense   lemon:reference ?reference ."
                               + " ?sense   lemon:subjOfProp ?subjOfProp ."
                               + " ?sense   lemon:objOfProp  ?objOfProp ."
                               + " ?entry   lemon:synBehavior ?frame ."
                               + " ?frame   <" + vocab.rdfType + "> lexinfo:AdjectivePPFrame ."
                               + " ?frame   lexinfo:subject ?subject ."
                               + " ?frame   lexinfo:prepositionalObject ?prepositionalObject ."
                               + " ?prepositionalObject lemon:marker ?mentry ."
                               + " ?mentry  lemon:canonicalForm ?mform ."
                               + " ?mform   lemon:writtenRep ?marker . "
                               + "}";
            
            Query query = QueryFactory.create(queryString) ;
  
            try (QueryExecution qexec = QueryExecutionFactory.create(query,model)) {
                    
                ResultSet results = qexec.execSelect() ;
                                
                for ( ; results.hasNext() ; ) {
                              
                    QuerySolution sol = results.nextSolution() ;
                              
                    try {
                        String canonicalForm = sol.get("canonicalForm").asLiteral().getValue().toString(); 
                        String reference     = sol.get("reference").toString();
                        String subjOfProp    = sol.get("subjOfProp").toString();
                        String objOfProp     = sol.get("objOfProp").toString();
                        String subject       = sol.get("subject").toString();
                        String prepObject    = sol.get("prepositionalObject").toString();
                        String marker        = sol.get("marker").asLiteral().getValue().toString();
                                                
                        LexicalEntry entry = new LexicalEntry(); 
                        entry.setCanonicalForm(canonicalForm);
                        entry.setReference(reference);
                        entry.setPOS(LexicalEntry.POS.ADJECTIVE);
                        entry.setFrame(vocab.lexinfo + "AdjectivePPFrame");
                        entry.setMarker(marker);
                        
                        if (subject.equals(subjOfProp) && prepObject.equals(objOfProp)) {
                            entry.addArgumentMapping(LexicalEntry.SynArg.SUBJECT,LexicalEntry.SemArg.SUBJOFPROP);
                            entry.addArgumentMapping(LexicalEntry.SynArg.OBJECT,LexicalEntry.SemArg.OBJOFPROP);
                        }
                        else if (subject.equals(objOfProp) && prepObject.equals(subjOfProp)) {
                            entry.addArgumentMapping(LexicalEntry.SynArg.SUBJECT,LexicalEntry.SemArg.OBJOFPROP);
                            entry.addArgumentMapping(LexicalEntry.SynArg.OBJECT,LexicalEntry.SemArg.SUBJOFPROP);
                        }
                        else {
                            continue;
                        }
                                                
                        String form = canonicalForm;
                        if (!index.containsKey(form)) index.put(form ,new ArrayList<>());
                        index.get(form).add(entry);
                        
                    }
                    catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }
                }
            }
        }
        
        private void collectPrepositions() {
            
            String queryString = "PREFIX lemon:   <" + vocab.lemon + "> "
                               + "PREFIX lexinfo: <" + vocab.lexinfo + "> "
                               + "SELECT DISTINCT ?canonicalForm ?reference ?subjOfProp ?objOfProp ?subject ?object WHERE {"
                               + " ?lexicon lemon:entry ?entry . "
                               + " ?entry   lexinfo:partOfSpeech lexinfo:preposition . "
                               + " ?entry   lemon:canonicalForm ?form . " 
                               + " ?form    lemon:writtenRep ?canonicalForm ."
                               + " ?entry   lemon:sense ?sense . "
                               + " ?sense   lemon:reference ?reference ."
                               + " ?sense   lemon:subjOfProp ?subjOfProp ."
                               + " ?sense   lemon:objOfProp  ?objOfProp ."
                               + " ?entry   lemon:synBehavior ?frame ."
                               + " ?frame   <" + vocab.rdfType + "> lexinfo:PrepositionalFrame ."
                               + " ?frame   lexinfo:attributiveArg ?subject ."
                               + " ?frame   lexinfo:prepositionalAdjunct ?object ."
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
                    String object        = sol.get("object").toString();
                    
                    try {      
                        LexicalEntry entry = new LexicalEntry(); 
                        entry.setCanonicalForm(canonicalForm);
                        entry.setReference(reference);
                        entry.setPOS(LexicalEntry.POS.PREPOSITION);
                        entry.setFrame(vocab.lexinfo + "PrepositionalFrame");
                        
                        if (subject.equals(subjOfProp) && object.equals(objOfProp)) {
                            entry.addArgumentMapping(LexicalEntry.SynArg.SUBJECT,LexicalEntry.SemArg.SUBJOFPROP);
                            entry.addArgumentMapping(LexicalEntry.SynArg.OBJECT,LexicalEntry.SemArg.OBJOFPROP);
                        }
                        else if (subject.equals(objOfProp) && object.equals(subjOfProp)) {
                            entry.addArgumentMapping(LexicalEntry.SynArg.SUBJECT,LexicalEntry.SemArg.OBJOFPROP);
                            entry.addArgumentMapping(LexicalEntry.SynArg.OBJECT,LexicalEntry.SemArg.SUBJOFPROP);
                        }
                        else {
                            continue;
                        }
                        
                        if (!index.containsKey(canonicalForm)) index.put(canonicalForm ,new ArrayList<>());
                        index.get(canonicalForm).add(entry);
                    }
                    catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }
                }
            }
        }
        
        private List<LexicalEntry> removeDuplicates(List<LexicalEntry> list) {

	List<LexicalEntry> result = new ArrayList<>();
	HashSet<LexicalEntry> set = new HashSet<>();

	for (LexicalEntry item : list) {
	    if (!set.contains(item)) {
		result.add(item);
		set.add(item);
	    }
	}
	return result;
    }
}
