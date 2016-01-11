package interQA.patterns;

import java.util.ArrayList;

import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import interQA.lexicon.LiteralSource;
import interQA.lexicon.SparqlQueryBuilder;
import interQA.elements.*;

public class SpringerQueryPattern0_4 extends QueryPattern{

	//Show me <Property:NounPossessiveFrame> <Literal> <Literal>
	//Show me the proceedings of ISWC 2015
	
	public SpringerQueryPattern0_4(Lexicon lexicon,InstanceSource source,LiteralSource literal){
		
		this.lexicon = lexicon;
		this.instances = instances;
		this.literals = literals;
		
		init();
	}
	
	@Override
	public void init(){
		
		elements = new ArrayList<>();
		
		StringElement element0 = new StringElement();
		element0.add("show me");
		elements.add(element0);
		
		PropertyElement element1 = new PropertyElement(lexicon,LexicalEntry.POS.NOUN,vocab.NounPossessiveFrame);
		elements.add(element1);
		
		LiteralElement element2 = new LiteralElement();
		elements.add(element2);
		
		LiteralElement element3 = new LiteralElement();
		elements.add(element3);
		
	}
	
	@Override
	public void updateAt(int i){
		
		if(i==1){
			
			elements.get(2).addToIndex(literals.getLiteralByProperty(elements.get(1).getActiveEntries(),LexicalEntry.SynArg.POSSESSIVEADJUNCT));
			
		}
		
		
	}
/*
	@Override
	public List<String> buildSPARQLqueries(){
		
		SparqlQueryBuilder sqb = new SparqlQueryBuilder();
		
	}*/
}
