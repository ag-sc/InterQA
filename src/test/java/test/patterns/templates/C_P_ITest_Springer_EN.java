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


/**
 * @author Mariano Rico
 */
public class C_P_ITest_Springer_EN extends TestCase {

    QueryPatternManager qm = null;

    //Executed before EACH test
    public void setUp() throws Exception {
        Config config = new Config();
        config.init(Usecase.SPRINGER,
                    Language.EN,
                    new ArrayList<String>(Arrays.asList("qpC_P_I1",  // What conferences took place...
                                                        "qpC_P_I2",  // Give me all conferences that ....
                                                        "qpC_P_I2")  // Which conferences...
                                                        )
                   );
        qm = config.getPatternManager();

    }
    public void testWhatConferencesTookPlaceIn() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                    "what",
                                                                    "conferences",
                                                                    "took place",
                                                                    "in"));

        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                     new HashSet<>(
                            Arrays.asList(
                                    "SELECT DISTINCT ?x WHERE { ?x a <http://lod.springer.com/data/ontology/class/Conference> ; <http://lod.springer.com/data/ontology/property/confCountry> ?I }",
                                    "SELECT DISTINCT ?x WHERE { ?x a <http://lod.springer.com/data/ontology/class/Conference> ; <http://lod.springer.com/data/ontology/property/confYear> ?I }",
                                    "SELECT DISTINCT ?x WHERE { ?x a <http://lod.springer.com/data/ontology/class/Conference> ; <http://lod.springer.com/data/ontology/property/confCity> ?I }"
                            )
                     ));
    }
    public void testWhatConferencesTookPlaceInYucatan() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("", //Separator
                                                                    "what",
                                                                    "conferences",
                                                                    "took place",
                                                                    "in",
                                                                    "Yucatan"));
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                     new HashSet<>(
                            Arrays.asList(
                                    "SELECT DISTINCT ?x WHERE {" +
                                    " ?x a <http://lod.springer.com/data/ontology/class/Conference>" +
                                    " ; <http://lod.springer.com/data/ontology/property/confCity> \"Yucatan\"@en "+
                                    "}"
                            )
                     ));
    }
    public void testWhatConferencesTookPlaceIn1973() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("", //Separator
                                                                    "what",
                                                                    "conferences",
                                                                    "took place",
                                                                    "in",
                                                                    "1973"));
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                new HashSet<>(
                        Arrays.asList(
                                "SELECT DISTINCT ?x WHERE {" +
                                " ?x a <http://lod.springer.com/data/ontology/class/Conference>" +
                                " ; <http://lod.springer.com/data/ontology/property/confYear> \"1973-01-01+01:00\"^^<http://www.w3.org/2001/XMLSchema#gYear> "+
                                "}"
                        )
                ));
    }
}