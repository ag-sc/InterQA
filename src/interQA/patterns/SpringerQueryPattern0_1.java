package interQA.patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interQA.elements.LiteralElement;
import interQA.elements.ClassElement;
import interQA.elements.IndividualElement;
import interQA.elements.PropertyElement;
import interQA.elements.StringElement;

import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import interQA.lexicon.LiteralSource;
import interQA.lexicon.SparqlQueryBuilder;



public class SpringerQueryPattern0_1 extends QueryPattern {
	
	
	
	//Which <Class:Noun> <Property:IntransitivePPframe> <Literal:gYear>
	
	//Which conferences took place in 2015?
	
	public SpringerQueryPattern0_1(Lexicon lexicon,InstanceSource instances,LiteralSource literals){
		
		this.lexicon = lexicon;
		this.instances = instances;
		this.literals = literals;
		
		init();
	}
	
	@Override
	public void init(){
		elements = new ArrayList<>();
		
		StringElement element0 = new StringElement();
		element0.add("which");
		elements.add(element0);
		
		ClassElement element1 = new ClassElement(lexicon,LexicalEntry.POS.NOUN,null);
		elements.add(element1);	
		
		PropertyElement element2 = new PropertyElement(lexicon,LexicalEntry.POS.VERB,vocab.IntransitivePPFrame);
		elements.add(element2);
		
		LiteralElement element3 = new LiteralElement();
		elements.add(element3);
	}
	
	@Override
	public void updateAt(int i){
		
		if(i==1){
			Map<String,List<LexicalEntry>> old_element2index = elements.get(1).getIndex();
            
    		Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
    		
    		
    		
    		for(LexicalEntry entry : elements.get(1).getActiveEntries()){
    			
    			new_element2index.putAll(instances.filterByClassForProperty(old_element2index, LexicalEntry.SynArg.SUBJECT, entry.getReference()));
    			
    		}
			elements.get(2).addToIndex(new_element2index);
		}
		
		if(i==2){
	
			elements.get(3).addToIndex(literals.getLiteralByProperty(elements.get(2).getActiveEntries()));
			
		}
	}
	
	@Override
	public List<String> buildSPARQLqueries(){
		
		SparqlQueryBuilder sqb = new SparqlQueryBuilder();
		
		ClassElement    noun = (ClassElement)    elements.get(1);
        PropertyElement    verb = (PropertyElement)    elements.get(2);
        LiteralElement literal = (LiteralElement) elements.get(3);
		
		
		
		return sqb.BuildQueryForClassAndPropertyAndLiteral(noun,verb,literal);
	}
	
	

}
