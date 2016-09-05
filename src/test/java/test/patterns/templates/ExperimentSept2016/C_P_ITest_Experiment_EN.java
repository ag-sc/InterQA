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
public class C_P_ITest_Experiment_EN extends TestCase {

    QueryPatternManager qm = null;

    //Executed before EACH test
    public void setUp() throws Exception {
        Config config = new Config();
        config.init(Usecase.EXPERIMENT,
                    Language.EN,
                    new ArrayList<String>(Arrays.asList("qpC_P_I1",  // What actors play in Batman?
                                                        "qpC_P_I2",  // Give me all actors that play in Batman.
                                                        "qpC_P_I2")  // Which movie/actor is ...
                                                        )
                   );
        //By default uses NaiveExtraction and does not use historical cache
        config.setCacheMode(ExhaustiveExtraction, true);

        qm = config.getPatternManager();

    }
    public void testWhatPersonAppear() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                    "what",
                                                                    "person",   //language is the ... weird query with City
                                                                    "appear"));

        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                     new HashSet<>(
                            Arrays.asList(
                                    "SELECT DISTINCT ?x WHERE { ?x a <http://dbpedia.org/ontology/Person> . ?I <http://dbpedia.org/ontology/starring> ?x }"
                            )
                     ));
    }

    public void testWhatPersonAppearInSpecific() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                    "what",
                                                                    "person",
                                                                    "appear",
                                                                    "in",
                                                                    "Adimakkachavadam"));
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                     new HashSet<>(
                        Arrays.asList(
                                "SELECT DISTINCT ?x WHERE {"+
                                " ?x a <http://dbpedia.org/ontology/Person> ."+
                                " <http://dbpedia.org/resource/Adimakkachavadam> <http://dbpedia.org/ontology/starring> ?x "+
                                "}"
                        )
                ));
    }


    public void testGiveMeAllActorsThatAppearInSpecific() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("", //Separator
                                                                    "give me all",
                                                                    "actors",
                                                                    "that",
                                                                    "appear",
                                                                    "in",
                                                                    "Acción mutante"));
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                     new HashSet<>(
                            Arrays.asList(
                                    "SELECT DISTINCT ?x WHERE {"+
                                    " ?x a <http://dbpedia.org/ontology/Actor> ."+
                                    " <http://dbpedia.org/resource/Acción_mutante> <http://dbpedia.org/ontology/starring> ?x "+
                                    "}"
                            )
                     ));
    }
}