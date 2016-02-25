package interQA.patterns;

import interQA.elements.IndividualElement;
import interQA.elements.StringElement;
import interQA.elements.ClassElement;
import interQA.elements.PropertyElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.LexicalEntry.Feature;
import interQA.lexicon.Lexicon;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author cunger
 */
public class Give_me_all_P_I extends QueryPattern {
    
    
        /*
        SELECT ?x WHERE { ?x <Property> <Individual> . } 
        SELECT ?x WHERE { <Individual> <Property> ?x . } 

        List|(Give|Show me) all|the <NounPP:Property> <Name:Individual>.

          Give me all movies by Tarantino. 
          Give me all rivers in Turkmenistan.
          Give me the founding date of Boston.
        */
    
    
	public Give_me_all_P_I(Lexicon lexicon, InstanceSource instances) {
            
            this.lexicon = lexicon;
            this.instances = instances; 
            
            init();
        }
        
        @Override
        public void init() {  
            
            elements = new ArrayList<>();
            
            StringElement element0 = new StringElement(); 
            element0.add("give me");
            element0.add("show me");
            element0.add("list me");
            elements.add(element0);
	
            StringElement element1 = new StringElement(); 
            element1.add("all",Feature.PLURAL);
            element1.add("the");
            elements.add(element1);
            
            PropertyElement element2 = new PropertyElement();
            element2.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.NounPPFrame);
            element2.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.NounPossessiveFrame);
            elements.add(element2);
		
            IndividualElement element3 = new IndividualElement(); 
            elements.add(element3);
	}

        @Override
        public void update(String s) {
            
            switch (currentElement) {
                
                case 1: ((StringElement) elements.get(1)).transferFeatures(elements.get(2),s); break;
            
                case 2: {

                    elements.get(3).addToIndex(instances.filterByPropertyForInstances(elements.get(2).getActiveEntries(), LexicalEntry.SynArg.OBJECT));  
                    break;
                }
            }
        }        
        
        @Override
	public Set<String> buildSPARQLqueries() {
            
            Set<String> queries = new HashSet<>();
            
            PropertyElement noun = (PropertyElement) elements.get(2);
            IndividualElement instance = (IndividualElement) elements.get(3);
                 
            switch (currentElement) {
                
                // case 2: TODO
                
                case 3: {
                    for (LexicalEntry noun_entry : noun.getActiveEntries()) {

                         switch (noun_entry.getSemArg(LexicalEntry.SynArg.OBJECT)) {

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
                
                default: return new HashSet<>();
            }
	}
}
