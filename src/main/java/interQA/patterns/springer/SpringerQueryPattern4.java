package interQA.patterns.springer;

import interQA.patterns.templates.QueryPattern;
import interQA.elements.StringElement;
import interQA.elements.PropertyElement;
import interQA.elements.InstanceElement;
import java.util.HashSet;

import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import java.util.Set;

public class SpringerQueryPattern4 extends QueryPattern{


        /*
        SELECT ?x WHERE { ?x <Property> <Literal> . ?x <Property <Literal> . } 

        List|(Give|Show me) all|the <NounPP:Property> <Literal> <Literal>.

           
        */
    
    
	public SpringerQueryPattern4(Lexicon lexicon,DatasetConnector instances){
		
		this.lexicon = lexicon;
		this.dataset = instances;
		sqb.setEndpoint(dataset.getEndpoint());
		
		init();
	}
	
	@Override
	public void init(){
		
		StringElement element0 = new StringElement();
		elements.add(element0);
                
		PropertyElement element1 = new PropertyElement();
		elements.add(element1);
                
                StringElement element2 = new StringElement();
		elements.add(element2);
		
		InstanceElement element3 = new InstanceElement();
		elements.add(element3);
		
		InstanceElement element4 = new InstanceElement();
		elements.add(element4);
                
                
		
	}
	
	@Override
	public void update(String s){
            	
            
            switch(currentElement){
                
                case 0: ((StringElement) elements.get(0)).transferFeatures(elements.get(1),s); break;
                
                case 1: { 
                    
                    elements.get(3).addToIndex(dataset.filterByPropertyForInstances(elements.get(1).getActiveEntries(), LexicalEntry.SynArg.OBJECT));
                    
                    for (String m : elements.get(1).getMarkers()) {
                        ((StringElement) elements.get(2)).add(m);
                    }
                    break;
                } 

                case 3: elements.get(4).addToIndex(dataset.filterByInstanceForInstance(elements.get(3).getActiveEntries())); break;

            }
	}

	@Override
	public Set<String> buildSPARQLqueries(){
		
		Set<String> SPARQLQueries = new HashSet<>();

		
		
		PropertyElement prop_element  = (PropertyElement) elements.get(1);
		InstanceElement name_literal  = (InstanceElement) elements.get(3);
		InstanceElement gYear_literal = (InstanceElement) elements.get(4);
		switch(currentElement){
                    
			case 1:SPARQLQueries = sqb.BuildQueryForProperty(prop_element);break;
			
			case 3:SPARQLQueries = sqb.BuildQueryForPropertyAndInstance(prop_element, name_literal,count);break;
				
			case 4:SPARQLQueries = sqb.BuildQueryForPropertyAndgYearAndNameLiteral(prop_element, gYear_literal, name_literal, LexicalEntry.SynArg.OBJECT);
                           break;
                        
			}
			
		
		return SPARQLQueries;
		
	}
}
