package interQA.patterns;

import interQA.elements.ClassElement;
import interQA.elements.StringElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.SparqlQueryBuilder;
import interQA.lexicon.Lexicon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cunger
 */
public class QueryPattern1_1 extends QueryPattern {
    
    // SELECT ?s WHERE { ?x rdf:type <Class> . }
    
    // Give me all <Noun:Class>.
    
    // Give me all movies. 
    // Give me all mountains. 
    // List me all movies
	// List me all mountains
    
    public QueryPattern1_1(Lexicon lexicon, InstanceSource instances) {
                            
            this.lexicon = lexicon;
            this.instances = instances; 
            
            init();
        }
        
        @Override
        public void init() {
            
            elements = new ArrayList<>();
        
            StringElement element0 = new StringElement(); 
            element0.add("give me");
            element0.add("list me");
            elements.add(element0);

            StringElement element1 = new StringElement(); 
            element1.add("all");
            elements.add(element1);

            ClassElement element2 = new ClassElement(lexicon,LexicalEntry.POS.NOUN,null); 
            elements.add(element2);
    }
    
    @Override
    public List<String> buildSPARQLqueries() {
        
        SparqlQueryBuilder sqb = new SparqlQueryBuilder();
                
        return sqb.BuildQueryForClassInstances(elements.get(2).getActiveEntries());
    }

}
