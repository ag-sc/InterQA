package interQA.patterns.templates;

import interQA.elements.ClassElement;
import interQA.elements.StringElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.Lexicon;
import java.util.Set;


public class C extends QueryPattern {
 
    
    /*
    SELECT ?x WHERE { ?x a <Class> . } 
    */
    
    
    public C(Lexicon lexicon,DatasetConnector dataset){
		
            this.lexicon = lexicon;
            this.dataset = dataset;
	
            init();	
	}
	
	@Override
	public void init(){
		
            StringElement element0 = new StringElement ();
            elements.add(element0);
		
            ClassElement element1 = new ClassElement();
            elements.add(element1);
            
            StringElement element2 = new StringElement ();
            elements.add(element2);
	}

        @Override
        public void update(String s) {
            
            switch (currentElement) {
                
                case 0: ((StringElement) elements.get(0)).transferFeatures(elements.get(1),s); break;
                case 1: setFeatures(1,2,s); break;
            }
        }

        @Override
        public Set<String> buildSPARQLqueries() {

            ClassElement c = (ClassElement) elements.get(1);
            
            switch (currentElement) {
                
                case 1: queries = sqb.BuildQueryForClassInstances(c.getActiveEntries());
            }
            
            return queries;
        }	
}
