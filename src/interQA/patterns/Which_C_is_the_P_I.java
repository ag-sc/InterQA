 package interQA.patterns;

import interQA.elements.*;
import interQA.lexicon.*;
import interQA.lexicon.LexicalEntry.Feature;
import java.util.*;
/**
 *
 * @author cunger
 */
public class Which_C_is_the_P_I extends QueryPattern {
    
    
        /*
        SELECT ?x WHERE { ?x rdf:type <Class> . ?x <Property> <Individual> . } 
        SELECT ?x WHERE { ?x rdf:type <Class> . <Individual> <Property> ?y . }

        Which <Noun:Class> is (a|the) <NounPP:Property> <Name:Individual>?

          Which city is the capital of Australia?
	*/

    
	public Which_C_is_the_P_I(Lexicon lexicon,InstanceSource instances){
                                    
            this.lexicon = lexicon;
            this.instances = instances; 
            
            init();
        }
        
        @Override
        public void init() {
            
            elements = new ArrayList<>();
				
            StringElement element0= new StringElement();
            element0.add("which");
            elements.add(element0);
		
            ClassElement element1 = new ClassElement();
            element1.addEntries(lexicon, LexicalEntry.POS.NOUN, null);
            elements.add(element1);
		
            StringElement element2 = new StringElement();
            element2.add("is",Feature.SINGULAR);
            elements.add(element2);
		
            StringElement element3 = new StringElement();
            element3.add("a",Feature.SINGULAR);
            element3.add("the");
            elements.add(element3);
		
            PropertyElement element4 = new PropertyElement();
            element4.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.NounPPFrame);
            element4.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.NounPossessiveFrame);
            elements.add(element4);
		
            IndividualElement element5 = new IndividualElement();
            elements.add(element5);	
	}
    
    @Override
    public void update(String s){
    	    	
        switch (currentElement) {
            
            case 1: {
    	
                setFeatures(1,2,s);
                setFeatures(1,3,s);

                Map<String,List<LexicalEntry>> old_element2index = elements.get(4).getIndex();
                Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();

                for (LexicalEntry entry1 : elements.get(1).getActiveEntries()) {

                    new_element2index.putAll(instances.filterByClassForProperty(old_element2index,LexicalEntry.SynArg.SUBJECT,entry1.getReference()));   
                }
                elements.get(4).addToIndex(new_element2index);
                break;
            }
        
            case 4: {
    		
                elements.get(5).addToIndex(instances.filterByPropertyForInstances(elements.get(4).getActiveEntries(), LexicalEntry.SynArg.OBJECT));
                break;
            }
        }
  }
    
    @Override
    public Set<String> buildSPARQLqueries(){
    	    	
    	ClassElement noun = (ClassElement) elements.get(1);
    	PropertyElement nounprop = (PropertyElement) elements.get(4);
    	IndividualElement indi = (IndividualElement) elements.get(5);
    	 
        switch (currentElement) {
            
            // case 1: TODO
            
            // case 4: TODO
    	
            case 5: return sqb.BuildQueryForIndividualAndProperty(noun, indi, nounprop,LexicalEntry.SynArg.OBJECT);
                
            default: return new HashSet<>();
        }
    }
    
}

