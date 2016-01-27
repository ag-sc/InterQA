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
import interQA.lexicon.LexicalEntry.Feature;
import interQA.lexicon.Lexicon;
import interQA.lexicon.LiteralSource;
import interQA.lexicon.SparqlQueryBuilder;

public class SpringerQueryPattern0_3_2 extends QueryPattern{
	
	//Show me all <Class:Noun> that <Property:IntransitivePPFrame> <Literal>
	
	//Show me all conferences that took place in Berlin in 2015.

	//SELECT DISTINCT ?uri { ?uri rdf:type Conference . ?uri confCity "Berlin" . ?uri confYear "2015" }
	
	//SELECT DISTINCT ?uri { ?uri rdf:type <Class>.     ?uri <Property> <Literal>. }
	
	public SpringerQueryPattern0_3_2(Lexicon lexicon,InstanceSource instances,LiteralSource literals){
		
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
                element0.add("give me");
		elements.add(element0);
                
                StringElement element1 = new StringElement();
		element1.add("all",Feature.PLURAL);
                element1.add("the");
		elements.add(element1);
		
		ClassElement element2 =  new ClassElement(lexicon,LexicalEntry.POS.NOUN,null);
		elements.add(element2);
		
		StringElement element3 = new StringElement();
		element3.add("that");
		elements.add(element3);
		
		PropertyElement element4 = new PropertyElement(lexicon,LexicalEntry.POS.VERB,vocab.IntransitivePPFrame);
		elements.add(element4);
		
		LiteralElement element5 = new LiteralElement();
		elements.add(element5);
		
		PropertyElement element6 = new PropertyElement(lexicon,LexicalEntry.POS.PREPOSITION,vocab.PrepositionalFrame);
		elements.add(element6);
		
		LiteralElement element7 = new LiteralElement();
		elements.add(element7);
                
                StringElement element8 = new StringElement();
                element8.add(".");
                elements.add(element8);
	}
	
	@Override
	public void updateAt(int i,String s){
		
                if (i==1) {
                    ((StringElement) elements.get(1)).transferFeatures(elements.get(2),s);
                }
                
                if (i==2) {
                    setFeatures(2,4,s);
                }
                            
		if(i==3){
			
			Map<String,List<LexicalEntry>> old_element2index = elements.get(2).getIndex();
            
    		Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
    		
    		
    		
    		for(LexicalEntry entry : elements.get(2).getActiveEntries()){
    			
    			new_element2index.putAll(instances.filterByClassForProperty(old_element2index, LexicalEntry.SynArg.SUBJECT, entry.getReference()));
    			
    		}
			elements.get(4).addToIndex(new_element2index);
			
		}
		
		if(i==4){
			
			elements.get(5).addToIndex(literals.getJustLiteralByProperty(elements.get(4).getActiveEntries()));
			
		}
		if(i==6){

			elements.get(7).addToIndex(literals.getLiteralByPropertyAndLiteral(elements.get(6).getActiveEntries(),elements.get(4).getActiveEntries(),
					elements.get(5).getActiveEntries()));
			
		}
		
	}
	@Override
	public List<String> buildSPARQLqueries(){
		
		SparqlQueryBuilder sqb = new SparqlQueryBuilder();
		
		ClassElement    noun = (ClassElement)    elements.get(2);
        PropertyElement    verb = (PropertyElement)    elements.get(4);
        LiteralElement literal = (LiteralElement) elements.get(5);
        PropertyElement verb2 = (PropertyElement) elements.get(6);
        LiteralElement literal2 = (LiteralElement)elements.get(7);
		
		
		
		return sqb.BuildQueryForClassAnd2PropertyAnd2Literal(noun,verb,literal,verb2,literal2);
	}
}
