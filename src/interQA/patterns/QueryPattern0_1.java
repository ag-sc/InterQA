package interQA.patterns;

import interQA.elements.ClassElement;
import interQA.elements.IndividualElement;
import interQA.elements.PropertyElement;
import interQA.elements.StringElement;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cunger
 */
public class QueryPattern0_1 extends QueryPattern {
    
    // SELECT ?x WHERE { ?x rdf:type <Class> . ?x <Property> <Individual> . } 
    // SELECT ?x WHERE { ?x rdf:type <Class> . ?y <Property> <Individual> . }
    
    // Which <Noun:Class> <TransitiveVerb:Property> <Name:Individual>?
    
    // Which band performed Dancing Queen?
    // Which movies star Brad Pitt?
            
    
	public QueryPattern0_1(Lexicon lexicon, InstanceSource instances) {
            
            this.lexicon = lexicon;
            this.instances = instances; 
            
            init();
        }
        
        @Override
        public void init() {
            
            elements = new ArrayList<>();
                       
            StringElement element0 = new StringElement(); 
            element0.add("which");
            elements.add(element0);
		
            ClassElement element1 = new ClassElement(lexicon,LexicalEntry.POS.NOUN,null); 
            elements.add(element1);
            
            

            PropertyElement element2 = new PropertyElement(lexicon,LexicalEntry.POS.VERB,vocab.TransitiveFrame);
	        elements.add(element2);

            
            IndividualElement element3 = new IndividualElement(); 
            elements.add(element3);
	}
        
        @Override
        public void updateAt(int i) {
            
            if (i == 1) {
            // If parse is at element1, filter possible entries of element2.
                
                Map<String,List<LexicalEntry>> old_element2index = elements.get(2).getIndex();
                Map<String,List<LexicalEntry>> new_element2index = new HashMap<>();
                     
                for (LexicalEntry entry1 : elements.get(1).getActiveEntries()) {
                                        
                    new_element2index.putAll(instances.filterByClassForProperty(old_element2index,LexicalEntry.SynArg.SUBJECT,entry1.getReference()));   
                }
                elements.get(2).setIndex(new_element2index);
            } 
            
            if (i == 2) {
            	
            	elements.get(3).addToIndex(instances.filterByPropertyForInstances(elements.get(2).getActiveEntries(), LexicalEntry.SynArg.DIRECTOBJECT ));
            	  
            }
            
        }

        @Override
	public List<String> buildSPARQLqueries() {
            
            List<String> queries = new ArrayList<>();
            
            ClassElement    noun = (ClassElement)    elements.get(1);
            PropertyElement    verb = (PropertyElement)    elements.get(2);
            IndividualElement indi = (IndividualElement) elements.get(3);
                 
            for (LexicalEntry noun_entry : noun.getActiveEntries()) {
            for (LexicalEntry verb_entry : verb.getActiveEntries()) {
                
                 switch (verb_entry.getSemArg(LexicalEntry.SynArg.DIRECTOBJECT)) {
                     
                    case SUBJOFPROP: 
                         for (LexicalEntry inst_entry : indi.getActiveEntries()) {
                              queries.add("SELECT DISTINCT ?x WHERE { "
                                        + " ?x <" + vocab.rdfType + "> <" + noun_entry.getReference() + "> . "
                                        + " <" + inst_entry.getReference() + "> <" + verb_entry.getReference() + "> ?x . }");
                         }
                         break;
                    case OBJOFPROP: 
                         for (LexicalEntry inst_entry : indi.getActiveEntries()) {
                              queries.add("SELECT DISTINCT ?x WHERE {"
                                        + " ?x <" + vocab.rdfType + "> <" + noun_entry.getReference() + "> . "
                                        + " ?x <" + verb_entry.getReference() + "> <" + inst_entry.getReference() + "> . }");
                         }
                         break;
                 }
            }}
		
            return queries;
	}
}
