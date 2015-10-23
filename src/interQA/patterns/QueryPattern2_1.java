package interQA.patterns;

import interQA.elements.IndividualElement;
import interQA.lexicon.Lexicon;
import interQA.elements.StringElement;
import interQA.elements.ConceptElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import java.util.ArrayList;
import java.util.List;



public class QueryPattern2_1 extends QueryPattern {

        // SELECT ?x WHERE { ?x <Property> <Individual> . } 
        // SELECT ?x WHERE { ?y <Property> <Individual> . } 
    
        // (Who|What) <TransitiveVerb:Property> <Name:Individual>?
    
	// Who created Miffy?
	// What causes cancer?
    
    
        InstanceSource instances;
        
    
	public QueryPattern2_1(Lexicon lexicon, InstanceSource instances) {
            
            this.instances = instances;
            
            StringElement element0 = new StringElement(); 
            element0.add("who");
            element0.add("what");
            elements.add(element0);
		
            ConceptElement element1 = new ConceptElement(lexicon,LexicalEntry.POS.VERB,vocab.TransitiveFrame); 
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
                    switch (entry.getSemArg(LexicalEntry.SynArg.DIRECTOBJECT)) {
                        
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
            
            ConceptElement  verb     = (ConceptElement)  elements.get(1);
            IndividualElement instance = (IndividualElement) elements.get(2);
                 
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
