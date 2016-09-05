package test.patterns.templates.Springer;


import interQA.Config;
import interQA.Config.Language;
import interQA.Config.Usecase;
import interQA.patterns.QueryPatternManager;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


/**
 * @author Mariano Rico
 */
public class P_ITest_Springer_EN extends TestCase {

    QueryPatternManager qm = null;

    //Executed before EACH test
    public void setUp() throws Exception {
        //Init SPRINGER
        Config config = new Config();
        config.init(Usecase.SPRINGER,
                    Language.EN,
                    new ArrayList<String>(Arrays.asList("qpP_I1",  // give me the start dates of ISWC
                                                        "qpP_I2")  // how many conferences are there?
                                                        )
                   );
        qm = config.getPatternManager();

    }

    public void testGiveMeTheStartDatesOf() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                    "give me the",
                                                                    "start dates",
                                                                    "of")); //No more options!!
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                new HashSet<>(
                        Arrays.asList(
                                "SELECT DISTINCT ?x WHERE { ?I <http://lod.springer.com/data/ontology/property/confStartDate> ?x }"
                        )
                ));
    }

    public void testGiveMeTheStartDatesOfInternationaBlahBlah2015() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                    "give me the",
                                                                    "start dates",
                                                                    "of",
                                                                    "International Semantic Web Conference 2015"));
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                     new HashSet<>(
                            Arrays.asList(
                                    "SELECT DISTINCT ?x WHERE {"+
                                    " <http://lod.springer.com/data/conference/semweb2015> <http://lod.springer.com/data/ontology/property/confStartDate> ?x "+
                                    "}"
                            )
                     ));
    }

    public void testGiveMeTheStartDatesOfISWC2015() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                "give me the",
                "start dates",
                "of",
                "ISWC 2015"));
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                new HashSet<>(
                        Arrays.asList(
                                "SELECT DISTINCT ?x WHERE {"+
                                " <http://lod.springer.com/data/conference/semweb2015> <http://lod.springer.com/data/ontology/property/confStartDate> ?x "+
                                "}"
                        )
                ));
    }

    public void testListAllStartsOfISWC2015() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("", //Separator
                                                                    "list all",
                                                                    "starts",
                                                                    "of",
                                                                    "ISWC 2015"));
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                     new HashSet<String>(
                            Arrays.asList(
                                    "SELECT DISTINCT ?x WHERE {"+
                                    " <http://lod.springer.com/data/conference/semweb2015> <http://lod.springer.com/data/ontology/property/confStartDate> ?x "+
                                    "}"
                            )
                     ));
    }

    public void testWhoTakesPlaceInYucatan() throws Exception { //It is a possible sentence with no sense :-O

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("", //Separator
                                                                    "who",
                                                                    "take place",
                                                                    "in",
                                                                    "Yucatan"));
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                new HashSet<String>(
                        Arrays.asList(
                                "SELECT DISTINCT ?x WHERE { ?x <http://lod.springer.com/data/ontology/property/confCity> \"Yucatan\"@en }"
                        )
                ));
    }
}