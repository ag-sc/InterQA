package interQA;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mariano on 13/07/2015.
 */
public class QueryPatternManager {
    List<QAPattern> availableQueryPatterns = new ArrayList<QAPattern>();
    StringBuffer parsedText = new StringBuffer();

    public QueryPatternManager(){

    }
    public void addQueryPattern(QAPattern pattern){
        availableQueryPatterns.add(pattern);
    }

    /**
     *
     * Get the current valid options for the active query patterns
     * @return a list of strings to display to the user
     */
    public List<String> getUIoptions(){
        List<String> allOpts = new ArrayList<String>();
        for (QAPattern pat : availableQueryPatterns){
            List<String> opts = pat.getNext();
            if (opts != null) {
                allOpts.addAll(opts);
            }
        }
        return allOpts;
    }

    /**
     * String selected by the user. This will reduce the number os query patterns available
     * @param str
     * @return the number of available query patterns
     */
    public List<String> userSelects(String str){
        List<QAPattern> toRemove = new ArrayList<QAPattern>();
        for(QAPattern pat: availableQueryPatterns){
            if (pat.parses(str) == false){ //does not parse str
                toRemove.add(pat);  //Add it to the toRemove list
            }
        }
        //Removes the non active QApatterns from the list
        availableQueryPatterns.removeAll(toRemove);

        return getQAPatternNames();
    }

    private List<String> getQAPatternNames(){
        List<String> names = new ArrayList<String>();
        for (QAPattern pat : availableQueryPatterns){
            String name = pat.getClass().getSimpleName();
            names.add(name);
        }
        return names;
    }
}
