/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interQA.patterns.templates;
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
	public void init() {
            
            sqb.setEndpoint(dataset.getEndpoint());
	
            StringElement element0 = new StringElement();
            elements.add(element0);

            ClassElement element1 =  new ClassElement();
            elements.add(element1);

            StringElement element2 = new StringElement();
            elements.add(element2);

            PropertyElement element3 = new PropertyElement();
            elements.add(element3);
            
            StringElement element4 = new StringElement();
            elements.add(element4);

            InstanceElement element5 = new InstanceElement();
            elements.add(element5);

            StringElement element6 = new StringElement();
            elements.add(element6);
            
            PropertyElement element7 = new PropertyElement();
            elements.add(element7);

            StringElement element8 = new StringElement();
            elements.add(element8);
            
            InstanceElement element9 = new InstanceElement();
            elements.add(element9);
	}
	
	@Override
	public void update(String s){
		
                switch (currentElement) {
                    
                    case 0: {
                        
                        checkHowMany(s);
                        ((StringElement) elements.get(0)).transferFeatures(elements.get(1),s);
                        break;
                    }
                
                    case 1: {
                        
                        setFeatures(1,2,s);
                        break;
                    } 
                            
                    case 2: {
                        
                        ((StringElement) elements.get(2)).transferFeatures(elements.get(3),s);
			
			Map<String,List<LexicalEntry>> old_element2index = elements.get(3).getIndex();
                        Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
    		
                        for(LexicalEntry entry : elements.get(1).getActiveEntries()){
    			
                            new_element2index.putAll(dataset.filterByClassForProperty(old_element2index, LexicalEntry.SynArg.SUBJECT, entry.getReference()));
                        }
			
                        elements.get(3).addToIndex(new_element2index);
			break;
                    }
		
                    case 3: {
                        
                        setFeatures(3,4,s);
                        
                    	elements.get(5).addToIndex(dataset.filterByPropertyForInstances(elements.get(3).getActiveEntries(), LexicalEntry.SynArg.OBJECT));
                        
                        for (String m : elements.get(3).getMarkers()) {
                            ((StringElement) elements.get(4)).add(m);
                        }
			break;
                    }
                    
                    case 4: {
                        
                        ((StringElement) elements.get(4)).transferFeatures(elements.get(5),s);
                        break;
                    }
                    
                    case 5: {
                        
                        setFeatures(5,6,s);
                        
                        for(String key :elements.get(3).getActiveEntriesKey()){
                            elements.get(5).getIndex().remove(key);
                        }
                        break;
                    }
                    
                    case 6: {
                        
                        ((StringElement) elements.get(6)).transferFeatures(elements.get(7),s);
                        break;
                    }
                    
                    case 7: {
                        
                        setFeatures(7,8,s);
                        
                    	elements.get(9).addToIndex(dataset.filterBy2PropertiesAndInstanceForInstances(elements.get(3).getActiveEntries(), 
                                                                                                      elements.get(5).getActiveEntries(), 
                    	                                                                              elements.get(7).getActiveEntries(), 
                                                                                                      LexicalEntry.SynArg.OBJECT));
                        
                        for (String m : elements.get(7).getMarkers()) {
                            ((StringElement) elements.get(8)).add(m);
                        }
			break;
                    }
                    
                    case 8: {
                        
                        ((StringElement) elements.get(8)).transferFeatures(elements.get(9),s);
                        break;
                    }
		}
	}
        
	@Override
	public Set<String> buildSPARQLqueries(){
				
		
		ClassElement    c  = (ClassElement)    elements.get(1);
		PropertyElement p1 = (PropertyElement) elements.get(3);
                InstanceElement i1 = (InstanceElement) elements.get(5);
                PropertyElement p2 = (PropertyElement) elements.get(7);
                InstanceElement i2 = (InstanceElement) elements.get(9);
		
                switch (currentElement) {
                    
                	case 1 : return sqb.BuildQueryForClassInstances(elements.get(1).getActiveEntries(),flag);
                    
                	case 3 : return sqb.BuildQueryForClassAndProperty(c,p1,LexicalEntry.SynArg.SUBJECT,flag);  
                    
                        case 5 : return sqb.BuildQueryForClassAndIndividualAndProperty(c,i1,p1,LexicalEntry.SynArg.SUBJECT,flag);
                    
                        case 7: return sqb.BuildQueryForClassAnd2PropertyAndIndividual(c,p1,i1,LexicalEntry.SynArg.OBJECT,p2,LexicalEntry.SynArg.OBJECT,flag);
                    
                  	case 9: return sqb.BuildQueryForClassAnd2PropertyAnd2Individual(c,p1,i1,LexicalEntry.SynArg.OBJECT,p2,i2,LexicalEntry.SynArg.OBJECT,flag);
                        
                    default: return new HashSet<>();
                }
	}
}
