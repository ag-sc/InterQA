package interQA.elements;

import java.util.ArrayList;
import java.util.HashMap;


public class InstanceElement extends Element {
 
    
    public InstanceElement() {

        // Starts with an empty index,
        // which is filled with possible instances during parsing.
        this.index = new HashMap<>();
        this.agrFeatures = new ArrayList<>();
    }

}
