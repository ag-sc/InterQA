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
    
    Set<QueryPattern> allQueryPatterns = new HashSet<>();
    Set<QueryPattern> activeQueryPatterns = new HashSet<>();
    
//  StringBuffer parsedText = new StringBuffer();

    
    public QueryPatternManager(){

    }
        
    /**
     * Intended to add patterns at the beginning of the process. This will not work if patterns are added once the
     * process has started and patterns have been rejected
     * @param patterns
     */
    public void addQueryPatterns(Set<QueryPattern> patterns) {
        for (QueryPattern qp : patterns){
             QueryPattern clone = qp.clone();
             allQueryPatterns.add(clone); 
        }
    }
    
    public Set<QueryPattern> getPatterns() {
        
        return allQueryPatterns;
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
    public List<String> getActivePatternsBasedOnUserInput(String str) {

        activeQueryPatterns = new HashSet<>();
        
        for (QueryPattern pattern : allQueryPatterns) {
             QueryPattern clone = pattern.clone();
             if (clone.parse(str)) {
                 activeQueryPatterns.add(clone);
             }
        }
        
        return getQAPatternNames();
    }

    public String[] getPredictedASKqueries(){
        HashSet<String> res = new HashSet<>();
        for (QueryPattern pat : allQueryPatterns){
            res.addAll(pat.predictASKqueries());
        }

        return(res.toArray(new String[res.size()]));
    }

    public String[] getPredictedSELECTqueries(){
        HashSet<String> res = new HashSet<>();
        for (QueryPattern pat : allQueryPatterns){
            res.addAll(pat.predictSELECTqueries());
        }

        return(res.toArray(new String[res.size()]));
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
