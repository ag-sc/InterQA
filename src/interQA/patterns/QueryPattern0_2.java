package interQA.patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interQA.lexicon.SparqlQueryBuilder;
import interQA.elements.ClassElement;
import interQA.elements.IndividualElement;
import interQA.elements.PropertyElement;
import interQA.elements.StringElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;

/**
 *
 * @author cunger
 */
public class QueryPattern0_2 extends QueryPattern {
 
    // SELECT ?x WHERE { ?x rdf:type <Class> . ?x <Property> <Individual> . } 
    // SELECT ?x WHERE { ?x rdf:type <Class> . ?y <Property> <Individual> . }
    
    // Which <Noun:Class> <IntransitivePP:Property> <Name:Individual>?

    // Which actors played in Batman?
    // Which rivers flow through Bielefeld? 
	
	public QueryPattern0_2(Lexicon lexicon,InstanceSource instances){
		this.lexicon = lexicon;
		this.instances = instances;
		
		init();
		
	}

	public void init(){
		
		elements = new ArrayList<>();
		
		StringElement element0 = new StringElement();
		element0.add("which");
		elements.add(element0);
		
		ClassElement element1 = new ClassElement(lexicon,LexicalEntry.POS.NOUN,null);
		elements.add(element1);
		
		PropertyElement element2 = new PropertyElement(lexicon,LexicalEntry.POS.VERB,vocab.IntransitivePPFrame);
		elements.add(element2);
		
		IndividualElement element3 = new IndividualElement();
		elements.add(element3);
	
	}
	
	@Override
	public void updateAt(int i,String s){
		
		if(i==1){
			
			Map<String,List<LexicalEntry>> old_element2index = elements.get(1).getIndex();
            
    		Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
    		
    		for(LexicalEntry entry : elements.get(1).getActiveEntries()){
    			
    			new_element2index.putAll(instances.filterByClassForProperty(old_element2index, LexicalEntry.SynArg.SUBJECT, entry.getReference()));
    			
    		}
			elements.get(2).addToIndex(new_element2index);
		}
		if(i==2){
        	elements.get(3).addToIndex(instances.filterByPropertyForInstances(elements.get(2).getActiveEntries(), LexicalEntry.SynArg.DIRECTOBJECT ));

		}
		
	}

    @Override
    public List<String> buildSPARQLqueries() {
        
    	SparqlQueryBuilder sqb = new SparqlQueryBuilder();
        
        ClassElement    noun = (ClassElement)    elements.get(1);
        PropertyElement    verb = (PropertyElement)    elements.get(2);
        IndividualElement indi = (IndividualElement) elements.get(3);
             
        
        return sqb.BuildQueryForIndividualAndProperty(noun, indi, verb,LexicalEntry.SynArg.DIRECTOBJECT);
}
}
