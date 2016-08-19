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
public class P_P_ITest_DBpedia_EN extends TestCase {

    QueryPatternManager qm = null;

    //Executed before EACH test
    public void setUp() throws Exception {
        Config config = new Config();
        config.init(USECASE.DBPEDIA,
                    Language.EN,
                    new ArrayList<String>(Arrays.asList("qpP_P_I"))  //What is the height and weight of Michael Jordan?
                   );
        qm = config.getPatternManager();

    }
    public void testWhatIsTheLanguageAndGenre() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                    "what is the",
                                                                    "language",
                                                                    "and",
                                                                    "genre")); //No continuation


        List<String> res = qm.buildSPARQLqueries();
        List<String> opts = qm.getUIoptions();
        assertEquals(new HashSet<>(res),
                     new HashSet<>(
                            Arrays.asList(
                                    "" //??????????????????????????
                            )
                     ));
    }

    public void testWhatIsTheAreaAndPopulationOfBangkok() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                        "what is the",
                                                                        "area",
                                                                        "and",
                                                                        "population",
                                                                        "of",
                                                                        "Bangkok"));


        List<String> res = qm.buildSPARQLqueries();   //1 query!!
        List<String> opts = qm.getUIoptions();        //0 opts!!
        assertEquals(new HashSet<>(res),
                new HashSet<>(
                        Arrays.asList(
                                "SELECT DISTINCT ?x ?y WHERE {"+
                                " <http://dbpedia.org/resource/Bangkok> <http://dbpedia.org/ontology/areaMetro> ?x"+
                                " ; <http://dbpedia.org/ontology/populationTotal> ?y"+
                                " }"
                        )
                ));
    }

    public void testWhatIsTheAreaAndPopulationOfBangkokV2() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                        "what is the",
                                                                        "area",
                                                                        "and",
                                                                        "population",
                                                                        "of",
                                                                        "Bangkok"));


        List<String> res = qm.buildSPARQLqueries();   //1 query!!
        List<String> opts = qm.getUIoptions();
        assertEquals(new HashSet<>(res),
                new HashSet<>(
                        Arrays.asList(
                                "SELECT DISTINCT ?x ?y WHERE {"+
                                        " <http://dbpedia.org/resource/Bangkok> <http://dbpedia.org/ontology/areaMetro> ?x"+
                                        " ; <http://dbpedia.org/ontology/populationTotal> ?y"+
                                        " }"
                        )
                ));
    }

}