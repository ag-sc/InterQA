package test.patterns.templates;


import interQA.Config;
import interQA.Config.Language;
import interQA.Config.USECASE;
import interQA.patterns.QueryPatternManager;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


/**
 * @author Mariano Rico
 */
public class C_P_I_P_ITest_Experiment_EN extends TestCase {

    QueryPatternManager qm = null;

    //Executed before EACH test
    public void setUp() throws Exception {
        Config config = new Config();
        config.init(USECASE.EXPERIMENT,
                    Language.EN,
                    new ArrayList<String>(Arrays.asList("qpC_P_I_P_I1",  //Show me all conferences that took place in Berlin in 2015.
                                                        "qpC_P_I_P_I2")  //Which conferences took place in Berlin in 2015?
                                                        )               // NO CONTINUATION!!!!
                   );
        qm = config.getPatternManager();

    }
    public void testWhatActorsPlayinBatman() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                    "show me all",
                                                                    "movies", //cities that star/appeared... no continuation!
                                                                    "that",
                                                                    "starred",
                                                                    "in"));
                                                                   //No continuations!!!
                   // show me all actors that starred.... produces this extractive query
                   // SELECT DISTINCT * WHERE { ?x ?P2 ?I2 ; a <http://dbpedia.org/ontology/Actor> . ?I1 <http://dbpedia.org/ontology/starring> ?x OPTIONAL { ?I1 <http://www.w3.org/2000/01/rdf-schema#label> ?l } }
                   // It has 3.453.202 results!!!
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                     new HashSet<>(
                            Arrays.asList(
                                    "" //??????????????????????????
                            )
                     ));
    }
    public void testGiveMeAllActorsThatPlayInBatman() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("", //Separator
                                                                    "which",
                                                                    "actors",
                                                                    "appear")); //Extractive query with 3.453.202
            //SELECT DISTINCT * WHERE { ?x ?P2 ?I2 ; a <http://dbpedia.org/ontology/Actor> . ?I1 <http://dbpedia.org/ontology/starring> ?x OPTIONAL { ?I1 <http://www.w3.org/2000/01/rdf-schema#label> ?l } }
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                     new HashSet<>(
                            Arrays.asList(
                                    "" //?????????????????????
                            )
                     ));
    }
}