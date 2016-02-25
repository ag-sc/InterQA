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
import interQA.lexicon.Lexicon;
import interQA.lexicon.LiteralSource;
import java.util.HashSet;
import java.util.Set;

public class Which_C_P_L_P_L extends QueryPattern {
	
    
        /*
        SELECT DISTINCT ?uri { ?uri rdf:type <Class> . ?uri <Property> <Literal> . ?uri <Property> <Literal> . }
	
        Which <Class:Noun> <Property:Verb> <Literal> <Property:Preposition> <Literal> ?
	
	  Which conferences took place in 2015 in Berlin?
	*/
    
	
	public Which_C_P_L_P_L(Lexicon lexicon,InstanceSource instances,LiteralSource literals){
	
		this.lexicon = lexicon;
		this.instances = instances;
		this.literals = literals;
		
		init();
	}

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
		
		PropertyElement element4 = new PropertyElement();
                element4.addEntries(lexicon, LexicalEntry.POS.PREPOSITION, vocab.PrepositionalFrame);
		elements.add(element4);
		
		LiteralElement element5 = new LiteralElement();
		elements.add(element5);
	}
	
	
	@Override
	public void update(String s){
		
            switch (currentElement) {
                
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
		
                case 4: {
			
                    elements.get(5).addToIndex(literals.getLiteralByPropertyAndLiteral(elements.get(4).getActiveEntries(),elements.get(2).getActiveEntries(),
                    elements.get(3).getActiveEntries()));
                    break;
		}
            }
	}
        
	@Override
	public Set<String> buildSPARQLqueries(){
		
		
            ClassElement    noun     = (ClassElement)    elements.get(1);
            PropertyElement verb     = (PropertyElement) elements.get(2);
            LiteralElement  literal  = (LiteralElement)  elements.get(3);
            PropertyElement verb2    = (PropertyElement) elements.get(4);
            LiteralElement  literal2 = (LiteralElement)  elements.get(5);

            switch (currentElement) {
                
                // case 1: TODO 
                
                // case 2: TODO 
                
                // case 3: TODO 
                
                // case 4: TODO 

                case 5: return sqb.BuildQueryForClassAnd2PropertyAnd2Literal(noun,verb,literal,verb2,literal2);
                    
                default: return new HashSet<>();
            }
	}
	
}
