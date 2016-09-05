package test.patterns;


import interQA.Config;
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
public class QueryPatternManagerTests_Experiment extends TestCase {

    QueryPatternManager qm = null;
    Config config = null;

    //Executed before EACH test
    public void setUp() throws Exception {
        //Init SPRINGER
        config = new Config();
        config.init(Config.Usecase.EXPERIMENT,
                    Config.Language.EN,
                    null); //All the patterns defined in the usecase

        qm = config.getPatternManager();
    }

    public void testDelete1stdElement() throws Exception {

        List<String> queries0 = qm.getUIoptions();
        List<String> avlPats1 = qm.getActivePatternsBasedOnUserInput("give me all");
        List<String> queries1 = qm.getUIoptions();
        List<String> avlPats2 = qm.getActivePatternsBasedOnUserInput("");
        List<String> queries2 = qm.getUIoptions();

        assertEquals(new HashSet<>(queries0),
                     new HashSet<>(queries2));
    }

    public void testDelete2ndElement() throws Exception {

        List<String> queries0 = qm.getUIoptions();
        List<String> avlPats1 = qm.getActivePatternsBasedOnUserInput("give me all");
        List<String> queries1 = qm.getUIoptions();
        List<String> avlPats2 = qm.getActivePatternsBasedOnUserInput("give me allfilms");
        List<String> queries2 = qm.getUIoptions();
        List<String> avlPats3 = qm.getActivePatternsBasedOnUserInput("give me all");
        List<String> queries3 = qm.getUIoptions();

        assertEquals(new HashSet<>(queries1),
                     new HashSet<>(queries3));
        assertEquals(new HashSet<>(avlPats1),
                     new HashSet<>(avlPats3));
    }
}