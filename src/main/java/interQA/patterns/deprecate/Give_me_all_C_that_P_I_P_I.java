package interQA.patterns.deprecate;

import interQA.patterns.templates.QueryPattern;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interQA.elements.ClassElement;
import interQA.elements.InstanceElement;
import interQA.elements.PropertyElement;
import interQA.elements.StringElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.LexicalEntry.Feature;
import interQA.lexicon.Lexicon;
import java.util.HashSet;
import java.util.Set;

public class Give_me_all_C_that_P_I_P_I extends QueryPattern{
	
    
	/*
        SELECT DISTINCT ?uri { ?uri rdf:type <Class> . ?uri <Property> <Literal> . ?uri <Property> <Literal> . }
     
        Show me all <Class:Noun> that <Property:Verb> <Literal> <Property:Preposition> <Literal> .
	
	  Show me all conferences that took place in Berlin in 2015.	
        */

	/*
	public Give_me_all_C_that_P_I_P_I(Lexicon lexicon,DatasetConnector instances){
		
		this.lexicon = lexicon;
		this.dataset = instances;
		
		
		init();
	}

	@Override
	public void init(){
		
		elements = new ArrayList<>();
		
		StringElement element0 = new StringElement();
		element0.add("show me");
        element0.add("give me");
        elements.add(element0);
        
        StringElement element1 = new StringElement();
        element1.add("all",Feature.PLURAL);
        element1.add("the");
        elements.add(element1);

        ClassElement element2 =  new ClassElement();
        element2.addEntries(lexicon, LexicalEntry.POS.NOUN, null);
        elements.add(element2);

        StringElement element3 = new StringElement();
        element3.add("that");
        elements.add(element3);

        PropertyElement element4 = new PropertyElement();
        element4.addEntries(lexicon, LexicalEntry.POS.VERB, vocab.TransitiveFrame);
        element4.addEntries(lexicon, LexicalEntry.POS.VERB, vocab.IntransitivePPFrame);
        elements.add(element4);

		InstanceElement element5 = new InstanceElement();
		elements.add(element5);
		
		PropertyElement element6 = new PropertyElement();
		element6.addEntries(lexicon, LexicalEntry.POS.PREPOSITION, vocab.PrepositionalFrame);
		elements.add(element6);

		InstanceElement element7 = new InstanceElement();
		elements.add(element7);
	}
	
	@Override
	public void update(String s){
		
                switch (currentElement) {
                    
                    case 1: ((StringElement) elements.get(1)).transferFeatures(elements.get(2),s); break;
                
                    case 2: setFeatures(2,4,s); break;
                            
                    case 3: {
			
			Map<String,List<LexicalEntry>> old_element2index = elements.get(2).getIndex();
                        Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
    		
                        for(LexicalEntry entry : elements.get(2).getActiveEntries()){
    			
                            new_element2index.putAll(dataset.filterByClassForProperty(old_element2index, LexicalEntry.SynArg.SUBJECT, entry.getReference()));
                        }
			
                        elements.get(4).addToIndex(new_element2index);
			break;
                    }
		
                    case 4: {
			
                    	elements.get(5).addToIndex(dataset.filterByPropertyForInstances(elements.get(4).getActiveEntries(), LexicalEntry.SynArg.OBJECT));
			break;
                    }
		
                    case 6: {

                    	elements.get(7).addToIndex(dataset.filterBy2PropertiesAndInstanceForInstances(elements.get(4).getActiveEntries(), elements.get(5).getActiveEntries(), 
                    			elements.get(6).getActiveEntries(), LexicalEntry.SynArg.OBJECT));
			break;
                    }
		}
	}
        
	@Override
	public Set<String> buildSPARQLqueries(){
				
		
		ClassElement    noun     = (ClassElement)    elements.get(2);
		PropertyElement verb     = (PropertyElement) elements.get(4);
        InstanceElement  individual  = (InstanceElement)  elements.get(5);
        PropertyElement verb2    = (PropertyElement) elements.get(6);
        InstanceElement  individual2 = (InstanceElement)  elements.get(7);
		
                switch (currentElement) {
                    
                	case 2 : return sqb.BuildQueryForClassInstances(elements.get(4).getActiveEntries());
                    
                	case 4 : return sqb.BuildQueryForClassAndProperty(noun, verb, LexicalEntry.SynArg.SUBJECT);  
                    
                    
                    case 6: return sqb.BuildQueryForClassAnd2PropertyAndIndividual(noun, verb, individual, LexicalEntry.SynArg.OBJECT,verb2, LexicalEntry.SynArg.OBJECT);
                    
                  	case 7: return sqb.BuildQueryForClassAnd2PropertyAnd2Individual(noun,verb,individual,LexicalEntry.SynArg.OBJECT,verb2,individual2
                  			,LexicalEntry.SynArg.OBJECT);
                        
                    default: return new HashSet<>();
                }
	}*/
}
