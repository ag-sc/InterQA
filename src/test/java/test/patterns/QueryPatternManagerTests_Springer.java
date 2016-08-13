package test.patterns;

import interQA.Config;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.Lexicon;
import interQA.patterns.QueryPatternFactory_EN;
import interQA.patterns.QueryPatternManager;
import junit.framework.TestCase;

import java.util.*;



/**
 * @author Mariano Rico
 */
public class QueryPatternManagerTests_Springer extends TestCase {

    QueryPatternManager qm = null;

    //Executed before EACH test
    public void setUp() throws Exception {
        //Init SPRINGER
        Config config = new Config();
        config.init(Config.USECASE.SPRINGER,
                Config.Language.EN,
                new ArrayList<String>(Arrays.asList("qpC1",  // Give me all mountains.
                                                    "qpC2") // Which movies are there?
                )
        );
        qm = config.getPatternManager();
    }

    public void testDelete1stdElement() throws Exception {

        List<String> options0 = qm.getUIoptions();
        List<String> avlPats1 = qm.getActivePatternsBasedOnUserInput("what");
        List<String> options1 = qm.getUIoptions();
        List<String> avlPats2 = qm.getActivePatternsBasedOnUserInput("");
        List<String> options2 = qm.getUIoptions();

        assertEquals(new HashSet<>(options0),
                     new HashSet<>(options2));
    }

    public void testDelete2ndElement() throws Exception {

        List<String> options0 = qm.getUIoptions();
        List<String> avlPats1 = qm.getActivePatternsBasedOnUserInput("what");
        List<String> options1 = qm.getUIoptions();
        List<String> avlPats2 = qm.getActivePatternsBasedOnUserInput("whatconferences");
        List<String> options2 = qm.getUIoptions();
        List<String> avlPats3 = qm.getActivePatternsBasedOnUserInput("what");
        List<String> options3 = qm.getUIoptions();

        assertEquals(new HashSet<>(options1),
                     new HashSet<>(options3));
        assertEquals(new HashSet<>(avlPats1),
                     new HashSet<>(avlPats3));
    }

    public void testDelete1stdElementAndPutItAgain() throws Exception {

        List<String> options0 = qm.getUIoptions();
        List<String> avlPats1 = qm.getActivePatternsBasedOnUserInput("what");
        List<String> options1 = qm.getUIoptions();
        List<String> avlPats2 = qm.getActivePatternsBasedOnUserInput("");
        List<String> options2 = qm.getUIoptions();
        List<String> avlPats3 = qm.getActivePatternsBasedOnUserInput("what");
        List<String> options3 = qm.getUIoptions();

        assertEquals(new HashSet<>(avlPats1),
                     new HashSet<>(avlPats3));

        assertEquals(new HashSet<>(options1),
                     new HashSet<>(options3));
    }

    public void testDelete2ndElementAndPutItAgain() throws Exception {

        List<String> options0 = qm.getUIoptions();
        List<String> avlPats1 = qm.getActivePatternsBasedOnUserInput("what");
        List<String> options1 = qm.getUIoptions();
        List<String> avlPats2 = qm.getActivePatternsBasedOnUserInput("whatconferences");
        List<String> options2 = qm.getUIoptions();
        List<String> avlPats3 = qm.getActivePatternsBasedOnUserInput("what");
        List<String> options3 = qm.getUIoptions();
        List<String> avlPats4 = qm.getActivePatternsBasedOnUserInput("whatconferences");
        List<String> options4 = qm.getUIoptions();

        assertEquals(new HashSet<>(options2),
                     new HashSet<>(options4));
        assertEquals(new HashSet<>(avlPats2),
                     new HashSet<>(avlPats4));
    }
}