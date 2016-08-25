package test.patterns.query;

import interQA.lexicon.Vocabulary;
import interQA.patterns.query.IncrementalQuery;
import interQA.patterns.query.QueryBuilder;
import junit.framework.TestCase;
import org.apache.jena.query.Query;

import java.util.*;


/**
 * @author Mariano Rico
 */
public class QueryBuilderTest extends TestCase {

    public void testSimplestCaseAsk() throws Exception {
        QueryBuilder builder = new QueryBuilder();
        builder.reset();

        String mainVar = "x";

        builder.addUninstantiatedTypeTriple(mainVar,builder.placeholder("C"));

        Set<IncrementalQuery> iqs= builder.getQueries();

        Iterator<IncrementalQuery> iqi = iqs.iterator();
        IncrementalQuery iq = iqi.next();
        Query q = iq.assemble(new Vocabulary(), //This is our vocab
                             false);           //onlyInstantiatedTriples


        q.setQueryResultStar(true); //isQueryStar
        String finalQuery = q.toString();
        assertEquals("ASK\n" +
                     "WHERE\n" +
                     "  { ?x  a                     ?C }\n",
                     finalQuery);
    }

}