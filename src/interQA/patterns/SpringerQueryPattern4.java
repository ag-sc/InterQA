package interQA.patterns;

import java.util.ArrayList;
import java.util.HashSet;

import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import interQA.lexicon.SparqlQueryBuilder;
import interQA.elements.*;
import interQA.lexicon.LexicalEntry.Feature;
import java.util.Set;

public class SpringerQueryPattern4 extends QueryPattern{


        /*
        SELECT ?x WHERE { ?x <Property> <Literal> . ?x <Property <Literal> . } 

        List|(Give|Show me) all|the <NounPP:Property> <Literal> <Literal>.

          Give me the proceedings of ISWC 2015. 
        */
    
    
	public SpringerQueryPattern4(Lexicon lexicon,InstanceSource instances){
		
		this.lexicon = lexicon;
		this.instances = instances;
		
		
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
		
		IndividualElement element3 = new IndividualElement();
		elements.add(element3);
		
		IndividualElement element4 = new IndividualElement();
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
		
			elements.get(3).addToIndex(instances.filterByPropertyForInstances(elements.get(2).getActiveEntries(), LexicalEntry.SynArg.OBJECT));
		}
		if(currentElement==3){
			
		elements.get(4).addToIndex(instances.filterByInstanceForInstance(elements.get(3).getActiveEntries()));
		}
		
	}

	@Override
	public Set<String> buildSPARQLqueries(){
		
		Set<String> SPARQLQueries = new HashSet<>();

		SparqlQueryBuilder sqb = new SparqlQueryBuilder();
		
		PropertyElement prop_element = (PropertyElement) elements.get(2);
		IndividualElement name_literal = (IndividualElement) elements.get(3);
		IndividualElement gYear_literal = (IndividualElement) elements.get(4);
		switch(currentElement){
			case 2:SPARQLQueries = sqb.BuildQueryForProperty(prop_element);
			
			case 3:SPARQLQueries = sqb.BuildQueryForPropertyAndInstance(prop_element, name_literal);
				
			case 4:SPARQLQueries = sqb.BuildQueryForPropertyAndgYearAndNameLiteral(prop_element, gYear_literal, name_literal, LexicalEntry.SynArg.OBJECT);
			}
			
			
		
		if(currentElement==4) SPARQLQueries = sqb.BuildQueryForPropertyAndgYearAndNameLiteral(prop_element, gYear_literal, name_literal, LexicalEntry.SynArg.OBJECT);
		
		return SPARQLQueries;
		
	}
}
