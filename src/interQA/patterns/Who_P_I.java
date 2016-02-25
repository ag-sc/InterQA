package interQA.patterns;

import interQA.elements.IndividualElement;
import interQA.elements.StringElement;
import interQA.elements.PropertyElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author cunger
 */
public class Who_P_I extends QueryPattern {
    
    
        /*
        SELECT ?x WHERE { ?x <Property> <Individual> . } 
        SELECT ?x WHERE { <Individual> <Property> ?x . } 
    
        Who|What <IntransitivePPVerb:Property> <Name:Individual>?
    
	  Who died in Berlin?
          Who co-starred with Audrey Hepburn?
        */
    

	public Who_P_I(Lexicon lexicon, InstanceSource instances) {
            
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
		
            PropertyElement element1 = new PropertyElement(); 
            element1.addEntries(lexicon, LexicalEntry.POS.VERB, vocab.TransitiveFrame);
            element1.addEntries(lexicon, LexicalEntry.POS.VERB, vocab.IntransitivePPFrame);
            elements.add(element1);
            
            IndividualElement element2 = new IndividualElement(); 
            elements.add(element2);
	}

        @Override
        public void update(String s) {
            
            if (currentElement == 2) {
                  
            	elements.get(2).addToIndex(instances.filterByPropertyForInstances(elements.get(1).getActiveEntries(), LexicalEntry.SynArg.OBJECT));    
            }
        }        
        
        @Override
	public Set<String> buildSPARQLqueries() {
                        
            PropertyElement   verb     = (PropertyElement)   elements.get(1);
            IndividualElement instance = (IndividualElement) elements.get(2);
                 
            switch (currentElement) {
                
                // case 1: TODO
                
                case 2: return sqb.BuildQueryForIndividualAndPropery(instance, verb, LexicalEntry.SynArg.OBJECT);
                    
                default: return new HashSet<>();
            }
        }
}
