package interQA.patterns.deprecate;

import interQA.patterns.templates.QueryPattern;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.Lexicon;
import interQA.lexicon.LexicalEntry;
import interQA.elements.ClassElement;
import interQA.elements.StringElement;
import interQA.elements.PropertyElement;
import java.util.*;
/**
*
* @author mince
*/

public class QueryPattern3_1 extends QueryPattern{
	//SELECT ?x ?y WHERE {?a rdf:type <Class>. ?a <propert1> ?x. ?a <property2> ?y. }
	//What is|are the <NounPFrame:Property> and <NounPFrame:Property> of <Noun:Class>
	//TODO:mince ADD ONE MORE PATTERN FOR <Noun:Class> <NounPosssesive> Case
	
	//What is the height and weight of nba players_?
	//What is the model and price of BMW cars?
	//What are the side effects and harms of Alzheimer drugs?
	
	DatasetConnector instances;
	/*
	public QueryPattern3_1(Lexicon lexicon,DatasetConnector instances){
				            
            this.lexicon = lexicon;
            this.instances = instances; 
            
            init();
        }
        
        @Override
        public void init() {
            
            elements = new ArrayList<>();
		
            StringElement element0 = new StringElement();
            element0.add("what");
            elements.add(element0);
		
            StringElement element1 = new StringElement();
            element1.add("is");
            element1.add("are");
            elements.add(element1);
		
            StringElement element2 = new StringElement();
            element2.add("the");
            elements.add(element2);
		
            PropertyElement element3 = new PropertyElement();
            element3.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.NounPossessiveFrame);
            elements.add(element3);
		
            StringElement element4 = new StringElement();
            element4.add("and");
            elements.add(element4);
		
            PropertyElement element5 = new PropertyElement();
            element5.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.NounPossessiveFrame);
            elements.add(element5);
		
            StringElement element6 = new StringElement();
            element6.add("of");
            elements.add(element6);
		
            ClassElement element7 = new ClassElement();
            element7.addEntries(lexicon, LexicalEntry.POS.NOUN, null);
            elements.add(element7);
		
	}
        
		@Override
		public void update(String s) {
		
			if (currentElement==4){
				Map<String,List<LexicalEntry>> old_element2index = elements.get(6).getIndex();
		           Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
		                
		            for (LexicalEntry entry1 : elements.get(4).getActiveEntries()) {
		            	                     
		                new_element2index.putAll(instances.filterByPropertyForProperty(old_element2index,LexicalEntry.SynArg.SUBJECT,entry1.getReference()));   
		            }
		            elements.get(6).setIndex(new_element2index);
			}
			if (currentElement==6){
				
				   elements.get(8).addToIndex(instances.filterBy2PropertiesForInstances(elements.get(4).getActiveEntries(),
						   elements.get(6).getActiveEntries(), LexicalEntry.SynArg.OBJECT));       
		            
				
			}
			
		
		}
		//SELECT ?x ?y WHERE {?a rdf:type <Class>. ?a <propert1> ?x. ?a <property2> ?y. }
		//TODO : mince instance position possibility for properties !!
		@Override
		public Set<String> buildSPARQLqueries(){
			
			PropertyElement nounpos1 = (PropertyElement) elements.get(4);
			PropertyElement nounpos2 = (PropertyElement) elements.get(6);
			ClassElement nounclass = (ClassElement) elements.get(8);
			
                        if (currentElement == 6) {
                            return sqb.BuildQueryForClassAnd2Properties(nounclass, nounpos1, nounpos2, LexicalEntry.SynArg.OBJECT, LexicalEntry.SynArg.OBJECT);
                        }
			
                        return new HashSet<>();
		}*/
		
		
	
	

}
