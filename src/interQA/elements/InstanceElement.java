package interQA.elements;

import java.util.ArrayList;
import java.util.List;


public class InstanceElement extends ParsableElement {


    public InstanceElement() {
    }

    @Override
    public List<String> getOptions() {

        List<String> options = new ArrayList<>();
        
        options.addAll(index.keySet());
        
        return options; 
    }

    public List<String> getInstances() {
		
        return null;
    }

}
