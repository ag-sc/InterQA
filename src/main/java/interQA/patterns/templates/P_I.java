package interQA.patterns.templates;

import interQA.elements.InstanceElement;
import interQA.elements.StringElement;
import interQA.elements.PropertyElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.Lexicon;

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

}
