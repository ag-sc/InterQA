package interQA.patterns;

import interQA.patterns.templates.QueryPattern;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Mariano on 13/07/2015.
 */
public class QueryPatternManager {
    
    List<QueryPattern> availableQueryPatterns = new ArrayList<>();
    List<QueryPattern> allQueryPatterns = new ArrayList<>();
    
    StringBuffer parsedText = new StringBuffer();

    
    public QueryPatternManager(){

    }

    /**
     * Intended to add patterns at the beginning of the process. This will not work if patterns are added once the
     * process has started and patterns have been rejected
     * @param patterns
     */
    public void addQueryPatterns(Set<QueryPattern> patterns){
        availableQueryPatterns.addAll(patterns);
        allQueryPatterns.addAll(patterns);
    }

    /**
     *
     * Get the current valid options for the active query patterns
     * @return a list of strings to display to the user
     */
    public List<String> getUIoptions(){
        
        List<String> allOpts = new ArrayList<>();
        for (QueryPattern pat : availableQueryPatterns){
             allOpts.addAll(pat.getNext());
        }
        return new ArrayList<>(new HashSet<>(allOpts)); //Removes duplicates
    }
    
    
    
    
    public List<String> buildSPARQLqueries() {
        
        List<String> queries = new ArrayList<>();
        
        for (QueryPattern pattern : availableQueryPatterns) {
          for (String s : pattern.buildSPARQLqueries()) {
            if (!queries.contains(s)) {
                queries.add(s);
            }
          }
        }
        return queries;
    }

    /**
     * String (complete) created by the user, including the last selection made by the user.
     * Typically this will reduce the number of query patterns available
     * @param str
     * @return the names of available query patterns
     */
    public List<String> userSentence(String str){
        
        List<QueryPattern> toRemove = new ArrayList<>();
        for(QueryPattern pat: allQueryPatterns){
           
            if (pat.parses(str) == false){ //does not parse str
                toRemove.add(pat); //Add it to the toRemove list
            }
        }
        //Removes the non active QApatterns from the list
        ArrayList<QueryPattern> temp = new ArrayList<QueryPattern>(allQueryPatterns);
        temp.removeAll(toRemove);
        availableQueryPatterns = new ArrayList<QueryPattern>(temp);

        return getQAPatternNames();
    }

    private List<String> getQAPatternNames(){
        List<String> names = new ArrayList<>();
        for (QueryPattern pat : availableQueryPatterns){
            String name = pat.getClass().getSimpleName();
            names.add(name);
        }
        return names;
    }
}