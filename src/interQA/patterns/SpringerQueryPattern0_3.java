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

public class SpringerQueryPattern0_3 extends QueryPattern{
	
	//Show me all <Class:Noun> that <Property:IntransitivePPFrame> <Literal>
	
	//Show me all conferences that took place in Germany.
	//Show me all conferences that took place in 2015.

	//SELECT DISTINCT ?uri { ?uri rdf:type Conference . ?uri confCity "Berlin" . }
	
	//SELECT DISTINCT ?uri { ?uri rdf:type <Class>.     ?uri <Property> <Literal>. }
	
	public SpringerQueryPattern0_3(Lexicon lexicon,InstanceSource instances,LiteralSource literals){
		
		this.lexicon = lexicon;
		this.instances = instances;
		this.literals = literals;
		
		init();
	}

	@Override
	public void init(){
		
		elements = new ArrayList<>();
		
		StringElement element0 = new StringElement();
		element0.add("show me all");
		elements.add(element0);
		
		ClassElement element1 =  new ClassElement(lexicon,LexicalEntry.POS.NOUN,null);
		elements.add(element1);
		
		StringElement element2 = new StringElement();
		element2.add("that");
		elements.add(element2);
		
		PropertyElement element3 = new PropertyElement(lexicon,LexicalEntry.POS.VERB,vocab.IntransitivePPFrame);
		elements.add(element3);
		
		LiteralElement element4 = new LiteralElement();
		elements.add(element4);
		
		
	}
	
	@Override
	public void updateAt(int i){
		
		if(i==2){
			
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
        PropertyElement    verb = (PropertyElement)    elements.get(3);
        LiteralElement literal = (LiteralElement) elements.get(4);
		
		
		
		return sqb.BuildQueryForClassAndPropertyAndLiteral(noun,verb,literal);
	}
}
