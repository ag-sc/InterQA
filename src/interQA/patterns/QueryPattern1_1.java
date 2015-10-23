package interQA.patterns;

import interQA.elements.ConceptElement;
import interQA.elements.StringElement;
import interQA.lexicon.LexicalEntry;
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
    
    
    public QueryPattern1_1(Lexicon lexicon) {
        
        StringElement element0 = new StringElement(); 
	element0.add("give me");
	elements.add(element0);
	
        StringElement element1 = new StringElement(); 
        element1.add("all");
	elements.add(element1);
		
        ConceptElement element2 = new ConceptElement(lexicon,LexicalEntry.POS.NOUN,null); 
	elements.add(element2);
    }
    
    @Override
    public List<String> buildSPARQLqueries() {
        
        List<String> queries = new ArrayList<>();
        
        for (LexicalEntry entry : elements.get(2).getActiveEntries()) {
             queries.add("SELECT DISTINCT ?x WHERE { "
                       + " ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + entry.getReference() + "> . }");
        }
        
        return queries;
    }

}
