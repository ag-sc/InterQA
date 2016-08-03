package interQA.patterns.templates;

import interQA.elements.InstanceElement;
import interQA.elements.StringElement;
import interQA.elements.PropertyElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.Lexicon;
import java.util.Set;

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
    
            PropertyElement p = (PropertyElement) elements.get(1);
            InstanceElement i = (InstanceElement) elements.get(3);
            
            switch (currentElement) {
                
                case 0: {
                    
                    // Create query template
                    
                    builder.reset();
             
                    String mainVar = "x";
                    
                    builder.addUninstantiatedTriple(mainVar,"P","I");
                   
                    if (count) builder.addCountVar(mainVar); 
                    else       builder.addProjVar(mainVar);
                    
                    // Propagate features
                    
                    checkHowMany(s);
                    ((StringElement) elements.get(0)).transferFeatures(elements.get(1),s); 
                    break;
                }
            
                case 1: {
                    
                    setFeatures(1,2,s);
                    for (String m : elements.get(1).getMarkers()) {
                        ((StringElement) elements.get(2)).add(m);
                    }
                    
                    builder.instantiate("P",p.getActiveEntries());
                    dataset.fillInstances(elements.get(3),builder,"I");
                    break;
                }
                
                case 2: { 
                        
                    ((StringElement) elements.get(2)).transferFeatures(elements.get(3),s);
                    break;
                } 
                    
                case 3: {
                        
                    setFeatures(3,4,s);
                    
                    builder.instantiate("I",i.getActiveEntries());
                    break;
                } 
            }
        }        

}
