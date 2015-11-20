package interQA.patterns;

import interQA.elements.IndividualElement;
import interQA.elements.StringElement;
import interQA.elements.ClassElement;
import interQA.elements.PropertyElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cunger
 */
public class QueryPattern9_2 extends QueryPattern {
    
        // SELECT ?x WHERE { ?x <Property> <Individual> . } 
        // SELECT ?x WHERE { <Individual> <Property> ?x . } 
    
        // (Who|What) <IntransitivePPVerb:Property> <Name:Individual>?
    
	// Who died in Berlin?
        // Who co-starred with Audrey Hepburn?
    

	public QueryPattern9_2(Lexicon lexicon, InstanceSource instances) {
            
            this.lexicon = lexicon;
            this.instances = instances; 
            
            init();
        }
        
        @Override
        public void init() {
            
            elements = new ArrayList<>();
            
            StringElement element0 = new StringElement(); 
            element0.add("who");
            element0.add("what");
            elements.add(element0);
		
            PropertyElement element1 = new PropertyElement(lexicon,LexicalEntry.POS.VERB,vocab.IntransitivePPFrame); 
            elements.add(element1);
		
            IndividualElement element2 = new IndividualElement(); 
            elements.add(element2);
	}

        @Override
        public void updateAt(int i) {
            
            if (i == 1) {
            // If parse is at element1, fill element2 with possible instances...
                     
                for (LexicalEntry entry : elements.get(1).getActiveEntries()) {
                    
                    String query; 

                    // ...depending on which semantic position it fills.
                    switch (entry.getSemArg(LexicalEntry.SynArg.PREPOSITIONALOBJECT)) {
                        
                         case SUBJOFPROP:
                              query = "SELECT DISTINCT ?x ?label WHERE { "
                                    + " ?x <" + entry.getReference() + "> ?object . "
                                    + " ?x <" + vocab.rdfs + "label> ?l . }";
                              elements.get(2).addToIndex(instances.getInstanceIndex(query,"x","l"));
                         case OBJOFPROP:
                              query = "SELECT DISTINCT ?x ?label WHERE { "
                                    + " ?subject <" + entry.getReference() + "> ?x . "
                                    + " ?x <" + vocab.rdfs + "label> ?l . }";
                              elements.get(2).addToIndex(instances.getInstanceIndex(query,"x","l"));
                    }
                }     
            }
        }        
        
        @Override
	public List<String> buildSPARQLqueries() {
            
            List<String> queries = new ArrayList<>();
            
            ClassElement  verb     = (ClassElement)  elements.get(1);
            IndividualElement instance = (IndividualElement) elements.get(2);
                 
            for (LexicalEntry verb_entry : verb.getActiveEntries()) {
                
                 switch (verb_entry.getSemArg(LexicalEntry.SynArg.PREPOSITIONALOBJECT)) {
                     
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
