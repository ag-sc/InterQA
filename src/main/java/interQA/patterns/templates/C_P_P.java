package interQA.patterns.templates;

import interQA.elements.ClassElement;
import interQA.elements.Element;
import interQA.elements.PropertyElement;
import interQA.elements.StringElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import interQA.patterns.query.IncrementalQuery;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mirtik, cunger
 */
public class C_P_P extends QueryPattern{
	
        
        // SELECT DISTINCT ?x WHERE 
        // {
        //   ?x rdf:type <C> .
        //   ?x <P1> ?y .
        //   ?x <P2> ?z . 
        // }
        
    
	public C_P_P(Lexicon lexicon, DatasetConnector instances){
		
		this.lexicon = lexicon;
		this.dataset = instances;

                init();
	}
	
	@Override
	public void init(){
		
		StringElement element0 = new StringElement();
		elements.add(element0);
		
		ClassElement element1 = new ClassElement();
		elements.add(element1);
                
                StringElement element2 = new StringElement();
		elements.add(element2);
		
		PropertyElement element3 = new PropertyElement();
		elements.add(element3);
		
		StringElement element4 = new StringElement();
		elements.add(element4);
		
		PropertyElement element5 = new PropertyElement();
		elements.add(element5);	
		
                StringElement element6 = new StringElement();
		elements.add(element6);
	}
	
	
	@Override
	public void update(String s){
		
            ClassElement    c  = (ClassElement)    elements.get(1);
            PropertyElement p1 = (PropertyElement) elements.get(2);
            PropertyElement p2 = (PropertyElement) elements.get(4);
            
            switch (currentElement) {
                    
                case 0: {
                        
                    // Create query template 
                        
                    builder.reset();
                    
                    String mainVar = "x";

                    builder.addUninstantiatedTypeTriple(mainVar,builder.placeholder("C"));
                    builder.addUninstantiatedTriple(mainVar,builder.placeholder("P1"),"y");
                    builder.addUninstantiatedTriple(mainVar,builder.placeholder("P2"),"z");

                    checkHowMany(s);
                    if (count) builder.addCountVar(mainVar); 
                    else       builder.addProjVar(mainVar);

                    break;
                } 
                 
                case 1: {
                                            
                    builder.instantiate("C",c);
                    dataset.filter(elements.get(3),builder,"P1");
                    break;
                }
                    
                case 3: {
                                                
                    builder.instantiate("P1",p1);
                    dataset.filter(elements.get(5),builder,"P2");
                    break;
                }
                
                case 5: {
                    
                    builder.instantiate("P2",p2);
                    break;
                }
            }
                
	}
        
        @Override
        public C_P_P clone() {
            
            C_P_P clone = new C_P_P(lexicon,dataset);
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
            builder.addProjVar(mainVar);
            builder.addUninstantiatedTypeTriple(mainVar,builder.placeholder("C"));
            builder.addUninstantiatedTriple(mainVar,builder.placeholder("P1"),"y");
            builder.addUninstantiatedTriple(mainVar,builder.placeholder("P2"),"z");

            // Build ASK queries
            
            Set<IncrementalQuery> iqueries = new HashSet<>();
            Set<IncrementalQuery> intermed = new HashSet<>();
            
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

}
