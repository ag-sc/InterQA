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
            element4.add("and");
            elements.add(element4);
		
            PropertyElement element5 = new PropertyElement(lexicon,LexicalEntry.POS.NOUN,vocab.NounPossessiveFrame);
            elements.add(element5);
		
            StringElement element6 = new StringElement();
            element6.add("of");
            elements.add(element6);
		
            ClassElement element7 = new ClassElement(lexicon,LexicalEntry.POS.NOUN,null);
            elements.add(element7);
		
	}
        //TODO:mince move updateAt another class (on progress)
		@Override
		public void updateAt(int i) {
		
			if (i==4){
				Map<String,List<LexicalEntry>> old_element2index = elements.get(5).getIndex();
		           Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
		                
		            for (LexicalEntry entry1 : elements.get(4).getActiveEntries()) {
		            	                     
		                new_element2index.putAll(instances.filterByPropertyForProperty(old_element2index,LexicalEntry.SynArg.SUBJECT,entry1.getReference()));   
		            }
		            elements.get(5).setIndex(new_element2index);
			}
			if (i==6){
				Map<String,List<LexicalEntry>> old_element2index = elements.get(7).getIndex();
		           Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
		              
		            for(LexicalEntry entry1 :elements.get(4).getActiveEntries()){
		            for (LexicalEntry entry2 : elements.get(5).getActiveEntries()) {
		                               
		            	 String query; 

		                    
		                    switch (entry2.getSemArg(LexicalEntry.SynArg.DIRECTOBJECT)) {
		                        
		                         case SUBJOFPROP:
		                        	 
		                        	 //TODO mince: The position of instance may vary depending on property (think on all cases) !!
		                              query = "SELECT DISTINCT ?x ?label WHERE { "
		                                    + " ?x <" + entry1.getReference() + "> ?object . "
		                                    + " ?x <" + entry2.getReference()+"> ?object"
		                                    + " ?x <" + vocab.rdfs + "label> ?l . }";
		                              elements.get(7).addToIndex(instances.getInstanceIndex(query,"x","l"));
		                         case OBJOFPROP:
		                              query = "SELECT DISTINCT ?x ?label WHERE { "
		                                    + " ?subject <"+entry1.getReference()+"> ?x ."
		                                    + " ?subject <" + entry2.getReference() + "> ?x . "
		                                    + " ?x <" + vocab.rdfs + "label> ?l . }";
		                              elements.get(7).addToIndex(instances.getInstanceIndex(query,"x","l"));
		                    }
		            	
		                 }
		            
			}
			}
			
		
		}
		//SELECT ?x ?y WHERE {?a rdf:type <Class>. ?a <propert1> ?x. ?a <property2> ?y. }
		@Override
		public List<String> buildSPARQLqueries(){
			List<String> queries = new ArrayList<>();
			
			PropertyElement nounpos1 = (PropertyElement) elements.get(3);
			PropertyElement nounpos2 = (PropertyElement) elements.get(5);
			ClassElement nounclass = (ClassElement) elements.get(7);
			
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
