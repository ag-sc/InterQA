package interQA.patterns;


import interQA.elements.ClassElement;
import interQA.elements.StringElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.LexicalEntry.Feature;
import interQA.lexicon.Lexicon;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Which_C_are_there extends QueryPattern {
	
	/*
        SELECT ?x WHERE { ?x rdf:type <Class> . }
	
	What <Noun:Class> are there?
        Which <Noun:Class> do you know?	
        */
    
	public Which_C_are_there(Lexicon lexicon,InstanceSource instances){
		
            this.lexicon = lexicon;
            this.instances = instances;
		
            init();	
	}
	
	@Override
	public void init(){
		
            elements = new ArrayList<>();
		
            StringElement element0 = new StringElement();
            element0.add("what");
            element0.add("which");
            elements.add(element0);
		
            ClassElement element1 = new ClassElement();
            element1.addEntries(lexicon, LexicalEntry.POS.NOUN, null);
            elements.add(element1);
	
            StringElement element2 = new StringElement();
            element2.add("is there",Feature.SINGULAR);
            element2.add("are there",Feature.PLURAL);
            element2.add("exist");
            element2.add("do you know");
            elements.add(element2);
	}
	
        @Override
        public void update(String s) {
            
            if (currentElement == 1) setFeatures(1,2,s);
        }
        
	@Override
	public Set<String> buildSPARQLqueries() {
	       
            if (currentElement == 2) return sqb.BuildQueryForClassInstances(elements.get(1).getActiveEntries());
            
            return new HashSet<>();
	}	
}
