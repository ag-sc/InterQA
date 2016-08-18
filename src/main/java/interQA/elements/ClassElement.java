package interQA.elements;

import interQA.lexicon.LexicalEntry;
import java.util.ArrayList;
import java.util.HashMap;


public class ClassElement extends Element {

	
	public ClassElement() {
            
            this.index = new HashMap<>();
            
            this.agrFeatures = new ArrayList<>();
            this.context     = new HashMap<>();
	}
        
        @Override
        public ClassElement clone() {
            
            ClassElement clone = new ClassElement();
            
            for (String k : index.keySet()) {
                 clone.index.put(k,new ArrayList<>());
                 for (LexicalEntry e : index.get(k)) {
                      clone.index.get(k).add(e);
                 }
            }
            
            return clone;
        }

}
