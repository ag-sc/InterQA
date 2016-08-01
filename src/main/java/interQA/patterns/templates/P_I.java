package interQA.patterns.templates;

import interQA.elements.InstanceElement;
import interQA.elements.StringElement;
import interQA.elements.PropertyElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import java.util.Set;

/**
 *
 * @author cunger
 */
public class P_I extends QueryPattern {
    
    
        /*
        SELECT ?x WHERE { ?x <Property> <Instance> . } 
        SELECT ?x WHERE { <Instance> <Property> ?x . } 
        */
    
    
	public P_I(Lexicon lexicon, DatasetConnector instances) {
            
            this.lexicon = lexicon;
            this.dataset = instances; 
            
            init();
        }
        
        @Override
        public void init() {  
            
            sqb.setEndpoint(dataset.getEndpoint());
            
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
            
            switch (currentElement) {
                
                case 0: {
                    
                    checkHowMany(s);
                    ((StringElement) elements.get(0)).transferFeatures(elements.get(1),s); 
                    break;
                }
            
                case 1: {
                    
                    setFeatures(1,2,s);

                    elements.get(3).addToIndex(dataset.filterByPropertyForInstances(elements.get(1).getActiveEntries(), LexicalEntry.SynArg.OBJECT));  
                    
                    for (String m : elements.get(1).getMarkers()) {
                        ((StringElement) elements.get(2)).add(m);
                    }
                    break;
                }
                
                case 2: { 
                        
                    ((StringElement) elements.get(2)).transferFeatures(elements.get(3),s);
                    break;
                } 
                    
                case 3: {
                        
                    setFeatures(3,4,s);
                    break;
                } 
            }
        }        
        
        @Override
	public Set<String> buildSPARQLqueries() {
                        
            // SELECT DISTINCT ?x WHERE 
            // {
            //   ?x <P> <I> .
            // }
            		
            String mainVar = "x";
            
            PropertyElement p = (PropertyElement) elements.get(1);
            InstanceElement i = (InstanceElement) elements.get(3);
		
            switch (currentElement) {
                    
                case 0: {
                    
                    builder.reset();
                    
                    if (count) builder.addCountVar(mainVar); 
                    else       builder.addProjVar(mainVar);
                    break;
                } 

                case 1: { // + ?x <P> ?I .

                    builder.addTriple(mainVar,p.getActiveEntries(),"I");
                    break;
                }
                    
                case 3: { // ?I -> <I>
                    
                    builder.instantiate("I",i.getActiveEntries());
                    break;
                }
            }
            
            return builder.returnQueries();
	}
}
