package interQA.patterns;

import interQA.elements.ClassElement;
import interQA.elements.StringElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import interQA.lexicon.SparqlQueryBuilder;
import java.util.ArrayList;
import java.util.List;

public class QueryPattern1_5 extends QueryPattern{

	////SELECT ?x WHERE { ?x rdf:type dbp-owl:Song . }
	
	//What Songs do you know?
	//What cars do you know ? 
	//what movies do you know ?
	
	//What <Noun:Class> do you know ?
	
	
	
	public QueryPattern1_5(Lexicon lexicon,InstanceSource instances){
		
		this.lexicon = lexicon;
		this.instances = instances;
		
		init();
		
	}
	@Override
	public void init(){
		
		StringElement element0 = new StringElement();
		element0.add("what");
		elements.add(element0);
		
		ClassElement element1 = new ClassElement(lexicon,LexicalEntry.POS.NOUN,null);
		elements.add(element1);
		
		StringElement element2 = new StringElement();
		element2.add("do");
		elements.add(element2);
		
		StringElement element3 = new StringElement();
		element3.add("you");
		elements.add(element3);
		
		StringElement element4 = new StringElement();
		element4.add("know");
		elements.add(element4);
		
		
	}
	
	@Override
	public List<String> buildSPARQLqueries() {
        
        SparqlQueryBuilder sqb = new SparqlQueryBuilder();
        
        return sqb.BuildQueryForClassInstances(elements.get(1).getActiveEntries());
    }	
	
	
}
