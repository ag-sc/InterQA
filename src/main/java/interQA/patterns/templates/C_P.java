package interQA.patterns.templates;


import interQA.elements.ClassElement;
import interQA.elements.StringElement;
import interQA.elements.PropertyElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.Lexicon;

/**
*
* @author mince, cunger
*/
public class C_P extends QueryPattern{

								
            // SELECT DISTINCT ?x WHERE 
            // {
            //   ?x rdf:type <C> .
            //   ?x <P> ?y .
            // }

            public C_P(Lexicon lexicon,DatasetConnector dataset){
                
                this.lexicon = lexicon;
                this.dataset = dataset; 

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

                ClassElement element3 = new ClassElement();
                elements.add(element3);
                
                StringElement element4 = new StringElement();
                elements.add(element4);
            }
	        
            @Override
            public void update(String s) {
		    
                ClassElement    c = (ClassElement)    elements.get(1);
                PropertyElement p = (PropertyElement) elements.get(3);
                
                switch (currentElement) {
                
                    case 0: {
                        
                        // Create query template
                        
                        builder.reset();
                        
                        String mainVar = "x";

                        builder.addUninstantiatedTypeTriple(mainVar,"C");
                        builder.addUninstantiatedTriple(mainVar,"P","y");

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
                        dataset.filter(elements.get(3),builder,"C");
                        break;
                    }
                    
                    case 3: {
                                                
                        builder.instantiate("C",c);
                        break;
                    } 
		}
            }
			
}
