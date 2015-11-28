package interQA.patterns;


import interQA.elements.*;
import interQA.lexicon.*;
import java.util.*;
/**
*
* @author mince
*/
public class QueryPattern3_1_1 extends QueryPattern{

	//SELECT ?x ?y WHERE {?a rdf:type <Class>. ?a <propert1> ?x. }
		//What is|are the <NounPFrame:Property> of <Noun:Class>
		//TODO : mince give another name for this pattern
		
		//What is the height of nba players_?
		//What is the model of BMW cars?
		//What are the side effects of Alzheimer drugs?
		
		InstanceSource instances;
		
		public QueryPattern3_1_1(Lexicon lexicon,InstanceSource instances){
					            
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
			
	            PropertyElement element3 = new PropertyElement(lexicon,LexicalEntry.POS.NOUN,vocab.NounPossessiveFrame);
	            elements.add(element3);
			
	            StringElement element4 = new StringElement();
	            element4.add("of");
	            elements.add(element4);
			
	            ClassElement element5 = new ClassElement(lexicon,LexicalEntry.POS.NOUN,null);
	            elements.add(element5);
			
		}
	        
			@Override
			public void updateAt(int i) {
			
				if (i==4){
					
					   elements.get(6).addToIndex(instances.filterByPropertyForInstances(elements.get(4).getActiveEntries(),
							    LexicalEntry.SynArg.DIRECTOBJECT));       
			            
					
				}
				
			
			}
			//SELECT ?x WHERE {?a rdf:type <Class>. ?a <property> ?x. }
			@Override
			public List<String> buildSPARQLqueries(){
				
				SparqlQueryBuilder sqb = new SparqlQueryBuilder();
				
				PropertyElement nounpos1 = (PropertyElement) elements.get(4);
				ClassElement nounclass = (ClassElement) elements.get(6);
			
				
				return sqb.BuildQueryForClassAndProperty(nounclass, nounpos1, LexicalEntry.SynArg.DIRECTOBJECT);
				
			}
			
}
