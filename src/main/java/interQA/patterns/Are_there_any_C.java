package interQA.patterns;

import interQA.elements.ClassElement;
import interQA.elements.StringElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import java.util.HashSet;
import java.util.Set;


public class Are_there_any_C extends QueryPattern{
	
	//SELECT ?x WHERE { ?x rdf:type dbp-owl:Song . }
	
	//Do you know any animals?
	//Do you know any NBA players?
	//Do you know any surfers?
	
	//Do you know any <Noun:Class>
	
	public Are_there_any_C(Lexicon lexicon,InstanceSource instances){
		
            this.lexicon = lexicon;
            this.instances = instances;
	
            init();
		
	}
	
	@Override
	public void init(){
		
            StringElement element0 = new StringElement ();
            element0.add("do you know any");
            element0.add("are there any");
            elements.add(element0);
		
            ClassElement element1 = new ClassElement();
            element1.addEntries(lexicon, LexicalEntry.POS.NOUN, null);
            elements.add(element1);
	}

        @Override
        public void update(String s) {
            
            if (currentElement == 0) {
                
                ((StringElement) elements.get(0)).transferFeatures(elements.get(1),s);
            }
        }

        @Override
        public Set<String> buildSPARQLqueries() {

            if (currentElement > 0) {
                return sqb.BuildQueryForClassInstances(elements.get(4).getActiveEntries());
            }
            return new HashSet<>();
        }	
}