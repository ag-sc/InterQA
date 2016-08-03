package interQA.patterns.templates;


import interQA.elements.ClassElement;
import interQA.elements.InstanceElement;
import interQA.elements.PropertyElement;
import interQA.elements.StringElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.Lexicon;


public class C_P_I extends QueryPattern{

    
	// SELECT DISTINCT ?x WHERE 
        // {
        //   ?x rdf:type <C> .
        //   ?x <P> <I> .
        // }
        
    
	public C_P_I(Lexicon lexicon,DatasetConnector dataset){
		
            this.lexicon = lexicon;
            this.dataset = dataset;

            init();
	}
        

	@Override
	public void init(){
            
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
	}
	
	@Override
	public void update(String s) {
            
            ClassElement    c = (ClassElement)    elements.get(1);
            PropertyElement p = (PropertyElement) elements.get(3);
            InstanceElement i = (InstanceElement) elements.get(5);
		
            switch (currentElement) {
                
                case 0: { 
                    
                    // Create query template
                    
                    builder.reset();
                    
                    String mainVar = "x";
                    
                    builder.addUninstantiatedTypeTriple(mainVar,"C");
                    builder.addUninstantiatedTriple(mainVar,"P","I");
                    
                    checkHowMany(s);
                    if (count) builder.addCountVar(mainVar); 
                    else       builder.addProjVar(mainVar);
                
                    // Propagate features
                    
                    ((StringElement) elements.get(0)).transferFeatures(elements.get(1),s);
                    
                    break;
                } 
                
                case 1: {
                
                    setFeatures(1,2,s);
                    
                    builder.instantiate("C",c.getActiveEntries());
                    dataset.filter(elements.get(3),builder,"P");
                    break;
                }
                
                case 2: { 
                    
                    ((StringElement) elements.get(2)).transferFeatures(elements.get(3),s);
                    break;
                }  
                    
                case 3: {
                    
                    setFeatures(3,4,s);
                    for (String m : elements.get(3).getMarkers()) {
                        ((StringElement) elements.get(4)).add(m);
                    }
                    
                    builder.instantiate("P",p.getActiveEntries());
                    dataset.fillInstances(elements.get(5),builder,"I");
                    break;
                }
                    
                case 4: {
                    
                    ((StringElement) elements.get(4)).transferFeatures(elements.get(5),s);
                    break;
                }
                
                case 5: { 
                    
                    setFeatures(5,6,s);
                    
                    builder.instantiate("I",i.getActiveEntries());
                    break;
                } 
            }
	}

}
