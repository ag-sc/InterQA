package interQA.patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interQA.elements.ClassElement;
import interQA.elements.IndividualElement;
import interQA.elements.PropertyElement;
import interQA.elements.StringElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import java.util.HashSet;
import java.util.Set;

public class Which_C_P_I_P_I extends QueryPattern {
	
    
        /*
        SELECT DISTINCT ?uri { ?uri rdf:type <Class> . ?uri <Property> <Literal> . ?uri <Property> <Literal> . }
	
        Which <Class:Noun> <Property:Verb> <Literal> <Property:Preposition> <Literal> ?
	
	  Which conferences took place in 2015 in Berlin?
	*/
    
	
	public Which_C_P_I_P_I(Lexicon lexicon,InstanceSource instances){
	
		this.lexicon = lexicon;
		this.instances = instances;
		
		
		init();
	}

	public void init(){
		
		elements = new ArrayList<>();
		
		StringElement element0 = new StringElement();
		element0.add("which");
		elements.add(element0);
		
		ClassElement element1 = new ClassElement();
        element1.addEntries(lexicon, LexicalEntry.POS.NOUN, null);
		elements.add(element1);	
		
		PropertyElement element2 = new PropertyElement();
         element2.addEntries(lexicon, LexicalEntry.POS.VERB, vocab.TransitiveFrame);
         element2.addEntries(lexicon, LexicalEntry.POS.VERB, vocab.IntransitivePPFrame);
		elements.add(element2);
		
		IndividualElement element3 = new IndividualElement();
		elements.add(element3);
		
		PropertyElement element4 = new PropertyElement();
                element4.addEntries(lexicon, LexicalEntry.POS.PREPOSITION, vocab.PrepositionalFrame);
		elements.add(element4);
		
		IndividualElement element5 = new IndividualElement();
		elements.add(element5);
	}
	
	
	@Override
	public void update(String s){
		
            switch (currentElement) {
                
                case 1: {
                    
                    setFeatures(1,2,s);
                    
                    Map<String,List<LexicalEntry>> old_element2index = elements.get(1).getIndex();
       	            Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
    		
                    for(LexicalEntry entry : elements.get(1).getActiveEntries()){
    			
    			new_element2index.putAll(instances.filterByClassForProperty(old_element2index, LexicalEntry.SynArg.SUBJECT, entry.getReference()));
    			
                    }
                    elements.get(2).addToIndex(new_element2index);
                    break;
		}
		
		case 2: {
	
			elements.get(3).addToIndex(instances.filterByPropertyForInstances(elements.get(2).getActiveEntries(),LexicalEntry.SynArg.OBJECT));                    break;
		}
		
                case 4: {
			
                	elements.get(5).addToIndex(instances.filterBy2PropertiesAndInstanceForInstances(elements.get(2).getActiveEntries(),elements.get(3).getActiveEntries(),
        					elements.get(4).getActiveEntries(),LexicalEntry.SynArg.OBJECT));
                    break;
		}
            }
	}
        
	@Override
	public Set<String> buildSPARQLqueries(){
		
		
		ClassElement    noun = (ClassElement)    elements.get(1);
        PropertyElement    verb = (PropertyElement)    elements.get(2);
        IndividualElement literal = (IndividualElement) elements.get(3);
        PropertyElement verb2 = (PropertyElement) elements.get(4);
        IndividualElement literal2 = (IndividualElement)elements.get(5);

            switch (currentElement) {
                
            case 1: return sqb.BuildQueryForClassInstances(noun.getActiveEntries());
                
            case 2: return sqb.BuildQueryForClassAndProperty(noun,verb,LexicalEntry.SynArg.SUBJECT);
      
            case 4: return sqb.BuildQueryForClassAnd2PropertyAndIndividual(noun,verb,literal,LexicalEntry.SynArg.OBJECT,
            		verb2,LexicalEntry.SynArg.OBJECT);
            		
            case 5: return sqb.BuildQueryForClassAnd2PropertyAnd2Individual(noun,verb,literal,LexicalEntry.SynArg.OBJECT,
            		verb2,literal2,LexicalEntry.SynArg.OBJECT);
                    
                default: return new HashSet<>();
            }
	}
	
}
