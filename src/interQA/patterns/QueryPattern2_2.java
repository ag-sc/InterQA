package interQA.patterns;

import interQA.elements.InstanceElement;
import interQA.elements.StringElement;
import interQA.elements.VerbElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cunger
 */
public class QueryPattern2_2 extends QueryPattern {
    
        // SELECT ?x WHERE { ?x <Property> <Individual> . } 
        // SELECT ?x WHERE { ?y <Property> <Individual> . } 
    
        // (Who|What) <IntransitivePPVerb:Property> <Name:Individual>?
    
	// Who died in Berlin?
        // Who co-starred with Audrey Hepburn?
    
    
        InstanceSource instances;
    
    
	public QueryPattern2_2(Lexicon lexicon, InstanceSource instances) {
            
            this.instances = instances;
            
            StringElement element0 = new StringElement(); 
            element0.add("who");
            element0.add("what");
            elements.add(element0);
		
            VerbElement element1 = new VerbElement(lexicon,vocab.IntransitivePPFrame); 
            elements.add(element1);
		
            InstanceElement element2 = new InstanceElement(); 
            elements.add(element2);
	}

        @Override
        public void updateAt(int i) {
            
            if (i == 1) {
            // If parse is at element1, fill element2 with possible instances...
                     
                for (LexicalEntry entry : elements.get(1).getActiveEntries()) {
                    
                    String query; 

                    // ...depending on which semantic position it fills.
                    switch (entry.getSemArg(LexicalEntry.SynArg.DIRECTOBJECT)) {
                        
                         case SUBJOFPROP:
                              query = "SELECT DISTINCT ?x ?label WHERE { "
                                    + " ?x <" + entry.getReference() + "> ?object . "
                                    + " ?x <" + vocab.rdfsLabel + "> ?l . }";
                              elements.get(2).addToIndex(instances.getInstanceIndex(query,"x","l"));
                         case OBJOFPROP:
                              query = "SELECT DISTINCT ?x ?label WHERE { "
                                    + " ?subject <" + entry.getReference() + "> ?x . "
                                    + " ?x <" + vocab.rdfsLabel + "> ?l . }";
                              elements.get(2).addToIndex(instances.getInstanceIndex(query,"x","l"));
                    }
                }     
            }
        }        
        
        @Override
	public List<String> buildSPARQLqueries() {
            
            List<String> queries = new ArrayList<>();
            
            VerbElement     verb     = (VerbElement)     elements.get(1);
            InstanceElement instance = (InstanceElement) elements.get(2);
                 
            for (LexicalEntry verb_entry : verb.getActiveEntries()) {
                
                 switch (verb_entry.getSemArg(LexicalEntry.SynArg.DIRECTOBJECT)) {
                     
                    case SUBJOFPROP: 
                         for (LexicalEntry inst_entry : instance.getActiveEntries()) {
                              queries.add("SELECT DISTINCT ?x WHERE {"
                                        + " <" + inst_entry.getReference() + "> <" + verb_entry.getReference() + "> ?x . }");
                         }
                         break;
                    case OBJOFPROP: 
                         for (LexicalEntry inst_entry : instance.getActiveEntries()) {
                              queries.add("SELECT DISTINCT ?x WHERE {"
                                        + " ?x <" + verb_entry.getReference() + "> <" + inst_entry.getReference() + "> . }");
                         }
                         break;
                 }
            }
		
            return queries;
	}
}
