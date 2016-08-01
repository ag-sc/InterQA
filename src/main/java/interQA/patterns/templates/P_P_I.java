/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interQA.patterns.templates;

import interQA.elements.InstanceElement;
import interQA.elements.PropertyElement;
import interQA.elements.StringElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mirtik
 */
public class P_P_I extends QueryPattern{
	

        // SELECT ?x ?y WHERE { <instance> <property1> ?x . <instance> <property2> ?y. }
	
	
	public P_P_I(Lexicon lexicon,DatasetConnector instances){
				            
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
		
            InstanceElement element5 = new InstanceElement();
            elements.add(element5);
		
            StringElement element6 = new StringElement();
            elements.add(element6);
	}
        
		@Override
		public void update(String s) {
		
                    switch(currentElement) {
                        
                        case 0: {
                    
                            checkHowMany(s);
                            ((StringElement) elements.get(0)).transferFeatures(elements.get(1),s); 
                            ((StringElement) elements.get(0)).transferFeatures(elements.get(3),s); 
                            break;
                        }
                    
                        case 1: {
                           
                            Map<String,List<LexicalEntry>> old_index = elements.get(3).getIndex();
                            Map<String,List<LexicalEntry>> new_index = new HashMap<>();

                            for (LexicalEntry entry1 : elements.get(1).getActiveEntries()) {
                                 new_index.putAll(dataset.filterByPropertyForProperty(old_index,LexicalEntry.SynArg.SUBJECT,entry1.getReference()));   
                            }
                            elements.get(3).setIndex(new_index);
                            break;
                        }
                        
                        case 2: {
                            
                            ((StringElement) elements.get(2)).transferFeatures(elements.get(3),s); 
                            break;
                        }
                        
                        case 3: {

                            for (String m : elements.get(3).getMarkers()) {
                                ((StringElement) elements.get(4)).add(m);
                            }
                            
                            elements.get(5).addToIndex(dataset.filterBy2PropertiesForInstances(elements.get(1).getActiveEntries(),
                                                                                               elements.get(3).getActiveEntries(), 
                                                                                               LexicalEntry.SynArg.OBJECT,
                                                                                               LexicalEntry.SynArg.OBJECT));       
                            break;
                        }
                    }
		}
		
		@Override
		public Set<String> buildSPARQLqueries(){
            
                    // SELECT DISTINCT ?x ?y WHERE 
                    // {
                    //   <I> <P1> ?x .
                    //   <I> <P2> ?y . 
                    // }

                    String mainVar1 = "x";
                    String mainVar2 = "y";

                    PropertyElement p1 = (PropertyElement) elements.get(1);
                    PropertyElement p2 = (PropertyElement) elements.get(3);
                    InstanceElement i  = (InstanceElement) elements.get(5);

                    switch (currentElement) {

                        case 0: {

                            builder.reset();

                            builder.addProjVar(mainVar1);
                            builder.addProjVar(mainVar2);
                            break;
                        } 

                        case 1: { // + ?I <P1> ?x .
                            
                            builder.addTriple("I",p1.getActiveEntries(),mainVar1);
                            break;
                        }
                        
                        case 3: { // + ?I <P2> ?y .

                            builder.addTriple("I",p2.getActiveEntries(),mainVar2);
                            break;
                        }

                        case 5: { // ?I -> <I>

                            builder.instantiate("I",i.getActiveEntries());
                            break;
                        }
                    }

                    return builder.returnQueries();
                
		}
		
		
	
	

}

