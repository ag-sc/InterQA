package interQA.elements;

import interQA.lexicon.LexicalEntry;
import java.util.ArrayList;
import java.util.HashMap;


public class PropertyElement extends Element {

	
    public PropertyElement() {
            
        this.index = new HashMap<>();
        
        this.agrFeatures = new ArrayList<>();
        this.context     = new HashMap<>();
    }
    
    @Override
    public PropertyElement clone() {
            
        PropertyElement clone = new PropertyElement();
            
        for (String k : index.keySet()) {
            clone.index.put(k,new ArrayList<>());
            for (LexicalEntry e : index.get(k)) {
                clone.index.get(k).add(e);
            }
        }
            
        return clone;
    }

}
