/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interQA.patterns.templates;

import interQA.elements.ClassElement;
import interQA.elements.PropertyElement;
import interQA.elements.StringElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mirtik
 */
public class P_P_C extends QueryPattern{
	


    //SELECT ?x ?y WHERE {?a rdf:type <Class>. ?a <propert1> ?x. ?a <property2> ?y. }
	
	
	
	public P_P_C(Lexicon lexicon,DatasetConnector instances){
				            
            this.lexicon = lexicon;
            this.dataset = instances; 
            sqb.setEndpoint(dataset.getEndpoint());
            init();
        }
        
        @Override
        public void init() {
            
            StringElement element0 = new StringElement();
            elements.add(element0);
		
            PropertyElement element1 = new PropertyElement();
            elements.add(element1);
		
            StringElement element2 = new StringElement();
            elements.add(element2);
		
            PropertyElement element3 = new PropertyElement();
            elements.add(element3);
		
            StringElement element4 = new StringElement();
            elements.add(element4);
		
            ClassElement element5 = new ClassElement();
            elements.add(element5);
		
	}
        
		@Override
		public void update(String s) {
		
                    
                    switch (currentElement) {
                        
                        case 0: {
                    
                            checkHowMany(s);
                            ((StringElement) elements.get(0)).transferFeatures(elements.get(1),s); 
                            ((StringElement) elements.get(0)).transferFeatures(elements.get(3),s); 
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

                            for (LexicalEntry entry1 : elements.get(1).getActiveEntries()) {
                                 new_element2index.putAll(dataset.filterByPropertyForProperty(old_element2index,LexicalEntry.SynArg.SUBJECT,entry1.getReference()));   
                            }
                            elements.get(3).setIndex(new_element2index);
                            break;
                        }

                        case 3: {
                            
                            setFeatures(3,4,s);

                            for (String m : elements.get(3).getMarkers()) {
                                ((StringElement) elements.get(4)).add(m);
                            }

                            elements.get(5).addToIndex(dataset.filterBy2PropertiesForClasses(elements.get(1).getActiveEntries(),
                                                                                             elements.get(3).getActiveEntries(), 
                                                                                             LexicalEntry.SynArg.OBJECT,
                                                                                             LexicalEntry.SynArg.OBJECT));    
                        
                            break;
                        }
                        
                        case 4: {
                            
                            ((StringElement) elements.get(4)).transferFeatures(elements.get(5),s); 
                            break;
                        }
                    }
		
		}
		
		@Override
		public Set<String> buildSPARQLqueries(){
			
			PropertyElement p1 = (PropertyElement) elements.get(1);
			PropertyElement p2 = (PropertyElement) elements.get(3);
			ClassElement    c  = (ClassElement)    elements.get(5);
			
                        switch(currentElement){
                            case 5: {
                            return sqb.BuildQueryForClassAnd2Properties(c, p1, p2, LexicalEntry.SynArg.OBJECT, LexicalEntry.SynArg.OBJECT);
                        }
                        }
                        return new HashSet<>();
                
		}
		
		
	
	

}

