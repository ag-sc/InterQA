package interQA.patterns;

import interQA.elements.ClassElement;
import interQA.elements.StringElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author cunger
 */
public class Give_me_all_C extends QueryPattern {
    
    
        /*
        SELECT ?s WHERE { ?x rdf:type <Class> . }

        List|(Give|Show me) all <Noun:Class>.

          Give me all cities. 
          List all movies.
          Show me all mountains. 
        */ 

    
        public Give_me_all_C(Lexicon lexicon, InstanceSource instances) {
                            
            this.lexicon = lexicon;
            this.instances = instances; 
            
            init();
        }
        
        @Override
        public void init() {
            
            elements = new ArrayList<>();
        
            StringElement element0 = new StringElement(); 
            element0.add("give me");
            element0.add("show me");
            element0.add("list");
            elements.add(element0);

            StringElement element1 = new StringElement(); 
            element1.add("all",LexicalEntry.Feature.PLURAL);
            elements.add(element1);
            
            ClassElement element2 = new ClassElement(); 
            element2.addEntries(lexicon, LexicalEntry.POS.NOUN, null);
            elements.add(element2);
            
            StringElement element3 = new StringElement();
            element3.add(".");
            elements.add(element3);
        }

        @Override 
        public void updateAt(int i, String s) {
            
            if (i==1) {
            
                ((StringElement) elements.get(1)).transferFeatures(elements.get(2),s);
            }
        }

        @Override
        public Set<String> buildSPARQLqueries() {

            return sqb.BuildQueryForClassInstances(elements.get(2).getActiveEntries());
        }

}
