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
            sqb.setEndpoint(dataset.getEndpoint());
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
                
                case 0: {
                    
                    checkHowMany(s);
                    ((StringElement) elements.get(0)).transferFeatures(elements.get(1),s);
                    break;
                }
                case 1: {
                    
                    setFeatures(1,2,s);
                    break;
                } 
            }
        }

        @Override
        public Set<String> buildSPARQLqueries() {

            // SELECT DISTINCT ?x WHERE 
            // {
            //   ?x rdf:type <C> .
            // }
            		
            String mainVar = "x";
            
            ClassElement c = (ClassElement) elements.get(1);
                        
            switch (currentElement) {
                    
                case 0: {
                    
                    builder.reset();
                    
                    if (count) builder.addCountVar(mainVar); 
                    else       builder.addProjVar(mainVar);
                    break;
                } 
                
                case 1: { // + ?x rdf:type <C> .
                    
                    builder.addTypeTriple(mainVar,c.getActiveEntries());
                    break;
                }
            }
                        
            return builder.returnQueries();
        }	
}
