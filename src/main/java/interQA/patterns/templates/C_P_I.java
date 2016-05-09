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
import java.util.Set;


public class C_P_I extends QueryPattern{

    
	/*
        SELECT DISTINCT ?uri { ?uri rdf:type <Class> . ?uri <Property> <Instance> . ?uri <Property> <Instance> . }
        */
        
	public C_P_I(Lexicon lexicon,DatasetConnector dataset){
		
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
            
            StringElement element4 = new StringElement();
            elements.add(element4);
		
            InstanceElement element5 = new InstanceElement();
            elements.add(element5);    
            
            StringElement element6 = new StringElement();
            elements.add(element6);
	}
	
	@Override
	public void update(String s){
		
            switch (currentElement) {
                
                case 0: { 
                    checkHowMany(s);
                    ((StringElement) elements.get(0)).transferFeatures(elements.get(1),s);
                    break;
                } 
                case 2:((StringElement) elements.get(2)).transferFeatures(elements.get(3),s); break; 
                case 4: ((StringElement) elements.get(4)).transferFeatures(elements.get(5),s); break;
                //case 5: setFeatures(5,6,s); break;
                                
                case 1: {
                    setFeatures(1,2,s);
                    
                    Map<String,List<LexicalEntry>> old_element3index = elements.get(3).getIndex();
                    Map<String,List<LexicalEntry>> new_element3index = new HashMap<>();
    		
                    for (LexicalEntry entry : elements.get(1).getActiveEntries()) {
                        
    			new_element3index.putAll(dataset.filterByClassForProperty(old_element3index, LexicalEntry.SynArg.SUBJECT, entry.getReference()));
    			
                    }
                    elements.get(3).addToIndex(new_element3index);
                    
                    
                    break;
                }

                case 3: {
                    
	
                    elements.get(5).addToIndex(dataset.filterByPropertyForInstances(elements.get(3).getActiveEntries(), LexicalEntry.SynArg.OBJECT));
                    break;
                }
            }
	}
	
	
	@Override
	public Set<String> buildSPARQLqueries() {
			
            ClassElement    c = (ClassElement)    elements.get(1);
            PropertyElement p = (PropertyElement) elements.get(3);
            InstanceElement i = (InstanceElement) elements.get(5);
            
            switch (currentElement) {
                
            	case 1:return sqb.BuildQueryForClassInstances(c.getActiveEntries(),count);
                
                case 3:return sqb.BuildQueryForClassAndProperty(c,p,LexicalEntry.SynArg.SUBJECT,count);
                
                case 5:return sqb.BuildQueryForClassAndIndividualAndProperty(c,i,p,LexicalEntry.SynArg.OBJECT,count);
                
            }
            
            return queries;
	}
}
