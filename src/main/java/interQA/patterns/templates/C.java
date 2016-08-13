package interQA.patterns.templates;

import interQA.elements.ClassElement;
import interQA.elements.Element;
import interQA.elements.StringElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import interQA.patterns.query.IncrementalQuery;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class C extends QueryPattern {
 
    
    // SELECT DISTINCT ?x WHERE 
    // {
    //   ?x rdf:type <C> .
    // }
    
    
    public C(Lexicon lexicon,DatasetConnector dataset) {
		
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
             
            ClassElement c = (ClassElement) elements.get(1);

            switch (currentElement) {
                
                case 0: {
                    
                    // Create query template 
                    
                    builder.reset();
                    
                    String mainVar = "x";
                                    
                    builder.addUninstantiatedTypeTriple(mainVar,builder.placeholder("C"));
                    
                    checkHowMany(s);
                    if (count) builder.addCountVar(mainVar); 
                    else       builder.addProjVar(mainVar);

                    break;
                }
                
                case 1: {
                                        
                    builder.instantiate("C",c);
                    break;
                } 
            }
        }
        
        @Override
        public C clone() {
            
            C clone = new C(lexicon,dataset);
            clone.elements = new ArrayList<>();
            for (Element e : elements) {
                 clone.elements.add(e.clone());
            }
            clone.agreement = agreement;
            clone.builder = builder.clone();
            
            return clone;
        }
        
        @Override 
        public Set<String> predictASKqueries() {
            
            Set<String> queries = new HashSet<>();
            
            for (LexicalEntry entry : lexicon.getClassEntries()) {
                 for (IncrementalQuery query : builder.getQueries()) {
                      IncrementalQuery instq = builder.instantiate("C",entry,query);
                      queries.add(instq.prettyPrint(instq.assembleAsAsk(vocab,false)));
                 }
            }
            
            return queries;
        }

}
