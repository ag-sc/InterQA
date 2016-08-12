package interQA.patterns.templates;

import interQA.elements.ClassElement;
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
 * @author cunger
 */
public class C_P_P_I extends QueryPattern{
    
    
    // SELECT DISTINCT ?x WHERE 
    // {
    //   ?x rdf:type <C> .
    //   ?x <P1> <I> .
    //   ?x <P2> <I> . 
    // }
        
    
    public C_P_P_I(Lexicon lexicon,DatasetConnector dataset){
        	
            this.lexicon = lexicon;
            this.dataset = dataset;
		
            init();
    }
    
    @Override
	public void init() {
            	
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
            
            ClassElement    c  = (ClassElement)    elements.get(1);
            PropertyElement p1 = (PropertyElement) elements.get(3);
            PropertyElement p2 = (PropertyElement) elements.get(7);
            InstanceElement i  = (InstanceElement) elements.get(9);
		
            switch (currentElement) {
                    
                case 0: {
                        
                    // Create query template 
                        
                    builder.reset();
                
                    String mainVar = "x";
                    
                    builder.addUninstantiatedTypeTriple(mainVar,builder.placeholder("C"));
                    builder.addUninstantiatedTriple(mainVar,builder.placeholder("P1"),builder.placeholder("I"));
                    builder.addUninstantiatedTriple(mainVar,builder.placeholder("P2"),builder.placeholder("I"));
                    
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
                        
                    for (String m : elements.get(3).getMarkers()) {
                        ((StringElement) elements.get(4)).add(m);
                    }
                        
                    builder.instantiate("P1",p1);
                    dataset.fillInstances(elements.get(5),builder,"I");
                    
                    // avoid P2=P1
                    Set<LexicalEntry> del = new HashSet<>();
                    for (LexicalEntry entry : elements.get(7).getActiveEntries()) {
                         if (elements.get(3).getActiveEntries().contains(entry)) {
                             del.add(entry);
                         }
                    }
                    elements.get(7).getActiveEntries().removeAll(del);
                    break;
                }
                    
                case 7: {
                        
                    for (String m : elements.get(7).getMarkers()) {
                       ((StringElement) elements.get(8)).add(m);
                    }
                        
                    builder.instantiate("P2",p2);
                    dataset.fillInstances(elements.get(9),builder,"I");
                    break;
                }
                    
                case 9: {
                        
                    builder.instantiate("I",i);
                    break;
                }
            }
	}
        
        @Override
        public C_P_P_I clone() {
            
            C_P_P_I clone = new C_P_P_I(lexicon,dataset);
            clone.elements = elements;
            clone.agreement = agreement;
            clone.builder = builder.clone();
            
            return clone;
        }

}
