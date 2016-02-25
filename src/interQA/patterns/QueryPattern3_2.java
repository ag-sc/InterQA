package interQA.patterns;

import interQA.elements.*;
import interQA.lexicon.*;
import java.util.*;


public class QueryPattern3_2 extends QueryPattern{
	
	//SELECT ?x ?y WHERE {?a rdf:type <Class>. ?a <propert1> ?x. ?a <property2> ?y. }

	
	//what are the NBA players` names and their heights?		
	//what are the BMW cars models and their prizes ?

	public QueryPattern3_2(Lexicon lexicon, InstanceSource instances){
		
		this.lexicon = lexicon;
		this.instances = instances;
		init();
	}
	
	@Override
	public void init(){
		
		StringElement element0 = new StringElement();
		element0.add("what");
		elements.add(element0);
		
		StringElement element1 = new StringElement();
		element1.add("are");
		elements.add(element1);
		
		StringElement element2 = new StringElement();
		element2.add("the");
		elements.add(element2);
		
		ClassElement element3 = new ClassElement();
                element3.addEntries(lexicon, LexicalEntry.POS.NOUN, null);
		elements.add(element3);
		
		PropertyElement element4 = new PropertyElement();
                element4.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.NounPossessiveFrame);
		elements.add(element4);
		
		StringElement element5 = new StringElement();
		element5.add("and");
		elements.add(element5);
		
		StringElement element6 = new StringElement();
		element6.add("their");
		elements.add(element6);
		
		PropertyElement element7 = new PropertyElement();
                element7.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.NounPossessiveFrame);
		elements.add(element7);	
		
	}
	
	
	@Override
	public void update(String s){
		
                if(currentElement==4){
			
			Map<String,List<LexicalEntry>> old_elementindex = elements.get(4).getIndex();
			Map<String,List<LexicalEntry>> new_elementindex  = new HashMap<>();
			
			for(LexicalEntry entry : elements.get(3).getActiveEntries()){
				new_elementindex.putAll(instances.filterByClassForProperty(old_elementindex,LexicalEntry.SynArg.OBJECT,entry.getReference()));
			}
			elements.get(4).setIndex(new_elementindex);
			
		}
		
		if(currentElement==6){
			Map<String,List<LexicalEntry>> old_elementindex = elements.get(7).getIndex();
			Map<String,List<LexicalEntry>> new_elementindex = new HashMap<>();
			
			for(LexicalEntry entry : elements.get(3).getActiveEntries()){
				
				new_elementindex.putAll(instances.filterByClassForProperty(old_elementindex,LexicalEntry.SynArg.OBJECT,entry.getReference()));
			}
			
			//to ensure second suggestion(property) is different from previous property suggestion
			if(!new_elementindex.equals(elements.get(4).getActiveEntries())) elements.get(7).setIndex(new_elementindex); 
		}
	}
	
	@Override
	public Set<String> buildSPARQLqueries(){
				
		ClassElement nounclass = (ClassElement) elements.get(3);
		PropertyElement prop_elemenet1 = (PropertyElement) elements.get(4);
		PropertyElement prop_element2 = (PropertyElement) elements.get(7);
		
                if (currentElement == 7) {
                    return sqb.BuildQueryForClassAnd2Properties(nounclass, prop_element2, prop_element2, LexicalEntry.SynArg.OBJECT, LexicalEntry.SynArg.OBJECT);
                }
                
                return new HashSet<>();
	}
	
}
