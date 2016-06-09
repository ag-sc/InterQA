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
    
    
        //SELECT DISTINCT ?uri { ?uri rdf:type <Class> . ?uri <Property> <Literal> . ?uri <Property> <Literal> . }

    
    boolean flag = false;
    
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
                    
                    case 0: {checkHowMany(s);((StringElement) elements.get(0)).transferFeatures(elements.get(1),s);
                } break;
                
                    case 1:setFeatures(1,3,s); break;
                            
                    case 2: {
			
			Map<String,List<LexicalEntry>> old_element2index = elements.get(3).getIndex();
                        Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
    		
                        for(LexicalEntry entry : elements.get(1).getActiveEntries()){
    			
                            new_element2index.putAll(dataset.filterByClassForProperty(old_element2index, LexicalEntry.SynArg.SUBJECT, entry.getReference()));
                        }
			
                        elements.get(3).addToIndex(new_element2index);
			break;
                    }
		
                    case 3: {
                        setFeatures(3,5,s);
                    	elements.get(4).addToIndex(dataset.filterByPropertyForInstances(elements.get(3).getActiveEntries(), LexicalEntry.SynArg.OBJECT));
			break;
                    }
                    case 4:{
                        for(String key :elements.get(3).getActiveEntriesKey()){
                            elements.get(5).getIndex().remove(key);
                        }
                        
                        
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
				
		
		ClassElement    c     = (ClassElement)    elements.get(1);
		PropertyElement p     = (PropertyElement) elements.get(3);
                InstanceElement  ind  = (InstanceElement)  elements.get(4);
                PropertyElement p2    = (PropertyElement) elements.get(5);
                InstanceElement  ind2 = (InstanceElement)  elements.get(6);
		
                switch (currentElement) {
                    
                	case 1 : return sqb.BuildQueryForClassInstances(elements.get(1).getActiveEntries(),flag);
                    
                	case 3 : return sqb.BuildQueryForClassAndProperty(c, p, LexicalEntry.SynArg.SUBJECT,flag);  
                    
                        case 4 : return sqb.BuildQueryForClassAndIndividualAndProperty(c, ind, p, LexicalEntry.SynArg.SUBJECT, flag);
                    
                        case 5: return sqb.BuildQueryForClassAnd2PropertyAndIndividual(c, p, ind, LexicalEntry.SynArg.OBJECT,p2, LexicalEntry.SynArg.OBJECT,flag);
                    
                  	case 6: return sqb.BuildQueryForClassAnd2PropertyAnd2Individual(c,p,ind,LexicalEntry.SynArg.OBJECT,p2,ind2
                  			,LexicalEntry.SynArg.OBJECT,flag);
                        
                    default: return new HashSet<>();
                }
	}
}
