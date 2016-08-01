package interQA.patterns.templates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interQA.elements.ClassElement;
import interQA.elements.InstanceElement;
import interQA.elements.PropertyElement;
import interQA.elements.StringElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import java.util.Set;


public class C_P_I extends QueryPattern{

    
	/*
        SELECT DISTINCT ?uri { ?uri rdf:type <Class> . ?uri <Property> <Instance> . ?uri <Property> <Instance> . }
        */
        
	public C_P_I(Lexicon lexicon,DatasetConnector dataset){
		
            this.lexicon = lexicon;
            this.dataset = dataset;
            sqb.setEndpoint(dataset.getEndpoint());
            init();
	}
        

	@Override
	public void init(){
            
            StringElement element0 = new StringElement();
            elements.add(element0);
		
            ClassElement element1 =  new ClassElement();
            elements.add(element1);
		
            StringElement element2 = new StringElement();
            elements.add(element2);
		
            PropertyElement element3 = new PropertyElement();
            elements.add(element3);
            
            StringElement element4 = new StringElement();
            elements.add(element4);
		
            InstanceElement element5 = new InstanceElement();
            elements.add(element5);    
            
            StringElement element6 = new StringElement();
            elements.add(element6);
	}
	
	@Override
	public void update(String s){
		
            switch (currentElement) {
                
                case 0: { 
                
                    checkHowMany(s);
                    ((StringElement) elements.get(0)).transferFeatures(elements.get(1),s);
                    break;
                } 
                
                case 1: {
                
                    setFeatures(1,2,s);
                    
                    Map<String,List<LexicalEntry>> old_element3index = elements.get(3).getIndex();
                    Map<String,List<LexicalEntry>> new_element3index = new HashMap<>();
    		
                    for (LexicalEntry entry : elements.get(1).getActiveEntries()) {
    			new_element3index.putAll(dataset.filterByClassForProperty(old_element3index, LexicalEntry.SynArg.SUBJECT, entry.getReference()));
                    }
                    elements.get(3).addToIndex(new_element3index);
                    break;
                }
                
                case 2: { 
                    
                    ((StringElement) elements.get(2)).transferFeatures(elements.get(3),s);
                    break;
                }  
                    
                case 3: {
                    
                    setFeatures(3,4,s);
                    
                    elements.get(5).addToIndex(dataset.filterByPropertyForInstances(elements.get(3).getActiveEntries(), LexicalEntry.SynArg.OBJECT));
                    
                    for (String m : elements.get(3).getMarkers()) {
                        ((StringElement) elements.get(4)).add(m);
                    }
                    break;
                }
                    
                case 4: {
                    
                    ((StringElement) elements.get(4)).transferFeatures(elements.get(5),s);
                    break;
                }
                
                case 5: { 
                    
                    setFeatures(5,6,s);
                    break;
                } 
            }
	}
	
	
	@Override
	public Set<String> buildSPARQLqueries() {
			
            // SELECT DISTINCT ?x WHERE 
            // {
            //   ?x rdf:type <C> .
            //   ?x <P> <I> .
            // }
            		
            String mainVar = "x";
            
            ClassElement    c  = (ClassElement)   elements.get(1);
            PropertyElement p = (PropertyElement) elements.get(3);
            InstanceElement i = (InstanceElement) elements.get(5);
		            
            switch (currentElement) {
                   
                case 0: {
                    
                    builder.reset();
                    
                    if (count) builder.addCountVar(mainVar); 
                    else       builder.addProjVar(mainVar);
                    break;
                }
                
                case 1: { // + ?x rdf:type <C> .
                    
                    builder.addTypeTriple(mainVar,c.getActiveEntries());
                    break;
                }
                    
                case 3: { // + ?x <P> ?I .
                    
                    builder.addTriple(mainVar,p.getActiveEntries(),"I");
                    break;
                }
                    
                case 5: { // ?I -> <I>
                    
                    builder.instantiate("I",i.getActiveEntries());
                    break;
                }
            }
            
            return builder.returnQueries();
	}
}
