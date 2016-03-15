package interQA.patterns;

import interQA.elements.StringElement;
import interQA.elements.PropertyElement;
import interQA.elements.IndividualElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interQA.lexicon.LexicalEntry.Feature;
import java.util.HashSet;
import java.util.Set;


public class SpringerQueryPattern5 extends QueryPattern{
	
	//Give me the <Property:Noun> and <Property:Noun> <Literal> <Literal>
	
	//Give me the start and end date of ISWC 2015.
	
	public SpringerQueryPattern5(Lexicon lexicon, InstanceSource instances ){
		
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
                element2.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.NounPPFrame);
                element2.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.NounPossessiveFrame);
		elements.add(element2);
		
		StringElement element3 = new StringElement();
		element3.add("and");
		elements.add(element3);
		
		PropertyElement element4 = new PropertyElement();
                element4.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.NounPPFrame);
                element4.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.NounPossessiveFrame);
		elements.add(element4);
		
		IndividualElement element5 = new IndividualElement();
		elements.add(element5);
		
		IndividualElement element6 = new IndividualElement();
		elements.add(element6);
		
	}
	
	@Override
	public void update(String s){
            
                if (currentElement==1) {
                    ((StringElement) elements.get(1)).transferFeatures(elements.get(2),s);
                }
		
		if (currentElement==3){
			
			Map<String,List<LexicalEntry>> old_element2index = elements.get(4).getIndex();
			Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
			
			for(LexicalEntry entry1 : elements.get(2).getActiveEntries()){
				new_element2index.putAll(instances.filterByPropertyForProperty(old_element2index,LexicalEntry.SynArg.OBJECT, entry1.getReference()));
				
			}
			elements.get(4).setIndex(new_element2index);
		}
		
		if (currentElement==4){
			
			elements.get(5).addToIndex(instances.filterByPropertyForInstances(elements.get(4).getActiveEntries(),LexicalEntry.SynArg.OBJECT));
			}
		if (currentElement==5){
			
			elements.get(6).addToIndex(instances.filterByInstanceForInstance(elements.get(5).getActiveEntries()));
		}
		
		
	}
	
	
	
	@Override
	public Set<String> buildSPARQLqueries(){
		
		PropertyElement noun_prop1 = (PropertyElement) elements.get(2);
		PropertyElement noun_prop2 = (PropertyElement) elements.get(4);
		IndividualElement literal1 = (IndividualElement) elements.get(5);
		IndividualElement literal2 = (IndividualElement) elements.get(6);
		
                if (currentElement == 6) {
                    return sqb.BuildQueryFor2PropertyAndNameLiteralAndGYearLiteral(noun_prop1,LexicalEntry.SynArg.SUBJECT,
            				noun_prop2,LexicalEntry.SynArg.SUBJECT, literal1, literal2);
                }
                
                return new HashSet<>();
	}

}