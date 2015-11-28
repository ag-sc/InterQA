package interQA.patterns;

import interQA.elements.ClassElement;
import interQA.elements.StringElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import interQA.lexicon.SparqlQueryBuilder;
import java.util.ArrayList;
import java.util.List;


public class QueryPattern1_3 extends QueryPattern{
	
	
	//SELECT ?x WHERE { ?x rdf:type dbp-owl:Song . }
	
		//What kind of meal exists
		//What kind of cars exists
		//What kind of musics exists
		//What kind of <Noun:Class> exists ?
	
	public QueryPattern1_3(Lexicon lexicon, InstanceSource instances){
		
		this.lexicon = lexicon;
		this.instances = instances;
		
		init();
		
	}
	public void init(){
		
		elements = new ArrayList<>();
		
		StringElement element0 = new StringElement();
		element0.add("what");
		elements.add(element0);
		
		StringElement element1 = new StringElement();
		element1.add("kind of");
		elements.add(element1);
		
		
		ClassElement element2 = new ClassElement(lexicon,LexicalEntry.POS.NOUN,null);
		elements.add(element2);
		
		
		StringElement element3 = new StringElement();
		element3.add("exists");
		elements.add(element3);
		
		
	}
	
	@Override
	public List<String> buildSPARQLqueries() {
	        
	        SparqlQueryBuilder sqb = new SparqlQueryBuilder();
	        
	        return sqb.BuildQueryForClassInstances(elements.get(2).getActiveEntries());
	    }	
	
	
}
