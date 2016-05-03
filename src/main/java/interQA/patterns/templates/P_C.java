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
public class P_C extends QueryPattern{

    
        /*
        SELECT ?y WHERE { ?x rdf:type <Class>. ?x <Property> ?y. }
        */ 

            public P_C(Lexicon lexicon,DatasetConnector dataset){
                
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
		
                switch (currentElement) {
                
                    case 0: {checkHowMany(s);((StringElement) elements.get(0)).transferFeatures(elements.get(1),s);
                }break;
                    case 2: ((StringElement) elements.get(2)).transferFeatures(elements.get(3),s); break;
                    case 3: setFeatures(3,4,s); break;

                    case 1: {
                        
                        setFeatures(1,2,s);
					
                        elements.get(4).addToIndex(dataset.filterByPropertyForInstances(elements.get(1).getActiveEntries(),LexicalEntry.SynArg.OBJECT)); 
                        break;
                    }
		}
            }

            @Override
            public Set<String> buildSPARQLqueries(){
								
		PropertyElement p = (PropertyElement) elements.get(1);
		ClassElement    c = (ClassElement)    elements.get(4);
			
                switch (currentElement) {
                
                    case 1: queries = sqb.BuildQueryForProperty(p);
                    
                    case 3: queries = sqb.BuildQueryForClassAndProperty(c,p,LexicalEntry.SynArg.OBJECT,count);                        
                }
                
                return queries;
            }
			
}
