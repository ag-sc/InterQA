package test.patterns.templates.ExperimentSept2016;


import interQA.Config;
import interQA.Config.Language;
import interQA.Config.Usecase;
import interQA.patterns.QueryPatternManager;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static interQA.Config.ExtractionMode.ExhaustiveExtraction;


/**
 * @author Mariano Rico
 */
public class P_P_ITest_Experiment_EN extends TestCase {

    QueryPatternManager qm = null;

    //Executed before EACH test
    public void setUp() throws Exception {
        Config config = new Config();
        config.init(Usecase.EXPERIMENT,
                    Language.EN,
                    new ArrayList<String>(Arrays.asList("qpP_P_I")) //What is the height and weight of Michael Jordan?
                   );                                               // SELECT DISTINCT ?x ?y WHERE
                                                                    // {
                                                                    //   <I> <P1> ?x .
                                                                    //   <I> <P2> ?y .
                                                                    // }
        //By default uses NaiveExtraction and does not use historical cache
        config.setCacheMode(ExhaustiveExtraction, true);

        qm = config.getPatternManager();

    }
    public void testWhatIsThe() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                    "what is the",
                                                                    "capital",
                                                                    "and",
                                                                    "currency",
                                                                    "of",
                                                                    "Algeria"));


        List<String> res = qm.buildSPARQLqueries();
        List<String> opts = qm.getUIoptions();

        assertEquals(new HashSet<>(res),
                     new HashSet<>(
                            Arrays.asList(
                                    "SELECT DISTINCT ?x ?y WHERE {"+
                                    " <http://dbpedia.org/resource/Algeria> <http://dbpedia.org/ontology/capital> ?x"+
                                    " ; <http://dbpedia.org/ontology/currency> ?y "+
                                    "}"
                            )
                     ));
    }

}