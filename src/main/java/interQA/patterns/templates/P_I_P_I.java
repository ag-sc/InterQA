package interQA.patterns.templates;

import interQA.elements.ClassElement;
import interQA.elements.InstanceElement;
import interQA.elements.PropertyElement;
import interQA.elements.StringElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.Lexicon;


/**
 *
 * @author cunger
 */
public class P_I_P_I extends QueryPattern{
    
    
    // SELECT DISTINCT ?x WHERE 
    // {
    //   ?x <P1> <I1> .
    //   ?x <P2> <I2> . 
    // }
    
    
    boolean flag = false;
    
    
    public P_I_P_I(Lexicon lexicon,DatasetConnector dataset){
        	
            this.lexicon = lexicon;
            this.dataset = dataset;
		
            sqb.setEndpoint(dataset.getEndpoint());

            init();
    }
    
    @Override
	public void init() {
           
            StringElement element0 = new StringElement();
            elements.add(element0);

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
            
            PropertyElement p1 = (PropertyElement) elements.get(3);
            InstanceElement i1 = (InstanceElement) elements.get(5);
            PropertyElement p2 = (PropertyElement) elements.get(7);
            InstanceElement i2 = (InstanceElement) elements.get(9);
		
            switch (currentElement) {
                    
                case 0: {
                        
                    // Create query template 
                        
                    builder.reset();
                
                    String mainVar = "x";
                    
                    builder.addUninstantiatedTriple(mainVar,"P1","I1");
                    builder.addUninstantiatedTriple(mainVar,"P2","I2");
                    
                    checkHowMany(s);
                    if (count) builder.addCountVar(mainVar); 
                    else       builder.addProjVar(mainVar);
                        
                    // Propagate features 
                    
                    ((StringElement) elements.get(0)).transferFeatures(elements.get(1),s);
                    
                    break;
                }
		
                case 3: {
                        
                    setFeatures(3,4,s);
                    for (String m : elements.get(3).getMarkers()) {
                        ((StringElement) elements.get(4)).add(m);
                    }
                        
                    builder.instantiate("P1",p1.getActiveEntries());
                    dataset.fillInstances(elements.get(5),builder,"I1");
                    
                    break;
                }
                    
                case 4: {
                        
                    ((StringElement) elements.get(4)).transferFeatures(elements.get(5),s);
                    break;
                }
                    
                case 5: {
                        
                    setFeatures(5,6,s);
                    for (String key :elements.get(3).getActiveEntriesKey()){
                         elements.get(5).getIndex().remove(key);
                    }
                        
                    builder.instantiate("I1",i1.getActiveEntries());
                    dataset.filter(elements.get(7),builder,"P2");
                    
                    break;
                }
                    
                case 6: {
                        
                    ((StringElement) elements.get(6)).transferFeatures(elements.get(7),s);
                    break;
                }
                    
                case 7: {
                        
                    setFeatures(7,8,s);
                    for (String m : elements.get(7).getMarkers()) {
                       ((StringElement) elements.get(8)).add(m);
                    }
                        
                    builder.instantiate("P2",p2.getActiveEntries());
                    dataset.fillInstances(elements.get(9),builder,"I2");
		
                    break;
                }
                    
                case 8: {
                        
                    ((StringElement) elements.get(8)).transferFeatures(elements.get(9),s);
                    break;
                }
                    
                case 9: {
                        
                    builder.instantiate("I2",i2.getActiveEntries());
                }
            }
	}

}
