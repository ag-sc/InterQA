package interQA.patterns;


import interQA.elements.*;
import interQA.lexicon.*;
import interQA.lexicon.LexicalEntry.Feature;
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

                StringElement element5 = new StringElement();
                element5.add("?");
                elements.add(element5);
            }
	        
            @Override
            public void updateAt(int i,String s) {
			
                if (i == 1) {
                
                    ((StringElement) elements.get(1)).transferFeatures(elements.get(3),s);
                }
                
                if (i==4){
					
                    elements.get(6).addToIndex(instances.filterByPropertyForInstances(elements.get(4).getActiveEntries(),LexicalEntry.SynArg.OBJECT));       
		}
            }

            @Override
            public Set<String> buildSPARQLqueries(){
								
		PropertyElement nounpos1 = (PropertyElement) elements.get(4);
		ClassElement nounclass = (ClassElement) elements.get(6);
						
                return sqb.BuildQueryForClassAndProperty(nounclass, nounpos1, LexicalEntry.SynArg.OBJECT);
				
            }
			
}
