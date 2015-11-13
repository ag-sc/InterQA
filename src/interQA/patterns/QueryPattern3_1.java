package interQA.patterns;

import interQA.elements.*;
import interQA.lexicon.*;
import java.util.*;
/**
*
* @author mertince
*/

public class QueryPattern3_1 extends QueryPattern{
	//SELECT ?x ?y WHERE {?a rdf:type <Class>. ?a <propert1> ?x. ?a <property2> ?y. }
	//What is|are the <NounPFrame:Property> and <NounPFrame:Property> of <Noun:Class>
	//TODO:mince ADD ONE MORE PATTERN FOR <Noun:Class> <NounPosssesive> Case
	
	//What is the height and weight of nba players_?
	//What is the model and price of BMW cars?
	//What are the side effects and harms of Alzheimer drugs?
	
	InstanceSource instances;
	
	public QueryPattern3_1(Lexicon lexicon,InstanceSource instances){
		
		this.instances = instances;
		
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
		
		ConceptElement element3 = new ConceptElement(lexicon,LexicalEntry.POS.NOUN,vocab.NounPossessiveFrame);
		elements.add(element3);
		
		StringElement element4 = new StringElement();
		element4.add("and");
		elements.add(element4);
		
		ConceptElement element5 = new ConceptElement(lexicon,LexicalEntry.POS.NOUN,vocab.NounPossessiveFrame);
		elements.add(element5);
		
		StringElement element6 = new StringElement();
		element6.add("of");
		elements.add(element6);
		
		ConceptElement element7 = new ConceptElement(lexicon,LexicalEntry.POS.NOUN,null);
		elements.add(element7);
		
	}
		@Override
		public void updateAt(int i) {
		
			if (i==4){
				Map<String,List<LexicalEntry>> old_element2index = elements.get(5).getIndex();
		           Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
		                
		            for (LexicalEntry entry1 : elements.get(4).getActiveEntries()) {
		                                    
		                new_element2index.putAll(instances.filterBy(old_element2index,LexicalEntry.SynArg.SUBJECT,entry1.getReference()));   
		            }
		            elements.get(5).setIndex(new_element2index);
			}
			if (i==6){
				Map<String,List<LexicalEntry>> old_element2index = elements.get(7).getIndex();
		           Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
		                
		            for (LexicalEntry entry1 : elements.get(5).getActiveEntries()) {
		                                    
		                new_element2index.putAll(instances.filterBy(old_element2index,LexicalEntry.SynArg.SUBJECT,entry1.getReference()));   
		            }
		            elements.get(7).setIndex(new_element2index);
			}
		}
		//SELECT ?x ?y WHERE {?a rdf:type <Class>. ?a <propert1> ?x. ?a <property2> ?y. }
		@Override
		public List<String> buildSPARQLqueries(){
			List<String> queries = new ArrayList<>();
			
			ConceptElement nounpos1 = (ConceptElement) elements.get(3);
			ConceptElement nounpos2 = (ConceptElement) elements.get(5);
			ConceptElement nounclass = (ConceptElement) elements.get(7);
			
			for(LexicalEntry nounpos1_entry: nounpos1.getActiveEntries()){
				for(LexicalEntry nounpos2_entry: nounpos2.getActiveEntries()){
					for(LexicalEntry nounclass_entry: nounclass.getActiveEntries()){
						queries.add("SELECT DISTINCT ?x ?y {"
								+ " ?a  <" + vocab.rdfType + ">  <" + nounclass_entry.getReference() + "> ."
								+ " ?a  <" + nounpos1_entry.getReference() + "> ?x . "
								+ " ?a  <" + nounpos2_entry.getReference() + "> ?y . }");
					}
				}
				
			}
			
			
			return queries;
			
		}
		
		
	
	

}
