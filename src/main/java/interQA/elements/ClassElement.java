package interQA.elements;

import java.util.ArrayList;
import java.util.HashMap;


public class ClassElement extends Element {

	
	public ClassElement() {
            
            this.index = new HashMap<>();
            
            this.agrFeatures = new ArrayList<>();
            this.context     = new HashMap<>();
	}

}
