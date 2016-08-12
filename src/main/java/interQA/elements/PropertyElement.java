package interQA.elements;

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
             clone.index.put(k,index.get(k));
        }
            
        return clone;
    }

}
