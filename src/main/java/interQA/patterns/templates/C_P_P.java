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
		
		PropertyElement element2 = new PropertyElement();
		elements.add(element2);
		
		StringElement element3 = new StringElement();
		elements.add(element3);
		
		PropertyElement element4 = new PropertyElement();
		elements.add(element4);	
		
	}
	
	
	@Override
	public void update(String s){
		
            
                switch(currentElement){
                    
                    case 0:{checkHowMany(s);((StringElement) elements.get(0)).transferFeatures(elements.get(1),s);
                } break;
                    
                    case 2 : setFeatures(2,3,s);break;
                    
                    case 3 : ((StringElement) elements.get(3)).transferFeatures(elements.get(4),s);break;
                    
                    
                    
                    case 1:{
                        
                        setFeatures(1,2,s);
                        Map<String,List<LexicalEntry>> old_elementindex = elements.get(2).getIndex();
			Map<String,List<LexicalEntry>> new_elementindex  = new HashMap<>();
			
			for(LexicalEntry entry : elements.get(1).getActiveEntries()){
				new_elementindex.putAll(dataset.filterByClassForProperty(old_elementindex,LexicalEntry.SynArg.OBJECT,entry.getReference()));
			}
			elements.get(2).setIndex(new_elementindex);
                    }break;
                    
                    case 4 :{
                        
                        setFeatures(4,5,s);
                        Map<String,List<LexicalEntry>> old_elementindex = elements.get(5).getIndex();
			Map<String,List<LexicalEntry>> new_elementindex = new HashMap<>();
			
			for(LexicalEntry entry : elements.get(1).getActiveEntries()){
				
				new_elementindex.putAll(dataset.filterByClassForProperty(old_elementindex,LexicalEntry.SynArg.OBJECT,entry.getReference()));
			}
			
			//to ensure second suggestion(property) is different from previous property suggestion
			if(!new_elementindex.equals(elements.get(2).getActiveEntries())) elements.get(5).setIndex(new_elementindex); 
		
                        
                    }break;
                }
                
	}
	
	@Override
	public Set<String> buildSPARQLqueries(){
				
		ClassElement    c  = (ClassElement) elements.get(1);
		PropertyElement p1 = (PropertyElement) elements.get(2);
		PropertyElement p2 = (PropertyElement) elements.get(4);
		
                
                switch(currentElement){
                    
                    
                    case 2:{
                        return sqb.BuildQueryForClassAndProperty(c, p2, LexicalEntry.SynArg.OBJECT, count);
                    }
                    
                    case 4:{
                        return sqb.BuildQueryForClassAnd2Properties(c, p2, p2, LexicalEntry.SynArg.OBJECT, LexicalEntry.SynArg.OBJECT);

                    }
                }
                
                return new HashSet<>();
	}
	
}
