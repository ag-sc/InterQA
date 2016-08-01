package interQA.patterns.templates;


import interQA.elements.ClassElement;
import interQA.elements.StringElement;
import interQA.elements.PropertyElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import java.util.*;
/**
*
* @author mince
*/
public class C_P extends QueryPattern{

    
        /*
        SELECT ?y WHERE { ?x rdf:type <Class>. ?x <Property> ?y. }
        */ 

            public C_P(Lexicon lexicon,DatasetConnector dataset){
                
                this.lexicon = lexicon;
                this.dataset = dataset; 
                sqb.setEndpoint(dataset.getEndpoint());
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
		
                switch (currentElement) {
                
                    case 0: {
                        
                        checkHowMany(s);
                        ((StringElement) elements.get(0)).transferFeatures(elements.get(1),s);
                        break;
                    }
                        
                    case 1: {
                        
                        setFeatures(1,2,s);
					
                        elements.get(4).addToIndex(dataset.filterByPropertyForInstances(elements.get(1).getActiveEntries(),LexicalEntry.SynArg.OBJECT)); 
                        
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
            public Set<String> buildSPARQLqueries(){
								
                // SELECT DISTINCT ?x WHERE 
                // {
                //   ?x rdf:type <C> .
                //   ?x <P> ?y .
                // }

                String mainVar = "x";

                ClassElement    c = (ClassElement)    elements.get(1);
                PropertyElement p = (PropertyElement) elements.get(3);

                builder.reset();

                switch (currentElement) {

                    case 1: { // + ?x rdf:type <C> .

                        if (count) builder.addCountVar(mainVar); 
                        else       builder.addProjVar(mainVar);

                        builder.addTypeTriple(mainVar,c.getActiveEntries());
                        break;
                    }

                    case 3: { // + ?x <P> ?y .

                        builder.addTriple(mainVar,p.getActiveEntries(),"y");
                        break;
                    }

                }

                return builder.returnQueries();        
            }
			
}
