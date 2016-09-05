package test.main;

import interQA.cache.JenaExecutorCacheAsk;
import junit.framework.TestCase;



/**
 * @author Mariano Rico
 */
public class JenaExecutorCacheAskTest extends TestCase {

    public void testSimpleCase() throws Exception {
        String ep = "http://dbpedia.org/sparql";
        JenaExecutorCacheAsk jeca = new JenaExecutorCacheAsk();
        String query =
                 "ASK WHERE { ?x a <http://lod.springer.com/data/ontology/class/Conference> }";
        boolean resTrial1 = jeca.executeWithCache(ep,query);
        boolean resTrial2 = jeca.executeWithCache(ep,query);
        boolean resTrial3 = jeca.executeWithCache(ep,query);
        assertEquals(resTrial1, resTrial2);
        assertEquals(resTrial2, resTrial3);
    }


}