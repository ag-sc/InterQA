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
public class C_P_ITest_Experiment_EN extends TestCase {

    QueryPatternManager qm = null;

    //Executed before EACH test
    public void setUp() throws Exception {
        Config config = new Config();
        config.init(USECASE.EXPERIMENT,
                    Language.EN,
                    new ArrayList<String>(Arrays.asList("qpC_P_I1",  // What actors play in Batman?
                                                        "qpC_P_I2",  // Give me all actors that play in Batman.
                                                        "qpC_P_I2")  // Which movie/actor is NO CONTINUATION
                                                        )
                   );
        qm = config.getPatternManager();

    }
    public void testWhatActorsPlayinBatman() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                    "what",
                                                                    "person",   //language has no continuation
                                                                    "appears"));// starred/appeared/"compose the music" has no continuation
                                                                   //No continuations!!!
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
                                                                    "give me all",
                                                                    "actors",
                                                                    "that",
                                                                    "appear",
                                                                    "in")); //There are no further options :-S
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                     new HashSet<>(
                            Arrays.asList(
                                    "" //?????????????????????
                            )
                     ));
    }
}