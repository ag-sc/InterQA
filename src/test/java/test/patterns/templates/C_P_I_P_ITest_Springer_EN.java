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

import static interQA.Config.ExtractionMode.NaiveExtraction;
import static interQA.Config.ExtractionMode.exahustiveExtraction;


/**
 * @author Mariano Rico
 */
public class C_P_I_P_ITest_Springer_EN extends TestCase {

    Config config = null;
    QueryPatternManager qm = null;

    //Executed before EACH test
    public void setUp() throws Exception {
        config = new Config();
        config.init(USECASE.SPRINGER,
                    Language.EN,
                    new ArrayList<String>(Arrays.asList("qpC_P_I_P_I1",  //Show me all conferences that took place in Berlin in 2015.
                                                        "qpC_P_I_P_I2")  //Which conferences took place in Berlin in 2015?
                                                        )
                   );
        //By default uses NaiveExtraction and does not use historical cache
        //config.setCacheMode(exahustiveExtraction, true); //This test runs OK for both modes, but in exhaustive+Historical mode...
                                                         //each method requires 8GB and 16GB Heap space respectively.

        qm = config.getPatternManager();

    }
    public void testShowMeConferencesThatTookPlace() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                    "show me all",
                                                                    "conferences",
                                                                    "that",
                                                                    "took place"));
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                     new HashSet<>(
                            Arrays.asList(
                             "SELECT DISTINCT ?x WHERE { ?x a <http://lod.springer.com/data/ontology/class/Conference> ; <http://lod.springer.com/data/ontology/property/confCity> ?I1 }",
                             "SELECT DISTINCT ?x WHERE { ?x a <http://lod.springer.com/data/ontology/class/Conference> ; <http://lod.springer.com/data/ontology/property/confYear> ?I1 }",
                             "SELECT DISTINCT ?x WHERE { ?x a <http://lod.springer.com/data/ontology/class/Conference> ; <http://lod.springer.com/data/ontology/property/confCountry> ?I1 }"
                            )
                     ));
    }
    public void testShowMeConferencesThatTookPlaceInSpecificYear() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                    "show me all",
                                                                    "conferences",
                                                                    "that",
                                                                    "took place",
                                                                    "in",
                                                                    "2015"));
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                new HashSet<>(
                        Arrays.asList(
                                "SELECT DISTINCT ?x WHERE {"+
                                " ?x a <http://lod.springer.com/data/ontology/class/Conference>"+
                                " ; <http://lod.springer.com/data/ontology/property/confYear> \"2015-01-01+01:00\"^^<http://www.w3.org/2001/XMLSchema#gYear> }"
                        )
                ));
    }
    public void testShowMeConferencesThatTookPlaceInSpecificPlace() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                    "show me all",
                                                                    "conferences",
                                                                    "that",
                                                                    "took place",
                                                                    "in",
                                                                    "Bangkok"));
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                new HashSet<>(
                        Arrays.asList(
                                "SELECT DISTINCT ?x WHERE {"+
                                " ?x a <http://lod.springer.com/data/ontology/class/Conference>"+
                                " ; <http://lod.springer.com/data/ontology/property/confCity> \"Bangkok\"@en }"
                        )
                ));
    }

    public void testShowMeConferencesThatTookPlaceInSpecificPlaceInSpecificYear() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                    "show me all",
                                                                    "conferences",
                                                                    "that",
                                                                    "took place",
                                                                    "in",
                                                                    "Bangkok",
                                                                    "in",
                                                                    "2014")); //BUG!!! We have years and also places :-S
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                new HashSet<>(
                        Arrays.asList(
                                "SELECT DISTINCT ?x WHERE {"+
                                " ?x a <http://lod.springer.com/data/ontology/class/Conference>"+
                                " ; <http://lod.springer.com/data/ontology/property/confCity> \"Bangkok\"@en"+
                                " ; <http://lod.springer.com/data/ontology/property/confYear> \"2014-01-01+01:00\"^^<http://www.w3.org/2001/XMLSchema#gYear> "+
                                "}"
                        )
                ));
    }

    public void testWhichConferencesTakePlaceInBangkokIn2014() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("", //Separator
                                                                    "which",
                                                                    "conferences",
                                                                    "take place",
                                                                    "in",
                                                                    "Bangkok",
                                                                    "in",
                                                                    "2014")); //BUG!!! We have years and also places :-S
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                new HashSet<>(
                        Arrays.asList(
                                "SELECT DISTINCT ?x WHERE {"+
                                " ?x a <http://lod.springer.com/data/ontology/class/Conference>"+
                                " ; <http://lod.springer.com/data/ontology/property/confCity> \"Bangkok\"@en"+
                                " ; <http://lod.springer.com/data/ontology/property/confYear> \"2014-01-01+01:00\"^^<http://www.w3.org/2001/XMLSchema#gYear> "+
                                "}"
                        )
                ));
    }
}