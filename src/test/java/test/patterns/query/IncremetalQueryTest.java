package test.patterns.query;

import interQA.lexicon.Vocabulary;
import interQA.patterns.query.IncrementalQuery;
import interQA.patterns.query.QueryBuilder;
import junit.framework.TestCase;
import org.apache.jena.query.Query;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * @author Mariano Rico
 */
public class IncremetalQueryTest extends TestCase {

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
        //Without project vars it is an ask query
        q.setQueryResultStar(true); //isQueryStar
        String finalQuery = q.toString();
        assertEquals("ASK\nWHERE\n  { ?x  a                     ?C }\n",
                     finalQuery);
    }

    public void testSimplestCaseSelect() throws Exception {
        QueryBuilder builder = new QueryBuilder();
        builder.reset();

        String mainVar = "x";

        builder.addUninstantiatedTypeTriple(mainVar, builder. placeholder("C"));
        builder.addProjVar(mainVar); //With project vars we create select queries

        Set<IncrementalQuery> iqs= builder.getQueries();
        Iterator<IncrementalQuery> iqi = iqs.iterator();
        IncrementalQuery iq = iqi.next();
        Query q = iq.assemble(new Vocabulary(), //This is our vocab
                             false);           //onlyInstantiatedTriples


        q.setQueryResultStar(true); //isQueryStar
        String finalQuery = q.toString();

        assertEquals("SELECT DISTINCT  *\nWHERE\n  { ?x  a                     ?C }\n",
                     finalQuery);
    }

    public void testSecondCaseSelect() throws Exception {
        QueryBuilder builder = new QueryBuilder();
        builder.reset();

        String mainVar = "x";

        builder.addProjVar(mainVar); //With project vars we create select queries
        builder.addUninstantiatedTypeTriple(mainVar,builder.placeholder("C"));
        builder.addUninstantiatedTriple(mainVar,builder.placeholder("P"),builder.placeholder("I"));

        Set<IncrementalQuery> iqs= builder.getQueries();
        Iterator<IncrementalQuery> iqi = iqs.iterator();
        IncrementalQuery iq = iqi.next();
        Query q = iq.assemble(new Vocabulary(), //This is our vocab
                              false);           //onlyInstantiatedTriples

        q.setQueryResultStar(true); //isQueryStar
        String finalQuery = q.toString();

        assertEquals("SELECT DISTINCT  *\n" +
                     "WHERE\n" +
                     "  { ?x  a                     ?C ;\n" +
                     "        ?P                    ?I\n" +
                     "  }\n",
                finalQuery);
    }
}