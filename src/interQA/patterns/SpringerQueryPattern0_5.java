package interQA.patterns;

import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.LexicalEntry.SynArg;
import interQA.lexicon.Lexicon;
import interQA.lexicon.LiteralSource;
import interQA.lexicon.SparqlQueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interQA.elements.*;
public class SpringerQueryPattern0_5 extends QueryPattern{
	
	//Give me the <Property:Noun> and <Property:Noun> <Literal> <Literal>
	
	//Give me the start and end date of ISWC 2015.
	
	public SpringerQueryPattern0_5(Lexicon lexicon, InstanceSource instances,LiteralSource literals ){
		
		this.lexicon = lexicon;
		this.instances = instances;
		this.literals = literals;
		
		init();
	}
	
	@Override
	public void init(){
		
		elements = new ArrayList<>();
		
		StringElement element0 = new StringElement();
		element0.add("give me the");
		elements.add(element0);
		
		
		PropertyElement element1 = new PropertyElement(lexicon,LexicalEntry.POS.NOUN,vocab.NounPossessiveFrame);
		elements.add(element1);
		
		
		StringElement element2 = new StringElement();
		element2.add("and");
		elements.add(element2);
		
		
		PropertyElement element3 = new PropertyElement(lexicon,LexicalEntry.POS.NOUN,vocab.NounPossessiveFrame);
		elements.add(element3);
		
		
		LiteralElement element4 = new LiteralElement();
		elements.add(element4);
		
		
		LiteralElement element5 = new LiteralElement();
		elements.add(element5);
		
		
		
	}
	
	@Override
	public void updateAt(int i){
		
		if(i==2){
			
			Map<String,List<LexicalEntry>> old_element2index = elements.get(3).getIndex();
			Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
			
			for(LexicalEntry entry1 : elements.get(1).getActiveEntries()){
				new_element2index.putAll(instances.filterByPropertyForProperty(old_element2index,LexicalEntry.SynArg.COPULATIVEARG, entry1.getReference()));
				
			}
			elements.get(3).setIndex(new_element2index);;
		}
		
	}
	
	
	/*
	@Override
	public List<String> buildSPARQLqueries(){
		SparqlQueryBuilder sqb = new SparqlQueryBuilder();
		
		
	}*/

}
