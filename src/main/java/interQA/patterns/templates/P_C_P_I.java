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


public class P_C_P_I extends QueryPattern{

    
	// SELECT DISTINCT ?x WHERE 
        // {
        //   ?x <P1> ?y .
        //   ?y rdf:type <C> .
        //   ?y <P2> <I> .
        // }
        
    
	public P_C_P_I(Lexicon lexicon,DatasetConnector dataset) {
		
            this.lexicon = lexicon;
            this.dataset = dataset;

            init();
	}
        

        @Override
	public void init() {
            
            StringElement element0 = new StringElement();
            elements.add(element0);
		
            PropertyElement element1 =  new PropertyElement();
            elements.add(element1);
		
            StringElement element2 = new StringElement();
            elements.add(element2);
		
            ClassElement element3 = new ClassElement();
            elements.add(element3);
            
            StringElement element4 = new StringElement();
            elements.add(element4);
            
            PropertyElement element5 =  new PropertyElement();
            elements.add(element5);
		
            StringElement element6 = new StringElement();
            elements.add(element6);
		
            InstanceElement element7 = new InstanceElement();
            elements.add(element7);    
            
            StringElement element8 = new StringElement();
            elements.add(element8);
	}
	
	@Override
	public void update(String s) {

            PropertyElement p1 = (PropertyElement) elements.get(1);
            ClassElement    c  = (ClassElement)    elements.get(3);
            PropertyElement p2 = (PropertyElement) elements.get(5);
            InstanceElement i  = (InstanceElement) elements.get(7);
		
            switch (currentElement) {
                
                case 0: { 
                    
                    // Create query template
                    
                    builder.reset();
                    
                    String mainVar  = "x";
                    String otherVar = "y";  
                    
                    builder.addUninstantiatedTriple(mainVar,builder.placeholder("P1"),otherVar);
                    builder.addUninstantiatedTypeTriple(otherVar,builder.placeholder("C"));
                    builder.addUninstantiatedTriple(otherVar,builder.placeholder("P2"),builder.placeholder("I"));
                    
                    checkHowMany(s);
                    if (count) builder.addCountVar(mainVar); 
                    else       builder.addProjVar(mainVar);
                    
                    break;
                } 
                
                case 1: {
                                    
                    builder.instantiate("P1",p1);
                    for (String m : elements.get(1).getMarkers()) {
                        ((StringElement) elements.get(2)).add(m);
                    }
                    
                    dataset.filter(elements.get(3),builder,"C");
                    break;
                }
                    
                case 3: {
                    
                    builder.instantiate("C",c);
                    dataset.filter(elements.get(5),builder,"P2");
                    break;
                }
                
                case 5: {
                    
                    builder.instantiate("P2",p2);
                    for (String m : elements.get(5).getMarkers()) {
                        ((StringElement) elements.get(6)).add(m);
                    }
                    
                    dataset.fillInstances(elements.get(7),builder,"I");
                    break;
                }
                
                case 7: { 
                                        
                    builder.instantiate("I",i);
                    break;
                } 
            }
	}
        
        @Override
        public P_C_P_I clone() {
            
            P_C_P_I clone = new P_C_P_I(lexicon,dataset);
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
            String mainVar  = "x";
            String otherVar = "y";  
            builder.addUninstantiatedTriple(mainVar,builder.placeholder("P1"),otherVar);
            builder.addUninstantiatedTypeTriple(otherVar,builder.placeholder("C"));
            builder.addUninstantiatedTriple(otherVar,builder.placeholder("P2"),builder.placeholder("I"));

            // Build ASK queries
            
            Set<IncrementalQuery> iqueries = new HashSet<>();
            Set<IncrementalQuery> intermed = new HashSet<>();

            for (LexicalEntry entry : lexicon.getPropertyEntries()) {
                 for (IncrementalQuery i : iqueries) {
                      IncrementalQuery j = builder.instantiate("P1",entry,i);
                      intermed.add(j);
                 }
            }
            
            iqueries.addAll(intermed);
            intermed = new HashSet<>();

            for (LexicalEntry entry : lexicon.getClassEntries()) {
                 for (IncrementalQuery i : builder.getQueries()) {
                      IncrementalQuery j = builder.instantiate("C",entry,i);
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
            String mainVar = "x";
            builder.addProjVar(mainVar);
            builder.addUninstantiatedTypeTriple(mainVar,builder.placeholder("C"));
            builder.addUninstantiatedTriple(mainVar,builder.placeholder("P"),builder.placeholder("I"));

            // Instantiate C and P
            
            Set<IncrementalQuery> iqueries = new HashSet<>();
            Set<IncrementalQuery> intermed = new HashSet<>();
            
            for (LexicalEntry entry : lexicon.getPropertyEntries()) {
                 for (IncrementalQuery i : iqueries) {
                      IncrementalQuery j = builder.instantiate("P1",entry,i);
                      intermed.add(j);
                 }
            }
            
            for (LexicalEntry entry : lexicon.getClassEntries()) {
                 for (IncrementalQuery i : builder.getQueries()) {
                      IncrementalQuery j = builder.instantiate("C",entry,i);
                      iqueries.add(j);
                 }
            }
            
            for (LexicalEntry entry : lexicon.getPropertyEntries()) {
                 for (IncrementalQuery i : iqueries) {
                      IncrementalQuery j = builder.instantiate("P2",entry,i);
                      intermed.add(j);
                 }
            }
            
            iqueries = intermed;
            
            // Build SELECT queries
            
            for (IncrementalQuery iquery : iqueries) {
                
                Query query = iquery.assemble(vocab,false);
                query.setQueryResultStar(true);
                queries.add(iquery.prettyPrint(query)); 
            }
            
            return queries;
        }
}
