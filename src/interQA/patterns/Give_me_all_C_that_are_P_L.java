package interQA.patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interQA.elements.ClassElement;
import interQA.elements.LiteralElement;
import interQA.elements.PropertyElement;
import interQA.elements.StringElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.LexicalEntry.Feature;
import interQA.lexicon.Lexicon;
import interQA.lexicon.LiteralSource;
import java.util.HashSet;
import java.util.Set;

public class Give_me_all_C_that_are_P_L extends QueryPattern{
	
        /* 
        SELECT DISTINCT ?x WHERE { ?x rdf:type <Class> . ?x <Property> <Literal> . }
    
        Give me all <Class:Noun> that is|are|was|were <Property:AdjectivePP> <Literal>.
    
          Give me all conferences that are held in Berlin.
        */
	
	
	public Give_me_all_C_that_are_P_L(Lexicon lexicon,InstanceSource instances,LiteralSource literals){
		
		this.lexicon = lexicon;
		this.instances = instances;
		this.literals = literals;
		init();
	}
	
	@Override
	public void init(){
		elements = new ArrayList<>();
		
		StringElement element0 = new StringElement();
		element0.add("give me");
                element0.add("show me");
		elements.add(element0);
                
                StringElement element1 = new StringElement();
		element1.add("all",Feature.PLURAL);
                element1.add("the");
		elements.add(element1);
		
		ClassElement element2 = new ClassElement();
                element2.addEntries(lexicon, LexicalEntry.POS.NOUN, null);
		elements.add(element2);
		
		StringElement element3 = new StringElement();
		element3.add("that");
		elements.add(element3);

                StringElement element4 = new StringElement();
		element4.add("is",Feature.SINGULAR);
                element4.add("are",Feature.PLURAL);
		element4.add("was",Feature.SINGULAR);
		element4.add("were",Feature.PLURAL);
		elements.add(element4);
                
                PropertyElement element5 = new PropertyElement();
                element5.addEntries(lexicon, LexicalEntry.POS.ADJECTIVE, vocab.AdjectivePPFrame);
		elements.add(element5);
		
                LiteralElement element6 = new LiteralElement();
		elements.add(element6);
	}
	
	@Override
	public void update(String s){
		
            switch (currentElement) {
                
                case 1: ((StringElement) elements.get(1)).transferFeatures(elements.get(2),s); break;
            
                case 2: {
                    setFeatures(2,4,s);
                                        
                    Map<String,List<LexicalEntry>> old_element2index = elements.get(2).getIndex();
                    Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
    		
                    for(LexicalEntry entry : elements.get(2).getActiveEntries()){
    			
    			new_element2index.putAll(instances.filterByClassForProperty(old_element2index, LexicalEntry.SynArg.SUBJECT, entry.getReference()));
                    }
                    elements.get(5).addToIndex(new_element2index);
		}
		
                case 5: {
			
			elements.get(6).addToIndex(literals.getJustLiteralByProperty(elements.get(5).getActiveEntries()));
                }
            }
	}
	
	@Override
	public Set<String> buildSPARQLqueries(){
				
		ClassElement    noun = (ClassElement) elements.get(2);
                PropertyElement adjective = (PropertyElement) elements.get(5);
                LiteralElement literal = (LiteralElement) elements.get(6);
		
                switch (currentElement) {
                    
                    // case 2: TODO
                    
                    // case 5: TODO
                    
                    case 6: return sqb.BuildQueryForClassAndPropertyAndLiteral(noun,adjective,literal);
                        
                    default: return new HashSet<>();
                }
	}
}
