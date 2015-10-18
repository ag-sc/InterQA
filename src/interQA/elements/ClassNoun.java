package interQA.elements; /**
 * Created by Mariano on 13/07/2015.
 */

import interQA.lexicon.Lexicon;
import java.util.ArrayList;
import java.util.List;


public class ClassNoun extends ParsableElement {


    public ClassNoun(Lexicon lexicon) {
        
        index = lexicon.getNounIndex();
    }
    
    
    @Override
    public List<String> getOptions() {
        
        List<String> options = new ArrayList<>();
        
        options.addAll(index.keySet());
        // TODO order?
        
        return options;
    }

}