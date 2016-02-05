package interQA.patterns;

import java.util.ArrayList;

import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import interQA.lexicon.LiteralSource;
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
    
    
	public SpringerQueryPattern4(Lexicon lexicon,InstanceSource source,LiteralSource literals){
		
		this.lexicon = lexicon;
		this.instances = instances;
		this.literals = literals;
		
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
		
		LiteralElement element3 = new LiteralElement();
		elements.add(element3);
		
		LiteralElement element4 = new LiteralElement();
		elements.add(element4);
                
                StringElement element5 = new StringElement();
                element5.add(".");
                elements.add(element5);
		
	}
	
	@Override
	public void updateAt(int i,String s){
            	
                if (i==1) {
                    ((StringElement) elements.get(1)).transferFeatures(elements.get(2),s);
                }
                
		if(i==2){
		
                    elements.get(3).addToIndex(literals.getLiteralByPropertyAndInstance(elements.get(2).getActiveEntries(),LexicalEntry.SynArg.OBJECT));
			
		}
		if(i==3){
			
                    elements.get(4).addToIndex(literals.getLiteralByLiteral(elements.get(3).getActiveEntries()));
		}
		
	}

	@Override
	public Set<String> buildSPARQLqueries(){
		
		SparqlQueryBuilder sqb = new SparqlQueryBuilder();
		
		PropertyElement prop_element = (PropertyElement) elements.get(2);
		LiteralElement name_literal = (LiteralElement) elements.get(3);
		LiteralElement gYear_literal = (LiteralElement) elements.get(4);
		
		return sqb.BuildQueryForPropertyAndgYearAndNameLiteral(prop_element, gYear_literal, name_literal, LexicalEntry.SynArg.OBJECT);
		
	}
}
