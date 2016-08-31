package test;

import interQA.Config;
import interQA.patterns.templates.QueryPattern;
import java.util.Arrays;
import java.util.HashSet;
import junit.framework.TestCase;

/**
 *
 * @author cunger
 */
public class QueryPredictionTest extends TestCase {
    
    
    Config config = new Config();
    
    public void test_C() throws Exception {
        
        config.init(Config.Usecase.SPRINGER,Config.Language.EN);
        
        for (QueryPattern pattern : config.getPatternManager().getPatterns()) {
             if (pattern.getClass().getCanonicalName().equals("C")) {
                 assertEquals(pattern.predictASKqueries(),
                    new HashSet<>(
                    Arrays.asList(
                        "ASK WHERE { ?x a <http://lod.springer.com/data/ontology/class/Conference> }"
                    ))
                 );
                 assertEquals(pattern.predictSELECTqueries(),
                    new HashSet<>());
             }
        }
    }
    
}
