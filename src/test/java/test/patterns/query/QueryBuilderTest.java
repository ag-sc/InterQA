package test.patterns.query;

import interQA.patterns.query.IncrementalQuery;
import interQA.patterns.query.QueryBuilder;
import junit.framework.TestCase;

import java.util.*;


/**
 * @author Mariano Rico
 */
public class QueryBuilderTest extends TestCase {

    public void testSimplestCase() throws Exception {
        QueryBuilder builder = new QueryBuilder();
        builder.reset();

        String mainVar = "x";

        builder.addUninstantiatedTypeTriple(mainVar,builder.placeholder("C"));

        Set<IncrementalQuery> iqs= builder.getQueries();

        IncrementalQuery[] iqa = (IncrementalQuery[]) iqs.toArray();
        assertEquals(new HashSet<>(iqs),
                     new HashSet<String>(
                            Arrays.asList(
                                    "?x @rdf:type ?C"
                            )
                     ));
    }

    public void SecondCase() throws Exception {
        QueryBuilder builder = new QueryBuilder();
        builder.reset();

        String mainVar = "x";

        builder.addUninstantiatedTypeTriple(mainVar,builder.placeholder("C"));
        builder.addUninstantiatedTriple(mainVar,builder.placeholder("P"),builder.placeholder("I"));

        Set<IncrementalQuery> iqs= builder.getQueries();

        //Set<String> iqsAsStrings = new ;
        assertEquals(new HashSet<>(iqs),
                new HashSet<String>(
                        Arrays.asList(
                                "?x @rdf:type ?C" +
                                "?x ?P ?I"
                        )
                ));
    }
}