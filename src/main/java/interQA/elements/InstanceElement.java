package interQA.elements;

import interQA.lexicon.LexicalEntry;
import java.util.ArrayList;
import java.util.HashMap;


public class InstanceElement extends Element {
     
    
    public InstanceElement() {

        // Starts with an empty index,
        // which is filled with possible instances during parsing.
        this.index = new HashMap<>();
        
        this.agrFeatures = new ArrayList<>();
        this.context     = new HashMap<>();
    }
    
    @Override
    public InstanceElement clone() {
            
        InstanceElement clone = new InstanceElement();
            
        for (String k : index.keySet()) {
            clone.index.put(k,new ArrayList<>());
            for (LexicalEntry e : index.get(k)) {
                 clone.index.get(k).add(e);
            }
        }
            
        return clone;
    }

}
