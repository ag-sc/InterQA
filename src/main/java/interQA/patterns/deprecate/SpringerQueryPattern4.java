package interQA.patterns.deprecate;

import interQA.patterns.templates.QueryPattern;
import interQA.elements.StringElement;
import interQA.elements.PropertyElement;
import interQA.elements.InstanceElement;
import java.util.ArrayList;
import java.util.HashSet;

import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import interQA.lexicon.SparqlQueryBuilder;
import interQA.lexicon.LexicalEntry.Feature;
import java.util.Set;

public class SpringerQueryPattern4 extends QueryPattern{


        /*
        SELECT ?x WHERE { ?x <Property> <Literal> . ?x <Property <Literal> . } 

        List|(Give|Show me) all|the <NounPP:Property> <Literal> <Literal>.

          Give me the proceedings of ISWC 2015. 
        */
    
    
	public SpringerQueryPattern4(Lexicon lexicon,DatasetConnector instances){
		
		this.lexicon = lexicon;
		this.dataset = instances;
		
		
		init();
	}
	
	@Override
	public void init(){
		
		elements = new ArrayList<>();
		
		StringElement element0 = new StringElement();
		element0.add("give me");
                element0.add("show me");
		elements.add(element0);
                
                StringElement element1 = new StringElement();
		element1.add("all",Feature.PLURAL);
                element1.add("the");
		elements.add(element1);
		
		PropertyElement element2 = new PropertyElement();
                element2.addEntries(lexicon,LexicalEntry.POS.NOUN,vocab.NounPossessiveFrame);
		elements.add(element2);
		
		InstanceElement element3 = new InstanceElement();
		elements.add(element3);
		
		InstanceElement element4 = new InstanceElement();
		elements.add(element4);
                
                StringElement element5 = new StringElement();
                element5.add(".");
                elements.add(element5);
		
	}
	
	@Override
	public void update(String s){
            	
                if (currentElement==1) {
                    ((StringElement) elements.get(1)).transferFeatures(elements.get(2),s);
                }
                
		if(currentElement==2){
		
			elements.get(3).addToIndex(dataset.filterByPropertyForInstances(elements.get(2).getActiveEntries(), LexicalEntry.SynArg.OBJECT));
		}
		if(currentElement==3){
			
		elements.get(4).addToIndex(dataset.filterByInstanceForInstance(elements.get(3).getActiveEntries()));
		}
		
	}

	@Override
	public Set<String> buildSPARQLqueries(){
		
		Set<String> SPARQLQueries = new HashSet<>();

		SparqlQueryBuilder sqb = new SparqlQueryBuilder();
		
		PropertyElement prop_element = (PropertyElement) elements.get(2);
		InstanceElement name_literal = (InstanceElement) elements.get(3);
		InstanceElement gYear_literal = (InstanceElement) elements.get(4);
		switch(currentElement){
			case 2:SPARQLQueries = sqb.BuildQueryForProperty(prop_element);
			
			case 3:SPARQLQueries = sqb.BuildQueryForPropertyAndInstance(prop_element, name_literal);
				
			case 4:SPARQLQueries = sqb.BuildQueryForPropertyAndgYearAndNameLiteral(prop_element, gYear_literal, name_literal, LexicalEntry.SynArg.OBJECT);
			}
			
			
		
		if(currentElement==4) SPARQLQueries = sqb.BuildQueryForPropertyAndgYearAndNameLiteral(prop_element, gYear_literal, name_literal, LexicalEntry.SynArg.OBJECT);
		
		return SPARQLQueries;
		
	}
}
