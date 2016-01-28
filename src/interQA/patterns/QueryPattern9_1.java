package interQA.patterns;

import interQA.elements.IndividualElement;
import interQA.lexicon.Lexicon;
import interQA.elements.StringElement;
import interQA.elements.ClassElement;
import interQA.elements.PropertyElement;
import interQA.lexicon.SparqlQueryBuilder;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;



public class QueryPattern9_1 extends QueryPattern {

        // SELECT ?x WHERE { ?x <Property> <Individual> . } 
        // SELECT ?x WHERE { <Individual> <Property> ?x . } 
    
        // (Who|What) <TransitiveVerb:Property> <Name:Individual>?
    
	// Who created Miffy?
	// What causes cancer?
            
    
	public QueryPattern9_1(Lexicon lexicon, InstanceSource instances) {
                        
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
		
            PropertyElement element1 = new PropertyElement(lexicon,LexicalEntry.POS.VERB,vocab.TransitiveFrame); 
            elements.add(element1);
		
            IndividualElement element2 = new IndividualElement(); 
            elements.add(element2);
	}
        
        @Override
        public void updateAt(int i,String s) {
            
            if (i == 1) {
            // If parse is at element1, fill element2 with possible instances...
                     
                for (LexicalEntry entry : elements.get(1).getActiveEntries()) {
                    
                	elements.get(2).addToIndex(instances.filterByPropertyForInstances(elements.get(1).getActiveEntries(), LexicalEntry.SynArg.DIRECTOBJECT));
                
                }     
            }
        }

        @Override
	public Set<String> buildSPARQLqueries() {
            
            SparqlQueryBuilder sqb = new SparqlQueryBuilder();
            
            PropertyElement    verb     = (PropertyElement)    elements.get(1);
            IndividualElement instance = (IndividualElement) elements.get(2);
            
            return sqb.BuildQueryForIndividualAndPropery(instance, verb,LexicalEntry.SynArg.DIRECTOBJECT);
            
	}
}
