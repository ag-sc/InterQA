package test.main;

import interQA.cache.JenaExecutorCacheSelect;
import junit.framework.TestCase;
import org.apache.jena.query.ResultSetRewindable;


/**
 * @author Mariano Rico
 */
public class JenaExecutorCacheSelectTest extends TestCase {

    public boolean areSimilar(ResultSetRewindable r1, ResultSetRewindable r2){
        boolean sameVars = (r1.getResultVars().size() != 0) &&
                           (r1.getResultVars().equals(r2.getResultVars())
                           );
        boolean sameSize = (r1.size() != 0) &&
                           (r1.size() == r2.size()
                           );
        return (sameVars && sameSize);
    }
    public void testSimpleCase() throws Exception {
        String ep = "http://dbpedia.org/sparql";
        JenaExecutorCacheSelect jecs = new JenaExecutorCacheSelect();
        String query =
                 "SELECT DISTINCT * WHERE { "+
                 "?x a <http://dbpedia.org/ontology/Skier>"+
                 " ; <http://dbpedia.org/ontology/team> ?I"+
                 " OPTIONAL { ?I <http://www.w3.org/2000/01/rdf-schema#label> ?l } "+
                 "}";
        ResultSetRewindable resFromEP =     (ResultSetRewindable)jecs.executeWithCache(ep,query);
        ResultSetRewindable res1FromCache = (ResultSetRewindable)jecs.executeWithCache(ep,query);
        ResultSetRewindable res2FromCache = (ResultSetRewindable)jecs.executeWithCache(ep,query);
        assertTrue(areSimilar(resFromEP, res1FromCache));
        assertTrue(areSimilar(res1FromCache, res2FromCache));
    }


}