package test.patterns.templates;


import interQA.Config;
import interQA.Config.Language;
import interQA.Config.Usecase;
import interQA.patterns.QueryPatternManager;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static interQA.Config.ExtractionMode.NaiveExtraction;
import static interQA.Config.ExtractionMode.ExhaustiveExtraction;


/**
 * @author Mariano Rico
 */
public class C_P_I_P_ITest_Experiment_EN extends TestCase {

    Config config = null;
    QueryPatternManager qm = null;

    //Executed before EACH test
    public void setUp() throws Exception {
        config = new Config();
        config.init(Usecase.EXPERIMENT,
                    Language.EN,
                    new ArrayList<String>(Arrays.asList("qpC_P_I_P_I1",  //Show me all conferences that took place in Berlin in 2015.
                                                        "qpC_P_I_P_I2")  //Which conferences took place in Berlin in 2015?
                                                        )
                   );
        //By default uses NaiveExtraction and does not use historical cache
        //config.setCacheMode(ExhaustiveExtraction, true); //This test runs OK for both modes, but in exhaustive+Historical mode...
                                                         //each method requires 8GB and 16GB Heap space respectively.

        qm = config.getPatternManager();

    }
    public void testShowMeAllMoviesThatStarredIn() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                    "show me all",
                                                                    "movies",
                                                                    "that",
                                                                    "starred",
                                                                    "in"));
        assertEquals(avlPats.size(), 1);

        // show me all movies that starred.... produces this extractive query
        // SELECT DISTINCT * WHERE { ?x ?P2 ?I2 ; a <http://dbpedia.org/ontology/Film> . ?I1 <http://dbpedia.org/ontology/starring> ?x OPTIONAL { ?I1 <http://www.w3.org/2000/01/rdf-schema#label> ?l } }
        // It has around 80.000 results

        // show me all actors that starred.... produces this extractive query
       // SELECT DISTINCT * WHERE { ?x ?P2 ?I2 ; a <http://dbpedia.org/ontology/Actor> . ?I1 <http://dbpedia.org/ontology/starring> ?x OPTIONAL { ?I1 <http://www.w3.org/2000/01/rdf-schema#label> ?l } }
       // It has 3.453.202 results!!!
        List<String> res = qm.buildSPARQLqueries();
        List<String> opts = qm.getUIoptions();
        if (config.getExtractionMode() == NaiveExtraction){
            assertEquals(opts.size(), 33);
        }else{
            if (config.getExtractionMode() == ExhaustiveExtraction){
                assertEquals(opts.size(), 220);
            }
        }
        assertEquals(new HashSet<String>(res),
                     new HashSet<>(
                            Arrays.asList(
                                    "SELECT DISTINCT ?x WHERE { ?x a <http://dbpedia.org/ontology/Film> . ?I1 <http://dbpedia.org/ontology/starring> ?x }"
                            )
                     ));
    }
    public void testShowMeAllMoviesThatStarredInSpecificMovie() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("", //Separator
                                                                    "show me all",
                                                                    "movies",
                                                                    "that",
                                                                    "starred",
                                                                    "in",
                                                                    "500 Years Later")); //33 options for NaiveExtraction
                                                                                         //220 options for ExhaustiveExtraction
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                     new HashSet<>(
                            Arrays.asList(
                                    "SELECT DISTINCT ?x WHERE {" +
                                    " ?x a <http://dbpedia.org/ontology/Film> ." +
                                    " <http://dbpedia.org/resource/500_Years_Later> <http://dbpedia.org/ontology/starring> ?x }"
                            )
                     ));
    }
    public void testWhatMoviesComposeTheMusicForSpecific() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("", //Separator
                                                                    "what",
                                                                    "movies",
                                                                    "compose the music",
                                                                    "for",
                                                                    "A Dog's Life"));
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<String>(res),
                new HashSet<>(
                        Arrays.asList(
                                "SELECT DISTINCT ?x WHERE {"+
                                " ?x a <http://dbpedia.org/ontology/Film> ."+
                                " <http://dbpedia.org/resource/A_Dog's_Life> <http://dbpedia.org/ontology/musicComposer> ?x "+
                                "}"
                        )
                ));
    }
}