package interQA.patterns;

import interQA.elements.*;
import interQA.lexicon.*;
import java.util.*;
/**
 *
 * @author cunger
 */
public class QueryPattern0_3 extends QueryPattern {
    
    // SELECT ?x WHERE { ?x rdf:type <Class> . ?x <Property> <Individual> . } 
    // SELECT ?x WHERE { ?x rdf:type <Class> . <Individual> <Property> ?y . }
    
    // Which <Noun:Class> is (a|the) <NounPP:Property> <Name:Individual>?

    // Which city is the capital of Australia?
	
	
	public QueryPattern0_3(Lexicon lexicon,InstanceSource instances){
                                    
            this.lexicon = lexicon;
            this.instances = instances; 
            
            init();
        }
        
        @Override
        public void init() {
            
            elements = new ArrayList<>();
				
            StringElement element0= new StringElement();
            element0.add("which");
            elements.add(element0);
		
            ClassElement element1 = new ClassElement(lexicon,LexicalEntry.POS.NOUN,null);
            elements.add(element1);
		
            StringElement element2 = new StringElement();
            element2.add("is");
            elements.add(element2);
		
            StringElement element3 = new StringElement();
            element3.add("a");
            element3.add("the");
            elements.add(element3);
		
            PropertyElement element4 = new PropertyElement(lexicon,LexicalEntry.POS.NOUN,vocab.NounPPFrame);
            elements.add(element4);
		
            IndividualElement element5 = new IndividualElement();
            elements.add(element5);	
		
	}
    
    @Override
    public void updateAt(int i){
    	    	
    	if(i==1){
    		
    		Map<String,List<LexicalEntry>> old_element2index = elements.get(4).getIndex();
            
    		Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
                
            for (LexicalEntry entry1 : elements.get(1).getActiveEntries()) {
                                    
                new_element2index.putAll(instances.filterByClassForProperty(old_element2index,LexicalEntry.SynArg.SUBJECT,entry1.getReference()));   
            }
            elements.get(4).setIndex(new_element2index);
    	}
    	else if(i==4){
    		
    		for (LexicalEntry entry : elements.get(5).getActiveEntries()) {
                
                String query; 

                
                switch (entry.getSemArg(LexicalEntry.SynArg.PREPOSITIONALOBJECT)) {
                    
                     case SUBJOFPROP:
                          query = "SELECT DISTINCT ?x ?label WHERE { "
                                + " ?x <" + entry.getReference() + "> ?object . "
                                + " ?x <" + vocab.rdfs + "label> ?l . }";
                          elements.get(5).addToIndex(instances.getInstanceIndex(query,"x","l"));
                     case OBJOFPROP:
                          query = "SELECT DISTINCT ?x ?label WHERE { "
                                + " ?subject <" + entry.getReference() + "> ?x . "
                                + " ?x <" + vocab.rdfs + "label> ?l . }";
                          elements.get(5).addToIndex(instances.getInstanceIndex(query,"x","l"));
 	}

    }
   }
    	
  }
    
    @Override
    public List<String> buildSPARQLqueries(){
    	
    	List<String> queries = new ArrayList<>();
    	
    	ClassElement noun = (ClassElement) elements.get(1);
    	PropertyElement nounprop = (PropertyElement) elements.get(4);
    	IndividualElement indi = (IndividualElement) elements.get(5);
    	
    	for(LexicalEntry noun_entry: noun.getActiveEntries()){
    	for(LexicalEntry nounprop_entry: nounprop.getActiveEntries()){
    			
    			switch (nounprop_entry.getSemArg(LexicalEntry.SynArg.DIRECTOBJECT)){
    			
    				case SUBJOFPROP:
    					for(LexicalEntry inst_entry : indi.getActiveEntries()){
    						queries.add("SELECT DISTINCT ?x WHERE { "
    								+ "?x <"+vocab.rdfType+"> <"+noun_entry.getReference()+"> ."
    								+ "?x <"+nounprop_entry.getReference()+"> <"+inst_entry.getReference()+"> . }");
    							
    					}
    					break;
    				case OBJOFPROP:
    					for(LexicalEntry inst_entry : indi.getActiveEntries()){
    						queries.add("SELECT DISTINCT ?x WHERE { "
    								+ "?x <"+vocab.rdfType+"> <"+noun_entry.getReference()+"> ."
    								+ " <"+inst_entry.getReference()+"> <"+nounprop_entry.getReference()+"> ?x . }");
    							
    					}
    					break;
    			
    			}
    		}
    	}
    	
    	
    	return queries;
    }
    
}

