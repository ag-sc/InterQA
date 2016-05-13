package interQA.patterns.springer;



import interQA.patterns.templates.QueryPattern;
import interQA.elements.StringElement;
import interQA.elements.PropertyElement;
import interQA.elements.InstanceElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interQA.lexicon.LexicalEntry.Feature;
import java.util.HashSet;
import java.util.Set;


public class SpringerQueryPattern5 extends QueryPattern{
	
	//Give me the <Property:Noun> and <Property:Noun> <Literal> <Literal>
	
	//Give me the start and end date of ISWC 2015.
	
	public SpringerQueryPattern5(Lexicon lexicon, DatasetConnector instances ){
		
		this.lexicon = lexicon;
		this.dataset = instances;
		
		
		init();
	}
	
	@Override
	public void init(){
		
		StringElement element0 = new StringElement();
		elements.add(element0);
		
		PropertyElement element1 = new PropertyElement();
		elements.add(element1);
		
		StringElement element2 = new StringElement();
		elements.add(element2);
		
		PropertyElement element3 = new PropertyElement();
                elements.add(element3);
		
		InstanceElement element4 = new InstanceElement();
		elements.add(element4);
		
		InstanceElement element5 = new InstanceElement();
		elements.add(element5);
		
	}
	
	@Override
	public void update(String s){
            
            
            switch(currentElement){
                
                case 0: ((StringElement) elements.get(0)).transferFeatures(elements.get(1),s);break;
                
                case 2 :{
                    Map<String,List<LexicalEntry>> old_element2index = elements.get(3).getIndex();
			Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
			
			for(LexicalEntry entry1 : elements.get(1).getActiveEntries()){
				new_element2index.putAll(dataset.filterByPropertyForProperty(old_element2index,LexicalEntry.SynArg.OBJECT, entry1.getReference()));
				
			}
			elements.get(3).setIndex(new_element2index);
                }break;
                 
                case 3:{
                    elements.get(4).addToIndex(dataset.filterByPropertyForInstances(elements.get(3).getActiveEntries(),LexicalEntry.SynArg.OBJECT));
                }break;
                
                case 4:{
                    elements.get(5).addToIndex(dataset.filterByInstanceForInstance(elements.get(4).getActiveEntries()));

                }break;
            }
            
		
	}
	
	
	
	@Override
	public Set<String> buildSPARQLqueries(){
		
		PropertyElement p1 = (PropertyElement) elements.get(1);
		PropertyElement p2 = (PropertyElement) elements.get(3);
		InstanceElement ins1 = (InstanceElement) elements.get(4);
		InstanceElement ins2 = (InstanceElement) elements.get(5);
		
                
                switch(currentElement){
                    
                    case 5 :return sqb.BuildQueryFor2PropertyAndNameLiteralAndGYearLiteral(p1,LexicalEntry.SynArg.SUBJECT,
            				p2,LexicalEntry.SynArg.SUBJECT, ins1, ins2); 
                
                
                }
                return new HashSet<>();
	}

}