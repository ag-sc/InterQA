package interQA.patterns;


import interQA.elements.ClassElement;
import interQA.elements.StringElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.SparqlQueryBuilder;
import interQA.lexicon.Lexicon;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class QueryPattern1_2 extends QueryPattern {
	
	//SELECT ?x WHERE { ?x rdf:type dbp-owl:Song . }
	
	//What songs|cars|NBA players are there?
	//Which songs|cars|NBA players are there ?
	//What <Noun:Class> are there ?
	
	public QueryPattern1_2(Lexicon lexicon,InstanceSource instances){
		
		this.lexicon = lexicon;
		this.instances = instances;
		
		init();
		
	}
	
	@Override
	public void init(){
		
		elements = new ArrayList<>();
		
		StringElement element0 = new StringElement();
		element0.add("what");
		element0.add("which");
		elements.add(element0);
		
		ClassElement element1 = new ClassElement(lexicon,LexicalEntry.POS.NOUN,null);
		elements.add(element1);
		
		StringElement element2 = new StringElement();
		element2.add("are");
		elements.add(element2);
		
		StringElement element3 = new StringElement();
		element3.add("there");
		elements.add(element3);
		
		
		
	}
	
	@Override
	public Set<String> buildSPARQLqueries() {
	       
		SparqlQueryBuilder sqb = new SparqlQueryBuilder();
	    
		return sqb.BuildQueryForClassInstances(elements.get(1).getActiveEntries());
		
	    }	
}
