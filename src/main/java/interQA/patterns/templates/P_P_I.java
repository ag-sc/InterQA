package interQA.patterns.templates;

import interQA.elements.InstanceElement;
import interQA.elements.PropertyElement;
import interQA.elements.StringElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mirtik, cunger
 */
public class P_P_I extends QueryPattern{
	

        // SELECT DISTINCT ?x ?y WHERE 
        // {
        //   <I> <P1> ?x .
        //   <I> <P2> ?y . 
        // }
    
	
	public P_P_I(Lexicon lexicon,DatasetConnector instances){
				            
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
		
            InstanceElement element5 = new InstanceElement();
            elements.add(element5);
		
            StringElement element6 = new StringElement();
            elements.add(element6);
	}
        
	@Override
	public void update(String s) {
	
            PropertyElement p1 = (PropertyElement) elements.get(1);
            PropertyElement p2 = (PropertyElement) elements.get(3);
            InstanceElement i  = (InstanceElement) elements.get(5);
                    
            switch (currentElement) {
                        
                case 0: {
                    
                    // Create query template 
                    
                    builder.reset();
                            
                    String mainVar1 = "x";
                    String mainVar2 = "y";
                            
                    builder.addUninstantiatedTriple(builder.placeholder("I"),builder.placeholder("P1"),mainVar1);
                    builder.addUninstantiatedTriple(builder.placeholder("I"),builder.placeholder("P2"),mainVar2);

                    checkHowMany(s); // TODO not used in this pattern
                    builder.addProjVar(mainVar1);
                    builder.addProjVar(mainVar2);

                    break;
                }
                    
                case 1: {
                           
                    builder.instantiate("P1",p1);
                    dataset.filter(elements.get(3),builder,"P2");
                    
                    // avoid P2=P1
                    Set<LexicalEntry> del = new HashSet<>();
                    for (LexicalEntry entry : elements.get(3).getActiveEntries()) {
                         if (elements.get(1).getActiveEntries().contains(entry)) {
                             del.add(entry);
                         }
                    }
                    elements.get(3).getActiveEntries().removeAll(del);
                    break;
                }
                        
                case 3: {

                    for (String m : elements.get(3).getMarkers()) {
                       ((StringElement) elements.get(4)).add(m);
                    }
                            
                    builder.instantiate("P2",p2);
                    dataset.fillInstances(elements.get(5),builder,"I");
                    break;
                }
                
                case 5: {
                    
                    builder.instantiate("I",i);
                    break;
                }
            }
        }

}

