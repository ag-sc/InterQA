package interQA.patterns;

import interQA.patterns.templates.QueryPattern;
import interQA.lexicon.Vocabulary;
import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author cunger
 */
public interface QueryPatternFactory {
    
    public Vocabulary vocab = new Vocabulary();
    
    public Set<QueryPattern> rollout();
    public Set<QueryPattern> rollout(ArrayList<String> patternNames);
}
