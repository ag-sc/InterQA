package interQA.patterns;

import interQA.elements.ClassElement;
import interQA.elements.StringElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import interQA.lexicon.SparqlQueryBuilder;
import java.util.ArrayList;
import java.util.List;


public class QueryPattern1_4 extends QueryPattern{
	
	//SELECT ?x WHERE { ?x rdf:type dbp-owl:Song . }
	
	//Do you know any animals?
	//Do you know any NBA players?
	//Do you know any surfers?
	
	//Do you know any <Noun:Class>
	
	public QueryPattern1_4(Lexicon lexicon,InstanceSource instances){
		
		this.lexicon = lexicon;
		this.instances = instances;
		
		init();
		
	}
	
	@Override
	public void init(){
		
		StringElement element0 = new StringElement ();
		element0.add("do");
		elements.add(element0);
		
		StringElement element1 = new StringElement();
		element1.add("you");
		elements.add(element1);
		
		StringElement element2 = new StringElement();
		element2.add("know");
		elements.add(element2);
		
		StringElement element3 = new StringElement();
		element3.add("any");
		elements.add(element3);
		
		ClassElement element4 = new ClassElement(lexicon,LexicalEntry.POS.NOUN,null);
		elements.add(element4);
		
		
	}


@Override
public List<String> buildSPARQLqueries() {
        
        SparqlQueryBuilder sqb = new SparqlQueryBuilder();
        
        return sqb.BuildQueryForClassInstances(elements.get(4).getActiveEntries());
    }	
}