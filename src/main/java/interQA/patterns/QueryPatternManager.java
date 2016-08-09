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
    
    Set<QueryPattern> activeQueryPatterns = new HashSet<>();
    
    StringBuffer parsedText = new StringBuffer();

    
    public QueryPatternManager(){

    }

    /**
     * Intended to add patterns at the beginning of the process. This will not work if patterns are added once the
     * process has started and patterns have been rejected
     * @param patterns
     */
    public void addQueryPatterns(Set<QueryPattern> patterns){
        activeQueryPatterns.addAll(patterns);
    }

    /**
     *
     * Get the current valid options for the active query patterns
     * @return a list of strings to display to the user
     */
    public List<String> getUIoptions(){
        
        List<String> allOpts = new ArrayList<>();
        for (QueryPattern pattern : activeQueryPatterns) {
             allOpts.addAll(pattern.getNext());
        }
        return new ArrayList<>(new HashSet<>(allOpts)); // removes duplicates
    }
    
    
    public List<String> buildSPARQLqueries() {
        
        List<String> queries = new ArrayList<>();
        
        for (QueryPattern pattern : activeQueryPatterns) {
          for (String s : pattern.buildSPARQLqueries(true)) {
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
    public List<String> getRemainingActivePatterns(String str) {

        // Remove the non-active query patterns from the list

        List<QueryPattern> toRemove = new ArrayList<>();
        for (QueryPattern pattern : activeQueryPatterns) {
            if (!pattern.parse(str)) { 
                 toRemove.add(pattern); 
            }
        }
        activeQueryPatterns.removeAll(toRemove);

        // Return the names of the remaining ones
        
        return getQAPatternNames();
    }

    private List<String> getQAPatternNames() {
        
        List<String> names = new ArrayList<>();
        
        for (QueryPattern pattern : activeQueryPatterns){
            String name = pattern.getClass().getSimpleName();
            names.add(name);
        }
        
        return names;
    }
}
