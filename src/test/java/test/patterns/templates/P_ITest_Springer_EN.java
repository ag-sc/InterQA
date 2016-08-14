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
public class P_ITest_Springer_EN extends TestCase {

    QueryPatternManager qm = null;

    //Executed before EACH test
    public void setUp() throws Exception {
        //Init SPRINGER
        Config config = new Config();
        config.init(USECASE.SPRINGER,
                    Language.EN,
                    new ArrayList<String>(Arrays.asList("qpP_I1",  // give me the start dates of ISWC
                                                        "qpP_I2")  // how many conferences are there?
                                                        )
                   );
        qm = config.getPatternManager();

    }
    public void testGiveMeAllconferences() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                    "give me the",
                                                                    "start dates",
                                                                    "of",
                                                                    "ISWC")); //No instances!!!!
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                     new HashSet<>(
                            Arrays.asList(
                                    "" //???????
                            )
                     ));
    }
    public void testHowManyConferences() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("", //Separator
                                                                    "how many",
                                                                    "conferences",
                                                                    "are there")); //No instances!!!!
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                     new HashSet<String>(
                            Arrays.asList(
                                    "" //???????
                            )
                     ));
    }
}