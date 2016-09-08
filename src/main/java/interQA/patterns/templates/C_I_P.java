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
import interQA.patterns.query.QueryBuilder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.apache.jena.query.Query;


public class C_I_P extends QueryPattern{

    
	// SELECT DISTINCT ?x WHERE 
        // {
        //   ?x rdf:type <C> .
        //   ?x <P> <I> .
        // }
        
    
	public C_I_P(Lexicon lexicon,DatasetConnector dataset) {
		
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
		
            InstanceElement element3 = new InstanceElement();
            elements.add(element3);
            
            StringElement element4 = new StringElement();
            elements.add(element4);
		
            PropertyElement element5 = new PropertyElement();
            elements.add(element5);    
            
            StringElement element6 = new StringElement();
            elements.add(element6);
	}
	
	@Override
	public void update(String s) {

            ClassElement    c = (ClassElement)    elements.get(1);
            InstanceElement i = (InstanceElement) elements.get(3);
            PropertyElement p = (PropertyElement) elements.get(5);
		
            switch (currentElement) {
                
                case 0: { 
                    
                    // Create query template
                    
                    builder.reset();
                    
                    String mainVar = "x";
                    
                    builder.addUninstantiatedTypeTriple(mainVar,builder.placeholder("C"));
                    builder.addUninstantiatedTriple(mainVar,builder.placeholder("P"),builder.placeholder("I"));
                    
                    checkHowMany(s);
                    if (count) builder.addCountVar(mainVar); 
                    else       builder.addProjVar(mainVar);
                    
                    break;
                } 
                
                case 1: {
                                    
                    builder.instantiate("C",c);
                    dataset.filter(p,builder,"P"); 
                    QueryBuilder b = builder.copyAndInstantiate("P",p);
                    dataset.fillInstances(i,b,"I");
                    break;
                }
                    
                case 3: {
                    
                    builder.instantiate("I",i);
                    dataset.filter(p,builder,"P"); 
                    break;
                }
                
                case 5: { 
                                        
                    builder.instantiate("P",p);
                    for (String m : p.getMarkers()) {
                        ((StringElement) elements.get(6)).add(m);
                    }
                    break;
                } 
            }
	}
        
        @Override
        public C_I_P clone() {
            
            C_I_P clone = new C_I_P(lexicon,dataset);
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
            builder.addUninstantiatedTypeTriple(mainVar,builder.placeholder("C"));
            builder.addUninstantiatedTriple(mainVar,builder.placeholder("P"),builder.placeholder("I"));

            // Build ASK queries
            
            Set<IncrementalQuery> initialqueries = builder.getQueries();
            Set<IncrementalQuery> iqueries  = new HashSet<>();
            Set<IncrementalQuery> intermed1 = new HashSet<>();
            Set<IncrementalQuery> intermed2 = new HashSet<>();
            
            for (LexicalEntry entry : lexicon.getClassEntries()) {
                 for (IncrementalQuery i : builder.getQueries()) {
                      IncrementalQuery j = builder.instantiate("C",entry,i);
                      intermed1.add(j);
                 }
            }
            
            iqueries.addAll(intermed1);
            
            for (LexicalEntry entry : lexicon.getPropertyEntries()) {
                 for (IncrementalQuery i : iqueries) {
                      IncrementalQuery j = builder.instantiate("P",entry,i);
                      intermed2.add(j);
                 }
            }
            
            iqueries.addAll(intermed2);
            intermed2 = new HashSet<>();
            
            // fill instances in element
            builder.setQueries(intermed1);
            dataset.fillInstances(elements.get(3),builder,"I");
            // create ASK query
            for (LexicalEntry entry : elements.get(3).getActiveEntries()) {
                for (IncrementalQuery i : intermed1) {
                      IncrementalQuery j = builder.instantiate("I",entry,i);
                      intermed2.add(j);
                 }
            }
            // reset 
            builder.setQueries(initialqueries);
            elements.set(3,new InstanceElement());
                        
            for (LexicalEntry entry : lexicon.getPropertyEntries()) {
                for (IncrementalQuery i : intermed2) {
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
            builder.addUninstantiatedTypeTriple(mainVar,builder.placeholder("C"));
            builder.addUninstantiatedTriple(mainVar,builder.placeholder("P"),builder.placeholder("I"));

            // Instantiate C and P
            
            Set<IncrementalQuery> iqueries = new HashSet<>();
            Set<IncrementalQuery> intermed = new HashSet<>();
            
            for (LexicalEntry entry : lexicon.getClassEntries()) {
                 for (IncrementalQuery i : builder.getQueries()) {
                      IncrementalQuery j = builder.instantiate("C",entry,i);
                      iqueries.add(j);
                 }
            }
            
            for (LexicalEntry entry : lexicon.getPropertyEntries()) {
                 for (IncrementalQuery i : iqueries) {
                      IncrementalQuery j = builder.instantiate("P",entry,i);
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
