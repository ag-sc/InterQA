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
public class CTest_Experiment_EN extends TestCase {

    QueryPatternManager qm = null;

    //Executed before EACH test
    public void setUp() throws Exception {

        Config config = new Config();
        config.init(Usecase.EXPERIMENT,
                    Language.EN,
                    new ArrayList<String>(Arrays.asList("qpC1",  // Give me all languages.
                                                        "qpC2")  // Which movies are there?
                                                        )
                   );
        //By default uses NaiveExtraction and does not use historical cache
        config.setCacheMode(ExhaustiveExtraction, true);


        qm = config.getPatternManager();

    }
    public void testGiveMeAllMovies() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                    "give me all",
                                                                    "movies"));
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                     new HashSet<>(
                            Arrays.asList(
                                    "SELECT DISTINCT ?x WHERE {"+
                                    " ?x a <http://dbpedia.org/ontology/Film> "+
                                    "}"
                            )
                     ));
    }
    public void testHowManyMovies() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("", //Separator
                                                                    "how many",
                                                                    "movies",
                                                                    "are there"));
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                     new HashSet<>(
                            Arrays.asList(
                                    "SELECT DISTINCT (COUNT(?x) AS ?x_count) WHERE {"+
                                    " ?x a <http://dbpedia.org/ontology/Film> "+
                                    "}"
                            )
                     ));
    }
}