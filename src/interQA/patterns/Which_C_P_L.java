package interQA.patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interQA.elements.LiteralElement;
import interQA.elements.ClassElement;
import interQA.elements.PropertyElement;
import interQA.elements.StringElement;

import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import interQA.lexicon.LiteralSource;
import java.util.HashSet;
import java.util.Set;



public class Which_C_P_L extends QueryPattern {
	
    
	/* 
        SELECT ?x WHERE { ?x rdf:type <Class> . ?x <Property> <Literal> . } 

        Which <Class:Noun> <Property:Verb> <Literal>?
	
	  Which conferences took place in 2015?
          Which actors died in 1999?
        */
    
    
	public Which_C_P_L(Lexicon lexicon,InstanceSource instances,LiteralSource literals){
		
		this.lexicon = lexicon;
		this.instances = instances;
		this.literals = literals;
		
		init();
	}
	
	@Override
	public void init(){
		elements = new ArrayList<>();
		
		StringElement element0 = new StringElement();
		element0.add("which");
		elements.add(element0);
		
		ClassElement element1 = new ClassElement();
                element1.addEntries(lexicon, LexicalEntry.POS.NOUN, null);
		elements.add(element1);	
		
		PropertyElement element2 = new PropertyElement();
                element2.addEntries(lexicon, LexicalEntry.POS.VERB, vocab.TransitiveFrame);
                element2.addEntries(lexicon, LexicalEntry.POS.VERB, vocab.IntransitivePPFrame);
		elements.add(element2);
		
		LiteralElement element3 = new LiteralElement();
		elements.add(element3);
	}
	
	@Override
	public void update(String s){
                
            switch (currentElement){ 
            
                case 1: {
                    
                    setFeatures(1,2,s);
                    
                    Map<String,List<LexicalEntry>> old_element2index = elements.get(1).getIndex();
       	            Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
    		
                    for(LexicalEntry entry : elements.get(1).getActiveEntries()){
    			
    			new_element2index.putAll(instances.filterByClassForProperty(old_element2index, LexicalEntry.SynArg.SUBJECT, entry.getReference()));                      
                    }
                    elements.get(2).addToIndex(new_element2index);
                    break;
		}
		
                case 2: {
	
                    elements.get(3).addToIndex(literals.getJustLiteralByProperty(elements.get(2).getActiveEntries()));
                    break;
		}
            }
	}
	
	@Override
	public Set<String> buildSPARQLqueries(){
				
		ClassElement    noun    = (ClassElement)    elements.get(1);
                PropertyElement verb    = (PropertyElement) elements.get(2);
                LiteralElement  literal = (LiteralElement)  elements.get(3);
		
                switch (currentElement) {
                    
                    // case 1: TODO
                    
                    // case 2: TODO
                    
                    case 3: return sqb.BuildQueryForClassAndPropertyAndLiteral(noun,verb,literal);
                        
                    default: return new HashSet<>();
                }
	}
	
	

}
