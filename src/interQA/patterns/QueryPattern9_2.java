package interQA.patterns;

import interQA.elements.IndividualElement;
import interQA.elements.StringElement;
import interQA.elements.ClassElement;
import interQA.elements.PropertyElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.SparqlQueryBuilder;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cunger
 */
public class QueryPattern9_2 extends QueryPattern {
    
        // SELECT ?x WHERE { ?x <Property> <Individual> . } 
        // SELECT ?x WHERE { <Individual> <Property> ?x . } 
    
        // (Who|What) <IntransitivePPVerb:Property> <Name:Individual>?
    
	// Who died in Berlin?
        // Who co-starred with Audrey Hepburn?
     

	public QueryPattern9_2(Lexicon lexicon, InstanceSource instances) {
            
            this.lexicon = lexicon;
            this.instances = instances; 
            
            init();
        }
        
        @Override
        public void init() {
            
            elements = new ArrayList<>();
            
            StringElement element0 = new StringElement(); 
            element0.add("who");
            element0.add("what");
            elements.add(element0);
		
            PropertyElement element1 = new PropertyElement(lexicon,LexicalEntry.POS.VERB,vocab.IntransitivePPFrame); 
            elements.add(element1);
		
            StringElement element2 = new StringElement();
            element2.add("in");
            element2.add("with");
            elements.add(element2);
            
            IndividualElement element3 = new IndividualElement(); 
            elements.add(element3);
	}

        @Override
        public void updateAt(int i,String s) {
            
            if (i == 2) {
            // If parse is at element1, fill element2 with possible instances...
                  
            	elements.get(3).addToIndex(instances.filterByPropertyForInstances(elements.get(1).getActiveEntries(), LexicalEntry.SynArg.PREPOSITIONALOBJECT));    
            }
        }        
        
        @Override
	public List<String> buildSPARQLqueries() {
            
            SparqlQueryBuilder sqb = new SparqlQueryBuilder();
            
            PropertyElement  verb     = (PropertyElement)  elements.get(1);
            IndividualElement instance = (IndividualElement) elements.get(2);
                 
            return sqb.BuildQueryForIndividualAndPropery(instance, verb, LexicalEntry.SynArg.PREPOSITIONALOBJECT);
            }
}
