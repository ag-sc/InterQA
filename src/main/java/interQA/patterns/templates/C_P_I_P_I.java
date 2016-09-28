package interQA.patterns.templates;

import interQA.elements.ClassElement;
import interQA.elements.Element;
import interQA.elements.InstanceElement;
import interQA.elements.PropertyElement;
import interQA.elements.StringElement;
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
 * @author mirtik, cunger
 */
public class C_P_I_P_I extends QueryPattern{
    
    
    // SELECT DISTINCT ?x WHERE 
    // {
    //   ?x rdf:type <C> .
    //   ?x <P1> <I1> .
    //   ?x <P2> <I2> . 
    // }    
    
    public C_P_I_P_I(Lexicon lexicon,DatasetConnector dataset){
        	
            this.lexicon = lexicon;
            this.dataset = dataset;
		
            init();
    }
    
    @Override
	public void init() {
            	
            StringElement element0 = new StringElement();
            elements.add(element0);

            ClassElement element1 =  new ClassElement();
            elements.add(element1);

            StringElement element2 = new StringElement();
            elements.add(element2);

            PropertyElement element3 = new PropertyElement();
            elements.add(element3);
            
            StringElement element4 = new StringElement();
            elements.add(element4);

            InstanceElement element5 = new InstanceElement();
            elements.add(element5);

            StringElement element6 = new StringElement();
            elements.add(element6);
            
            PropertyElement element7 = new PropertyElement();
            elements.add(element7);

            StringElement element8 = new StringElement();
            elements.add(element8);
            
            InstanceElement element9 = new InstanceElement();
            elements.add(element9);
            
            StringElement element10 = new StringElement();
            elements.add(element10);
	}
	
	@Override
	public void update(String s) {
            
            ClassElement    c  = (ClassElement)    elements.get(1);
            PropertyElement p1 = (PropertyElement) elements.get(3);
            InstanceElement i1 = (InstanceElement) elements.get(5);
            PropertyElement p2 = (PropertyElement) elements.get(7);
            InstanceElement i2 = (InstanceElement) elements.get(9);
		
            switch (currentElement) {
                    
                case 0: {
                        
                    // Create query template 
                        
                    builder.reset();
                
                    String mainVar = "x";
                    
                    builder.addUninstantiatedTypeTriple(mainVar,builder.placeholder("C"));
                    builder.addUninstantiatedTriple(mainVar,builder.placeholder("P1"),builder.placeholder("I1"));
                    builder.addUninstantiatedTriple(mainVar,builder.placeholder("P2"),builder.placeholder("I2"));
                    
                    checkHowMany(s);
                    if (count) builder.addCountVar(mainVar); 
                    else       builder.addProjVar(mainVar);

                    break;
                }
                
                case 1: {
 
                    builder.instantiate("C",c);
                    dataset.filter(elements.get(3),builder,"P1");
                    elements.get(3).addCopula((StringElement) elements.get(2));
                    break;
                } 
		
                case 3: {
                        
                    for (String m : elements.get(3).getMarkers()) {
                        ((StringElement) elements.get(4)).add(m);
                    }
                        
                    builder.instantiate("P1",p1);
                    dataset.fillInstances(elements.get(5),builder,"I1");
                    break;
                }
                    
                case 5: {
                        
                    for (String key :elements.get(3).getActiveEntriesKey()){
                         elements.get(5).getIndex().remove(key);
                    }
                        
                    builder.instantiate("I1",i1);
                    dataset.filter(elements.get(7),builder,"P2");
                    
                    // avoid P2=P1
                    Set<LexicalEntry> del = new HashSet<>();
                    for (LexicalEntry entry : elements.get(7).getActiveEntries()) {
                         if (elements.get(3).getActiveEntries().contains(entry)) {
                             del.add(entry);
                         }
                    }
                    elements.get(7).getActiveEntries().removeAll(del);
                    break;
                }
                    
                case 7: {
                        
                    for (String m : elements.get(7).getMarkers()) {
                       ((StringElement) elements.get(8)).add(m);
                    }
                        
                    builder.instantiate("P2",p2);
                    dataset.fillInstances(elements.get(9),builder,"I2");
                    break;
                }
                    
                case 9: {
                        
                    builder.instantiate("I2",i2);
                    break;
                }
            }
	}
        
        @Override
        public C_P_I_P_I clone() {
            
            C_P_I_P_I clone = new C_P_I_P_I(lexicon,dataset);
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
            
            return predictASKqueries(false);
        }
        
