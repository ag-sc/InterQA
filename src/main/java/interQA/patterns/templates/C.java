package interQA.patterns.templates;

import interQA.elements.ClassElement;
import interQA.elements.StringElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.Lexicon;


public class C extends QueryPattern {
 
    
    // SELECT DISTINCT ?x WHERE 
    // {
    //   ?x rdf:type <C> .
    // }
    
    
    public C(Lexicon lexicon,DatasetConnector dataset) {
		
            this.lexicon = lexicon;
            this.dataset = dataset;
            
            init();	
	}
	
	@Override
	public void init(){
            
            StringElement element0 = new StringElement ();
            elements.add(element0);
		
            ClassElement element1 = new ClassElement();
            elements.add(element1);
            
            StringElement element2 = new StringElement ();
            elements.add(element2);
	}

        @Override
        public void update(String s) {
             
            ClassElement c = (ClassElement) elements.get(1);

            switch (currentElement) {
                
                case 0: {
                    
                    // Create query template 
                    
                    builder.reset();
                    
                    String mainVar = "x";
                                    
                    builder.addUninstantiatedTypeTriple(mainVar,"C");
                    
                    checkHowMany(s);
                    if (count) builder.addCountVar(mainVar); 
                    else       builder.addProjVar(mainVar);

                    break;
                }
                
                case 1: {
                                        
                    builder.instantiate("C",c.getActiveEntries());
                    break;
                } 
            }
        }

}
