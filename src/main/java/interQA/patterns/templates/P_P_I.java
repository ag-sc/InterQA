package interQA.patterns.templates;

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
public class P_P_I extends QueryPattern{
	

        // SELECT DISTINCT ?x ?y WHERE 
        // {
        //   <I> <P1> ?x .
        //   <I> <P2> ?y . 
        // }
    
	
	public P_P_I(Lexicon lexicon,DatasetConnector instances){
				            
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
		
            PropertyElement element3 = new PropertyElement();
            elements.add(element3);
		
            StringElement element4 = new StringElement();
            elements.add(element4);
		
            InstanceElement element5 = new InstanceElement();
            elements.add(element5);
		
            StringElement element6 = new StringElement();
            elements.add(element6);
	}
        
	@Override
	public void update(String s) {
	
            PropertyElement p1 = (PropertyElement) elements.get(1);
            PropertyElement p2 = (PropertyElement) elements.get(3);
            InstanceElement i  = (InstanceElement) elements.get(5);
                    
            switch (currentElement) {
                        
                case 0: {
                    
                    // Create query template 
                    
                    builder.reset();
                            
                    String mainVar1 = "x";
                    String mainVar2 = "y";
                            
                    builder.addUninstantiatedTriple(builder.placeholder("I"),builder.placeholder("P1"),mainVar1);
                    builder.addUninstantiatedTriple(builder.placeholder("I"),builder.placeholder("P2"),mainVar2);

                    checkHowMany(s); // TODO not used in this pattern
                    builder.addProjVar(mainVar1);
                    builder.addProjVar(mainVar2);

                    break;
                }
                    
                case 1: {
                           
                    builder.instantiate("P1",p1);
                    dataset.filter(elements.get(3),builder,"P2");
                    
                    // avoid P2=P1
                    Set<LexicalEntry> del = new HashSet<>();
                    for (LexicalEntry entry : elements.get(3).getActiveEntries()) {
                         if (elements.get(1).getActiveEntries().contains(entry)) {
                             del.add(entry);
                         }
                    }
                    elements.get(3).getActiveEntries().removeAll(del);
                    break;
                }
                        
                case 3: {

                    for (String m : elements.get(3).getMarkers()) {
                       ((StringElement) elements.get(4)).add(m);
                    }
                            
                    builder.instantiate("P2",p2);
                    dataset.fillInstances(elements.get(5),builder,"I");
                    break;
                }
                
                case 5: {
                    
                    builder.instantiate("I",i);
                    break;
                }
            }
        }
        
        @Override
        public P_P_I clone() {
            
            P_P_I clone = new P_P_I(lexicon,dataset);
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
            String mainVar1 = "x";
            String mainVar2 = "y";
            builder.addProjVar(mainVar1);
            builder.addProjVar(mainVar2);
            builder.addUninstantiatedTriple(builder.placeholder("I"),builder.placeholder("P1"),mainVar1);
            builder.addUninstantiatedTriple(builder.placeholder("I"),builder.placeholder("P2"),mainVar2);

            // Build ASK queries
            
            Set<IncrementalQuery> iqueries = new HashSet<>();
            Set<IncrementalQuery> intermed = new HashSet<>();
            
            for (LexicalEntry entry : lexicon.getPropertyEntries()) {
                 for (IncrementalQuery i : builder.getQueries()) {
                      IncrementalQuery j = builder.instantiate("P1",entry,i);
                      intermed.add(j);
                 }
            }
            
            iqueries.addAll(intermed);
            intermed = new HashSet<>();
            
            for (LexicalEntry entry : lexicon.getPropertyEntries()) {
                 for (IncrementalQuery i : iqueries) {
                      IncrementalQuery j = builder.instantiate("P2",entry,i);
                      intermed.add(j);
                 }
            }
            
            iqueries.addAll(intermed);
            
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
            String mainVar1 = "x";
            String mainVar2 = "y";
            builder.addProjVar(mainVar1);
            builder.addProjVar(mainVar2);
            builder.addUninstantiatedTriple(builder.placeholder("I"),builder.placeholder("P1"),mainVar1);
            builder.addUninstantiatedTriple(builder.placeholder("I"),builder.placeholder("P2"),mainVar2);

            // Instantiate P1 and P2
            
            Set<IncrementalQuery> iqueries1 = new HashSet<>();
            Set<IncrementalQuery> iqueries2 = new HashSet<>();
            
            for (LexicalEntry entry : lexicon.getPropertyEntries()) {
                 for (IncrementalQuery i : builder.getQueries()) {
                      IncrementalQuery j = builder.instantiate("P1",entry,i);
                      iqueries1.add(j);
                 }
            }
            
            for (LexicalEntry entry : lexicon.getPropertyEntries()) {
                 for (IncrementalQuery i : iqueries1) {
                      IncrementalQuery j = builder.instantiate("P2",entry,i);
                      iqueries2.add(j);
                 }
            }
            
            // Build SELECT queries
            
            for (IncrementalQuery iquery : iqueries2) {
                
                Query query = iquery.assemble(vocab,false);
                query.setQueryResultStar(true);
                queries.add(iquery.prettyPrint(query)); 
            }
            
            return queries;
        }

}

