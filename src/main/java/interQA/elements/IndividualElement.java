package interQA.elements;

import java.util.ArrayList;
import java.util.HashMap;


public class IndividualElement extends Element {
 
    
    public IndividualElement() {

        // Starts with an empty index,
        // which is filled with possible instances during parsing.
        this.index = new HashMap<>();
        this.features = new ArrayList<>();
    }

}
