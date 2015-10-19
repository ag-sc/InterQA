package interQA.elements;

import java.util.HashMap;


public class IndividualElement extends ParsableElement {
 
    
    public IndividualElement() {

        // Starts with an empty index,
        // which is filled with possible individuals during parsing.
        index = new HashMap<>();
    }

}
