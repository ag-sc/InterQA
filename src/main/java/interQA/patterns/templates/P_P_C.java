package interQA.patterns.templates;

import interQA.elements.ClassElement;
import interQA.elements.PropertyElement;
import interQA.elements.StringElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.Lexicon;

/**
 *
 * @author mirtik, cunger
 */
public class P_P_C extends QueryPattern{
	
    
        // SELECT DISTINCT ?x ?y WHERE 
        // {
        //   ?i rdf:type <C> .
        //   ?i <P1> ?x .
        //   ?i <P2> ?y . 
        // }
    
	
	public P_P_C(Lexicon lexicon,DatasetConnector instances){
				            
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
                    String iVar     = "i";
                    
                    builder.addUninstantiatedTypeTriple(iVar,"C");
                    builder.addUninstantiatedTriple(iVar,"P1",mainVar1);
                    builder.addUninstantiatedTriple(iVar,"P2",mainVar2);
                    
                    checkHowMany(s); // TODO not used in this pattern
                    builder.addProjVar(mainVar1);
                    builder.addProjVar(mainVar2);
                    
                    break;
                }
                        
                case 1: {
                    
                    builder.instantiate("P1",p1.getActiveEntries());
                    dataset.filter(elements.get(3),builder,"P2");
                    break;
                }

                case 3: {

                    for (String m : elements.get(3).getMarkers()) {
                       ((StringElement) elements.get(4)).add(m);
                    }
                    
                    builder.instantiate("P2",p2.getActiveEntries());
                    dataset.filter(elements.get(5),builder,"C");
                    break;
                }
                        
                case 5: {

                    builder.instantiate("C",c.getActiveEntries());
                    break;
                }
            }
		
	}

}

