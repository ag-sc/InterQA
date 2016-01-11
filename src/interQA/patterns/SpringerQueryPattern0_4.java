package interQA.patterns;

import java.util.ArrayList;

import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import interQA.lexicon.LiteralSource;
import interQA.lexicon.SparqlQueryBuilder;
import interQA.elements.*;
import java.util.List;
import java.util.Map;

public class SpringerQueryPattern0_4 extends QueryPattern{

	//Show me <Property:NounPossessiveFrame> <Literal> <Literal>
	//Show me the proceedings of ISWC 2015
    
//    SELECT DISTINCT ?uri WHERE {
//       ?uri hasConference ?conf . 
//       ?conf confAcronym "ISWC" .
//       ?conf confYear "2015" .
//}
//	
	public SpringerQueryPattern0_4(Lexicon lexicon,InstanceSource source,LiteralSource literals){
		
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
		element1.add("all");
                element1.add("the");
		elements.add(element1);
		
		PropertyElement element2 = new PropertyElement(lexicon,LexicalEntry.POS.NOUN,vocab.NounPossessiveFrame);
		elements.add(element2);
		
		LiteralElement element3 = new LiteralElement();
		elements.add(element3);
		
		LiteralElement element4 = new LiteralElement();
		elements.add(element4);
		
	}
	
	@Override
	public void updateAt(int i){
            		
                if(i==2){
		
                    elements.get(3).addToIndex(literals.getLiteralByProperty(elements.get(2).getActiveEntries(),LexicalEntry.SynArg.POSSESSIVEADJUNCT));
			
		}
		
		
	}
/*
	@Override
	public List<String> buildSPARQLqueries(){
		
		SparqlQueryBuilder sqb = new SparqlQueryBuilder();
		
	}*/
}