        public Set<String> predictASKqueries(boolean all) {
            
            Set<String> queries = new HashSet<>();
            
            // Init queries
            
            builder.reset();
            String mainVar = "x";
            builder.addUninstantiatedTypeTriple(mainVar,builder.placeholder("C"));
            builder.addUninstantiatedTriple(mainVar,builder.placeholder("P1"),builder.placeholder("I1"));
            builder.addUninstantiatedTriple(mainVar,builder.placeholder("P2"),builder.placeholder("I2"));

            // Build ASK queries
            
            Set<IncrementalQuery> initialqueries = builder.getQueries();
            Set<IncrementalQuery> iqueries  = new HashSet<>();
            Set<IncrementalQuery> intermed1 = new HashSet<>();
            Set<IncrementalQuery> intermed2 = new HashSet<>();
            
            for (LexicalEntry entry : lexicon.getClassEntries()) {
                 for (IncrementalQuery i : initialqueries) {
                      IncrementalQuery j = builder.instantiate("C",entry,i);
                      intermed1.add(j);
                 }
            }
            
            iqueries.addAll(intermed1);
            intermed1 = new HashSet<>();
            
            for (LexicalEntry entry : lexicon.getPropertyEntries()) {
                 for (IncrementalQuery i : iqueries) {
                      IncrementalQuery j = builder.instantiate("P1",entry,i);
                      intermed1.add(j);
                 }
            }
            
            iqueries.addAll(intermed1);
            
            if (all) {
            
                // fill instances in element
                builder.setQueries(intermed1);
                dataset.fillInstances(elements.get(5),builder,"I1",true);
                // create ASK query
                for (LexicalEntry entry : elements.get(5).getActiveEntries()) {
                    for (IncrementalQuery i : intermed1) {
                          IncrementalQuery j = builder.instantiate("I1",entry,i);
                          intermed2.add(j);
                     }
                }
                // reset
                builder.setQueries(initialqueries);
                elements.set(5,new InstanceElement());
            } 
            else {
                intermed2 = intermed1;
            }
            
            for (LexicalEntry entry : lexicon.getPropertyEntries()) {
                for (IncrementalQuery i : intermed2) {
                     IncrementalQuery j = builder.instantiate("P2",entry,i);
                     iqueries.add(j);
                 }
            }
                        
            for (IncrementalQuery iquery : iqueries) {
                 queries.add(iquery.prettyPrint(iquery.assembleAsAsk(vocab,true)));
            }
            
            return queries;
        }
        
        @Override
        public Set<String> predictSELECTqueries() {
            
            return predictSELECTqueries(false);
        }
         
        public Set<String> predictSELECTqueries(boolean all) {
            
            Set<String> queries = new HashSet<>();
            
            // Init queries
            
            builder.reset();
            String mainVar = "x";
            builder.addProjVar(mainVar);
            builder.addUninstantiatedTypeTriple(mainVar,builder.placeholder("C"));
            builder.addUninstantiatedTriple(mainVar,builder.placeholder("P1"),builder.placeholder("I1"));
            builder.addUninstantiatedTriple(mainVar,builder.placeholder("P2"),builder.placeholder("I2"));

            // Instantiate C and P1

            Set<IncrementalQuery> initialqueries = builder.getQueries();
            Set<IncrementalQuery> iqueries  = new HashSet<>();
            Set<IncrementalQuery> intermed1 = new HashSet<>();
            Set<IncrementalQuery> intermed2 = new HashSet<>();
            Set<IncrementalQuery> intermed3 = new HashSet<>();

            for (LexicalEntry entry : lexicon.getClassEntries()) {
                 for (IncrementalQuery i : builder.getQueries()) {
                      IncrementalQuery j = builder.instantiate("C",entry,i);
                      intermed1.add(j);
                 }
            }
            
            for (LexicalEntry entry : lexicon.getPropertyEntries()) {
                 for (IncrementalQuery i : intermed1) {
                      IncrementalQuery j = builder.instantiate("P1",entry,i);
                      intermed2.add(j);
                 }
            }
            
            // Build SELECT queries for I1

            for (IncrementalQuery iquery : intermed2) {
                
                Query query = iquery.assemble(vocab,true);
                query.setQueryResultStar(true);
                queries.add(iquery.prettyPrint(query)); 
            }
            
            // Instantiate I1 and P2

            if (all) {
                
                // fill instances in element
                builder.setQueries(intermed2);
                dataset.fillInstances(elements.get(5),builder,"I1",true);
                // create ASK query
                for (LexicalEntry entry : elements.get(5).getActiveEntries()) {
                    for (IncrementalQuery i : intermed2) {
                         IncrementalQuery j = builder.instantiate("I1",entry,i);
                         intermed3.add(j);
                     }
                }
                // reset
                builder.setQueries(initialqueries);
                elements.set(5,new InstanceElement());
            }
            else {
                intermed3 = intermed2;
            }
            
            for (LexicalEntry entry : lexicon.getPropertyEntries()) {
                 for (IncrementalQuery i : intermed3) {
                      IncrementalQuery j = builder.instantiate("P2",entry,i);
                      iqueries.add(j);
                 }
            }
            
            // Build SELECT queries for I2
            
            for (IncrementalQuery iquery : iqueries) {
                
                Query query = iquery.assemble(vocab,true);
                query.setQueryResultStar(true);
                queries.add(iquery.prettyPrint(query)); 
            }
            
            return queries;
        }

}
