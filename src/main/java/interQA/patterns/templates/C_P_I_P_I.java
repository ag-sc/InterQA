/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interQA.patterns.templates;
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
import interQA.lexicon.Lexicon;
import java.util.HashSet;
import java.util.Set;


/**
 *
 * @author mirtik
 */
public class C_P_I_P_I extends QueryPattern{
    
    public C_P_I_P_I(Lexicon lexicon,DatasetConnector dataset){
        	
            this.lexicon = lexicon;
            this.dataset = dataset;
		
            init();
    }
    
    @Override
	public void init(){
            
	StringElement element0 = new StringElement();
        elements.add(element0);

        ClassElement element1 =  new ClassElement();
        elements.add(element1);

        StringElement element2 = new StringElement();
        elements.add(element2);

        PropertyElement element3 = new PropertyElement();
        elements.add(element3);

	InstanceElement element4 = new InstanceElement();
	elements.add(element4);
		
	PropertyElement element5 = new PropertyElement();
	elements.add(element5);

	InstanceElement element6 = new InstanceElement();
	elements.add(element6);
	}
	
	@Override
	public void update(String s){
		
                switch (currentElement) {
                    
                    case 0: ((StringElement) elements.get(0)).transferFeatures(elements.get(0),s); break;
                
                    case 1: setFeatures(1,3,s); break;
                            
                    case 2: {
			
			Map<String,List<LexicalEntry>> old_element2index = elements.get(2).getIndex();
                        Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
    		
                        for(LexicalEntry entry : elements.get(1).getActiveEntries()){
    			
                            new_element2index.putAll(dataset.filterByClassForProperty(old_element2index, LexicalEntry.SynArg.SUBJECT, entry.getReference()));
                        }
			
                        elements.get(3).addToIndex(new_element2index);
			break;
                    }
		
                    case 3: {
			
                    	elements.get(4).addToIndex(dataset.filterByPropertyForInstances(elements.get(3).getActiveEntries(), LexicalEntry.SynArg.OBJECT));
			break;
                    }
		
                    case 5: {

                    	elements.get(6).addToIndex(dataset.filterBy2PropertiesAndInstanceForInstances(elements.get(3).getActiveEntries(), elements.get(4).getActiveEntries(), 
                    			elements.get(5).getActiveEntries(), LexicalEntry.SynArg.OBJECT));
			break;
                    }
		}
	}
        
	@Override
	public Set<String> buildSPARQLqueries(){
				
		
		ClassElement    noun     = (ClassElement)    elements.get(1);
		PropertyElement verb     = (PropertyElement) elements.get(3);
                InstanceElement  individual  = (InstanceElement)  elements.get(4);
                PropertyElement verb2    = (PropertyElement) elements.get(5);
                InstanceElement  individual2 = (InstanceElement)  elements.get(6);
		
                switch (currentElement) {
                    
                	case 2 : return sqb.BuildQueryForClassInstances(elements.get(3).getActiveEntries());
                    
                	case 4 : return sqb.BuildQueryForClassAndProperty(noun, verb, LexicalEntry.SynArg.SUBJECT);  
                    
                    
                    case 6: return sqb.BuildQueryForClassAnd2PropertyAndIndividual(noun, verb, individual, LexicalEntry.SynArg.OBJECT,verb2, LexicalEntry.SynArg.OBJECT);
                    
                  	case 7: return sqb.BuildQueryForClassAnd2PropertyAnd2Individual(noun,verb,individual,LexicalEntry.SynArg.OBJECT,verb2,individual2
                  			,LexicalEntry.SynArg.OBJECT);
                        
                    default: return new HashSet<>();
                }
	}
}
