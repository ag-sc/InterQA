package interQA.patterns.templates;

import interQA.elements.InstanceElement;
import interQA.elements.StringElement;
import interQA.elements.PropertyElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author cunger
 */
public class P_I extends QueryPattern {
    
    
        /*
        SELECT ?x WHERE { ?x <Property> <Instance> . } 
        SELECT ?x WHERE { <Instance> <Property> ?x . } 
        */
    
    
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
            
            switch (currentElement) {
                
                case 0: {checkHowMany(s);((StringElement) elements.get(0)).transferFeatures(elements.get(1),s);
                }break;
                case 1: ((StringElement) elements.get(1)).transferFeatures(elements.get(2),s); break;
                case 2: ((StringElement) elements.get(2)).transferFeatures(elements.get(3),s); break;
            
                case 3: {

                    elements.get(4).addToIndex(dataset.filterByPropertyForInstances(elements.get(3).getActiveEntries(), LexicalEntry.SynArg.OBJECT));  
                    break;
                }
            }
        }        
        
        @Override
	public Set<String> buildSPARQLqueries() {
                        
            PropertyElement p = (PropertyElement) elements.get(1);
            InstanceElement i = (InstanceElement) elements.get(3);
                 
            switch (currentElement) {
                
                case 2: queries = sqb.BuildQueryForProperty(p); break;
                
                case 3: queries = sqb.BuildQueryForPropertyAndInstance(p,i,count); break;          
            }
            
            return queries;
	}
}
