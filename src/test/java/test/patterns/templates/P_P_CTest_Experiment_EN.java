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
public class P_P_CTest_Experiment_EN extends TestCase {

    QueryPatternManager qm = null;

    //Executed before EACH test
    public void setUp() throws Exception {
        Config config = new Config();
        config.init(Usecase.EXPERIMENT,
                    Language.EN,
                    new ArrayList<String>(Arrays.asList("qpP_P_C"))  //What is the height and weight of NBA players?
                   );
        qm = config.getPatternManager();

    }
    public void testWhatActorsPlayinBatman() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                    "what is the")); //no continuations
                                                                                     // Also for "give me the" and "who is the"

        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                     new HashSet<>(
                            Arrays.asList(
                                    "" //??????????????????????????
                            )
                     ));
    }

}