package interQA.patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interQA.elements.ClassElement;
import interQA.elements.LiteralElement;
import interQA.elements.PropertyElement;
import interQA.elements.StringElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import interQA.lexicon.LiteralSource;
import interQA.lexicon.SparqlQueryBuilder;

public class SpringerQueryPattern0_2 extends QueryPattern{
	
	//Give me all <Class:Noun> that <Property:AdjectivePPFrame> <Literal> 
	//Give me all conferences that are held in Berlin.
	
	
	public SpringerQueryPattern0_2(Lexicon lexicon,InstanceSource instances,LiteralSource literal){
		
		this.lexicon = lexicon;
		this.instances = instances;
		init();
	}
	
	@Override
	public void init(){
		elements = new ArrayList<>();
		
		StringElement element0 = new StringElement();
		element0.add("give me all");
		elements.add(element0);
		
		ClassElement element1 = new ClassElement(lexicon,LexicalEntry.POS.NOUN,null);
		elements.add(element1);
		
		StringElement element2 = new StringElement();
		element2.add("that are");
		elements.add(element2);
		
		PropertyElement element3 = new PropertyElement(lexicon,LexicalEntry.POS.ADJECTIVE,vocab.AdjectivePPFrame);
		elements.add(element3);
		
		LiteralElement element4 = new LiteralElement();
		elements.add(element4);
	}
	
	@Override
	public void updateAt(int i){
		
		if(i==1){
			Map<String,List<LexicalEntry>> old_element2index = elements.get(1).getIndex();
            
    		Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
    		
    		
    		
    		for(LexicalEntry entry : elements.get(1).getActiveEntries()){
    			
    			new_element2index.putAll(instances.filterByClassForProperty(old_element2index, LexicalEntry.SynArg.SUBJECT, entry.getReference()));
    			
    		}
			elements.get(3).addToIndex(new_element2index);
		}
		
		if(i==3){
			
			elements.get(4).addToIndex(literals.getLiteralByProperty(elements.get(3).getActiveEntries(),LexicalEntry.SynArg.PREPOSITIONALOBJECT));
		}
		
	}
	
	@Override
	public List<String> buildSPARQLqueries(){
		
		SparqlQueryBuilder sqb = new SparqlQueryBuilder();
		
		ClassElement    noun = (ClassElement)    elements.get(1);
        PropertyElement    adjective = (PropertyElement)    elements.get(3);
        LiteralElement literal = (LiteralElement) elements.get(4);
		
		
		
		return sqb.BuildQueryForClassAndPropertyAndLiteral(noun,adjective,literal);
		
	}
}
