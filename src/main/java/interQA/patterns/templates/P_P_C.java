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
public class P_P_C extends QueryPattern {
	
    
        // SELECT DISTINCT ?x ?y WHERE 
        // {
        //   ?I rdf:type <C> .
        //   ?I <P1> ?x .
        //   ?I <P2> ?y . 
        // }
    
	
	public P_P_C(Lexicon lexicon,DatasetConnector instances) {
				            
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
		
            ClassElement element5 = new ClassElement();
            elements.add(element5);
            
            StringElement element6 = new StringElement();
            elements.add(element6);
		
	}
        
	@Override
	public void update(String s) {

            PropertyElement p1 = (PropertyElement) elements.get(1);
            PropertyElement p2 = (PropertyElement) elements.get(3);
            ClassElement    c  = (ClassElement)    elements.get(5); 
            
            switch (currentElement) {
                        
                case 0: {
                    
                    // Create query template 
                    
                    builder.reset();
                    
                    String mainVar1 = "x";
                    String mainVar2 = "y";
                    String iVar     = "I";
                    
                    builder.addUninstantiatedTypeTriple(iVar,builder.placeholder("C"));
                    builder.addUninstantiatedTriple(iVar,builder.placeholder("P1"),mainVar1);
                    builder.addUninstantiatedTriple(iVar,builder.placeholder("P2"),mainVar2);
                    
                    checkHowMany(s); // TODO not used in this pattern
                    builder.addProjVar(mainVar1);
                    builder.addProjVar(mainVar2);
                    
                    break;
                }
                        
                case 1: {
                    
                    builder.instantiate("P1",p1);
                    dataset.filter(elements.get(3),builder,"P2");
                    break;
                }

                case 3: {

                    for (String m : elements.get(3).getMarkers()) {
                       ((StringElement) elements.get(4)).add(m);
                    }
                    
                    builder.instantiate("P2",p2);
                    dataset.filter(elements.get(5),builder,"C");
                    break;
                }
                        
                case 5: {

                    builder.instantiate("C",c);
                    break;
                }
            }
		
	}
        
        @Override
        public P_P_C clone() {
            
            P_P_C clone = new P_P_C(lexicon,dataset);
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
            String iVar     = "I";
            builder.addProjVar(mainVar1);
            builder.addProjVar(mainVar2);         
            builder.addUninstantiatedTypeTriple(iVar,builder.placeholder("C"));
            builder.addUninstantiatedTriple(iVar,builder.placeholder("P1"),mainVar1);
            builder.addUninstantiatedTriple(iVar,builder.placeholder("P2"),mainVar2);

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
            intermed = new HashSet<>();
            
            for (LexicalEntry entry : lexicon.getClassEntries()) {
                 for (IncrementalQuery i : iqueries) {
                      IncrementalQuery j = builder.instantiate("C",entry,i);
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

