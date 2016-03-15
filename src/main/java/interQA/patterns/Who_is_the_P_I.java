package interQA.patterns;


import interQA.elements.ClassElement;
import interQA.elements.StringElement;
import interQA.elements.PropertyElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.*;
import interQA.lexicon.LexicalEntry.Feature;
import interQA.lexicon.Lexicon;
import java.util.*;
/**
*
* @author mince
*/
public class Who_is_the_P_I extends QueryPattern{

    
            /*
            SELECT ?y WHERE { ?x rdf:type <Class>. ?x <Property> ?y. }

            What is|are the <NounPP:Property> of <Noun:Class>?

              What are the side effects of Alzheimer drugs?
            */ 


            InstanceSource instances;

            public Who_is_the_P_I(Lexicon lexicon,InstanceSource instances){

                this.lexicon = lexicon;
                this.instances = instances; 

                init();
            }

            @Override
            public void init() {

                elements = new ArrayList<>();

                StringElement element0 = new StringElement();
                element0.add("what");
                element0.add("who");
                elements.add(element0);

                StringElement element1 = new StringElement();
                element1.add("is",Feature.SINGULAR);
                element1.add("are",Feature.SINGULAR);
                element1.add("was",Feature.PLURAL);
                element1.add("were",Feature.PLURAL);
                elements.add(element1);

                StringElement element2 = new StringElement();
                element2.add("the");
                elements.add(element2);

                PropertyElement element3 = new PropertyElement();
                element3.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.NounPPFrame);
                element3.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.NounPossessiveFrame);
                elements.add(element3);

                ClassElement element4 = new ClassElement();
                element4.addEntries(lexicon, LexicalEntry.POS.NOUN, null);
                elements.add(element4);
            }
	        
            @Override
            public void update(String s) {
		
                switch (currentElement) {
                
                    case 1: ((StringElement) elements.get(1)).transferFeatures(elements.get(3),s); break;
                
                    case 4: {
					
                        elements.get(6).addToIndex(instances.filterByPropertyForInstances(elements.get(4).getActiveEntries(),LexicalEntry.SynArg.OBJECT)); 
                        break;
                    }
		}
            }

            @Override
            public Set<String> buildSPARQLqueries(){
								
		PropertyElement nounpos1 = (PropertyElement) elements.get(4);
		ClassElement nounclass = (ClassElement) elements.get(6);
			
                switch (currentElement) {
                
                case 3: return sqb.BuildQueryForProperty(nounpos1);
                    
                    case 4: return sqb.BuildQueryForClassAndProperty(nounclass, nounpos1, LexicalEntry.SynArg.OBJECT);
                        
                    default: return new HashSet<>();
                }
            }
			
}
