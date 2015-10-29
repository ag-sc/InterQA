package interQA.patterns;

import interQA.elements.IndividualElement;
import interQA.elements.StringElement;
import interQA.elements.ConceptElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cunger
 */
public class QueryPattern9_3 extends QueryPattern {
    
    // SELECT ?x WHERE { ?x <Property> <Individual> . } 
    // SELECT ?x WHERE { <Individual> <Property> ?x . } 
    
    // Give me (all|the) <NounPP:Property> <Name:Individual>.
    
    // Give me all movies by Tarantino. 
    // Give me all rivers in Turkmenistan.
    // Give me the founding date of Boston.
    
    
        InstanceSource instances;
    
    
	public QueryPattern9_3(Lexicon lexicon, InstanceSource instances) {
            
            this.instances = instances;
            
            StringElement element0 = new StringElement(); 
            element0.add("give me");
            elements.add(element0);
	
            StringElement element1 = new StringElement(); 
            element1.add("all");
            element1.add("the");
            elements.add(element1);
            
            ConceptElement element2 = new ConceptElement(lexicon,LexicalEntry.POS.NOUN,vocab.NounPPFrame); 
            elements.add(element2);
		
            IndividualElement element3 = new IndividualElement(); 
            elements.add(element3);
	}

        @Override
        public void updateAt(int i) {
            
            if (i == 2) {
            // If parse is at element2, fill element3 with possible instances...
                     
                for (LexicalEntry entry : elements.get(2).getActiveEntries()) {
                    
                    String query; 

                    // ...depending on which semantic position it fills.
                    switch (entry.getSemArg(LexicalEntry.SynArg.PREPOSITIONALOBJECT)) {
                        
                         case SUBJOFPROP:
                              query = "SELECT DISTINCT ?x ?label WHERE { "
                                    + " ?x <" + entry.getReference() + "> ?object . "
                                    + " ?x <" + vocab.rdfs + "label> ?l . }";
                              elements.get(3).addToIndex(instances.getInstanceIndex(query,"x","l"));
                         case OBJOFPROP:
                              query = "SELECT DISTINCT ?x ?label WHERE { "
                                    + " ?subject <" + entry.getReference() + "> ?x . "
                                    + " ?x <" + vocab.rdfs + "label> ?l . }";
                              elements.get(3).addToIndex(instances.getInstanceIndex(query,"x","l"));
                    }
                }     
            }
        }        
        
        @Override
	public List<String> buildSPARQLqueries() {
            
            List<String> queries = new ArrayList<>();
            
            ConceptElement  noun     = (ConceptElement)  elements.get(2);
            IndividualElement instance = (IndividualElement) elements.get(3);
                 
            for (LexicalEntry noun_entry : noun.getActiveEntries()) {
                
                 switch (noun_entry.getSemArg(LexicalEntry.SynArg.PREPOSITIONALOBJECT)) {
                     
                    case SUBJOFPROP: 
                         for (LexicalEntry inst_entry : instance.getActiveEntries()) {
                              queries.add("SELECT DISTINCT ?x WHERE {"
                                        + " <" + inst_entry.getReference() + "> <" + noun_entry.getReference() + "> ?x . }");
                         }
                         break;
                    case OBJOFPROP: 
                         for (LexicalEntry inst_entry : instance.getActiveEntries()) {
                              queries.add("SELECT DISTINCT ?x WHERE {"
                                        + " ?x <" + noun_entry.getReference() + "> <" + inst_entry.getReference() + "> . }");
                         }
                         break;
                 }
            }
		
            return queries;
	}
}