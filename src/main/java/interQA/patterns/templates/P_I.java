package interQA.patterns.templates;

import interQA.elements.Element;
import interQA.elements.InstanceElement;
import interQA.elements.StringElement;
import interQA.elements.PropertyElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import interQA.patterns.query.IncrementalQuery;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.apache.jena.query.Query;

/**
 *
 * @author cunger
 */
public class P_I extends QueryPattern {
    
                        
        // SELECT DISTINCT ?x WHERE 
        // {
        //   ?x <P> <I> .
        // }
    
    
	public P_I(Lexicon lexicon, DatasetConnector instances) {
            
            this.lexicon = lexicon;
            this.dataset = instances; 
            
            init();
        }
        
        @Override
        public void init() {  
                        
            StringElement element0 = new StringElement(); 
            elements.add(element0);
            
            PropertyElement element1 = new PropertyElement();
            elements.add(element1);
            
            StringElement element2 = new StringElement(); 
            elements.add(element2);
		
            InstanceElement element3 = new InstanceElement(); 
            elements.add(element3);
            
            StringElement element4 = new StringElement(); 
            elements.add(element4);
	}

        @Override
        public void update(String s) {
    
            PropertyElement p = (PropertyElement) elements.get(1);
            InstanceElement i = (InstanceElement) elements.get(3);
            
            switch (currentElement) {
                
                case 0: {
                    
                    // Create query template
                    
                    builder.reset();
             
                    String mainVar = "x";
                    
                    builder.addUninstantiatedTriple(mainVar,builder.placeholder("P"),builder.placeholder("I"));
                    
                    checkHowMany(s);
                    if (count) builder.addCountVar(mainVar); 
                    else       builder.addProjVar(mainVar);
                    
                    break;
                }
            
                case 1: {
                    
                    for (String m : elements.get(1).getMarkers()) {
                        ((StringElement) elements.get(2)).add(m);
                    }
                    
                    builder.instantiate("P",p);
                    dataset.fillInstances(elements.get(3),builder,"I");
                    break;
                }
                    
                case 3: {
                                            
                    builder.instantiate("I",i);
                    break;
                } 
            }
        }       
        
        @Override
        public P_I clone() {
            
            P_I clone = new P_I(lexicon,dataset);
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
            
            // Init queries
            
            builder.reset();
            String mainVar = "x";
            builder.addUninstantiatedTriple(mainVar,builder.placeholder("P"),builder.placeholder("I"));

            // Build ASK queries
            
            Set<IncrementalQuery> iqueries = new HashSet<>();
            
            for (LexicalEntry entry : lexicon.getPropertyEntries()) {
                 for (IncrementalQuery i : builder.getQueries()) {
                      IncrementalQuery j = builder.instantiate("P",entry,i);
                      iqueries.add(j);
                 }
            }
                        
            for (IncrementalQuery iquery : iqueries) {
                 queries.add(iquery.prettyPrint(iquery.assembleAsAsk(vocab,false)));
            }
            
            return queries;
        }

        @Override 
        public Set<String> predictSELECTqueries() {
            
            Set<String> queries = new HashSet<>();
            
            // Init queries
            
            builder.reset();
            String mainVar = "x";
            builder.addProjVar(mainVar);
            builder.addUninstantiatedTriple(mainVar,builder.placeholder("P"),builder.placeholder("I"));

            // Instantiate P
            
            Set<IncrementalQuery> iqueries = new HashSet<>();
            
            for (LexicalEntry entry : lexicon.getPropertyEntries()) {
                 for (IncrementalQuery i : builder.getQueries()) {
                      IncrementalQuery j = builder.instantiate("P",entry,i);
                      iqueries.add(j);
                 }
            }
                        
            // Build SELECT queries
            
            for (IncrementalQuery iquery : iqueries) {
                
                Query query = iquery.assemble(vocab,false);
                query.setQueryResultStar(true);
                queries.add(iquery.prettyPrint(query)); 
            }
            
            return queries;
        }

}
