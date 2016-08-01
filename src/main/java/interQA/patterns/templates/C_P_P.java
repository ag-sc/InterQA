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
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mirtik
 */
public class C_P_P extends QueryPattern{
	
	//SELECT ?x ?y WHERE {?a rdf:type <Class>. ?a <propert1> ?x. ?a <property2> ?y. }

	
	//what are the NBA players` names and their heights?		
	//what are the BMW cars models and their prizes ?
        
	public C_P_P(Lexicon lexicon, DatasetConnector instances){
		
		this.lexicon = lexicon;
		this.dataset = instances;
                sqb.setEndpoint(dataset.getEndpoint());
		init();
	}
	
	@Override
	public void init(){
		
		StringElement element0 = new StringElement();
		elements.add(element0);
		
		ClassElement element1 = new ClassElement();
		elements.add(element1);
                
                StringElement element2 = new StringElement();
		elements.add(element2);
		
		PropertyElement element3 = new PropertyElement();
		elements.add(element3);
		
		StringElement element4 = new StringElement();
		elements.add(element4);
		
		PropertyElement element5 = new PropertyElement();
		elements.add(element5);	
		
                StringElement element6 = new StringElement();
		elements.add(element6);
	}
	
	
	@Override
	public void update(String s){
		
            
                switch(currentElement){
                    
                    case 0: {
                        
                        checkHowMany(s);
                        ((StringElement) elements.get(0)).transferFeatures(elements.get(1),s);
                        break;
                    } 
                 
                    case 1: {
                        
                        setFeatures(1,2,s);
                        
                        Map<String,List<LexicalEntry>> old_index = elements.get(3).getIndex();
			Map<String,List<LexicalEntry>> new_index  = new HashMap<>();
			
			for(LexicalEntry entry : elements.get(1).getActiveEntries()){
				new_index.putAll(dataset.filterByClassForProperty(old_index,LexicalEntry.SynArg.OBJECT,entry.getReference()));
			}
			elements.get(3).setIndex(new_index);
                        
                        break;
                    }
                        
                    case 2: { 
                     
                        ((StringElement) elements.get(2)).transferFeatures(elements.get(3),s);
                        break;
                    }
                    
                    case 3: {
                        
                        setFeatures(3,4,s);
                        
                        Map<String,List<LexicalEntry>> old_index = elements.get(5).getIndex();
			Map<String,List<LexicalEntry>> new_index = new HashMap<>();
			
			for (LexicalEntry entry : elements.get(3).getActiveEntries()) {
			     new_index.putAll(dataset.filterByClassForProperty(old_index,LexicalEntry.SynArg.OBJECT,entry.getReference()));
			}
                        elements.get(5).setIndex(new_index);
					
                        break;
                    }
                }
                
	}
	
	@Override
	public Set<String> buildSPARQLqueries(){
				
            // SELECT DISTINCT ?x WHERE 
            // {
            //   ?x rdf:type <C> .
            //   ?x <P1> ?y .
            //   ?x <P2> ?z . 
            // }
            		
            String mainVar = "x";
            
            ClassElement    c  = (ClassElement)    elements.get(1);
            PropertyElement p1 = (PropertyElement) elements.get(2);
            PropertyElement p2 = (PropertyElement) elements.get(4);
		           
            switch (currentElement) {
                    
                case 0: {
                    
                    builder.reset();
                    
                    if (count) builder.addCountVar(mainVar); 
                    else       builder.addProjVar(mainVar);
                    break;
                } 

                case 1: { // + ?x rdf:type <C> .
                    
                    builder.addTypeTriple(mainVar,c.getActiveEntries());
                    break;
                }
                    
                case 3: { // + ?x <P1> ?y .
                    
                    builder.addTriple(mainVar,p1.getActiveEntries(),"y");
                    break;
                }
                    
                case 5: { // + ?x <P2> ?z .
                    
                    builder.addTriple(mainVar,p2.getActiveEntries(),"z");
                    break;
                }
                        
            }
            
            return builder.returnQueries();                
	}
	
}
