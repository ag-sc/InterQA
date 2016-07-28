package interQA.patterns.springer;

import interQA.patterns.templates.QueryPattern;
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




public class SpringerQueryPattern3 extends QueryPattern{
	
    
	/*
        SELECT DISTINCT ?uri { ?uri rdf:type <Class> . ?uri <Property> <Literal> . ?uri <Property> <Literal> . }
     
        Show me all <Class:Noun> that <Property:Verb> <Literal> <Property:Preposition> <Literal> .
	
	  	
        */

	
	public SpringerQueryPattern3(Lexicon lexicon,DatasetConnector instances){
		
		this.lexicon = lexicon;
		this.dataset = instances;
		sqb.setEndpoint(dataset.getEndpoint());
		
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
		
		PropertyElement element7 = new PropertyElement();
		elements.add(element7);

		InstanceElement element8 = new InstanceElement();
		elements.add(element8);
	}
	
	@Override
	public void update(String s){
		
                switch (currentElement) {
                    
                    //case 1: ((StringElement) elements.get(1)).transferFeatures(elements.get(2),s); break;
                
                    case 1: setFeatures(1,3,s); break;
                            
                    case 2: {
			
			Map<String,List<LexicalEntry>> old_element2index = elements.get(1).getIndex();
                        Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
    		
                        for(LexicalEntry entry : elements.get(1).getActiveEntries()){
    			
                            new_element2index.putAll(dataset.filterByClassForProperty(old_element2index, LexicalEntry.SynArg.SUBJECT, entry.getReference()));
                        }
			
                        elements.get(3).addToIndex(new_element2index);
			break;
                    }
		
                    case 3: {
			
                    	elements.get(7).addToIndex(dataset.filterByPropertyForInstances(elements.get(3).getActiveEntries(), LexicalEntry.SynArg.OBJECT));
                        
                        for (String m : elements.get(3).getMarkers()) {
                            ((StringElement) elements.get(4)).add(m);
                        }
			break;
                    }
		
                    case 7: {

                    	elements.get(6).addToIndex(dataset.filterBy2PropertiesAndInstanceForInstances(elements.get(3).getActiveEntries(), elements.get(7).getActiveEntries(), 
                    			elements.get(7).getActiveEntries(), LexicalEntry.SynArg.OBJECT));
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
                InstanceElement i2 = (InstanceElement) elements.get(8);
		
                switch (currentElement) {
                    
                	case 2: return sqb.BuildQueryForClassInstances(elements.get(3).getActiveEntries(),count);
                    
                	case 3: return sqb.BuildQueryForClassAndProperty(c,p1,LexicalEntry.SynArg.SUBJECT,count);  
                    
                        case 7: return sqb.BuildQueryForClassAnd2PropertyAndIndividual(c,p1,i1,LexicalEntry.SynArg.OBJECT,p2, LexicalEntry.SynArg.OBJECT,count);
                    
                  	case 8: return sqb.BuildQueryForClassAnd2PropertyAnd2Individual(c,p1,i1,LexicalEntry.SynArg.OBJECT,p2,i2,LexicalEntry.SynArg.OBJECT,count);
                        
                    default: return new HashSet<>();
                }
	}
}