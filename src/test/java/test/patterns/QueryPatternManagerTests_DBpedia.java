package test.patterns;


import interQA.Config;
import interQA.patterns.QueryPatternManager;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.List;


/**
 * @author Mariano Rico
 */
public class QueryPatternManagerTests_DBpedia extends TestCase {

    QueryPatternManager qm = null;

    //Executed before EACH test
    public void setUp() throws Exception {
        //Init SPRINGER
        Config config = new Config();
        config.init(Config.Usecase.DBPEDIA,
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
        List<String> avlPats3 = qm.getActivePatternsBasedOnUserInput("give me all");
        List<String> queries3 = qm.getUIoptions();

        assertEquals(queries0.size(), queries2.size()); //18 for NaiveExtraction
        assertEquals(queries1.size(), queries3.size()); //487 for NaiveExtraction
        assertEquals(avlPats1.size(), avlPats3.size()); //4 for NaiveExtraction
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
        List<String> avlPats4 = qm.getActivePatternsBasedOnUserInput("give me allfilms");
        List<String> queries4 = qm.getUIoptions();

        assertEquals(queries1.size(), queries3.size()); //487 for NaiveExtraction
        assertEquals(new HashSet<>(queries1),
                     new HashSet<>(queries3));
        assertEquals(avlPats1.size(), avlPats3.size()); //4 for NaiveExtraction
        assertEquals(avlPats2.size(), avlPats4.size()); //3 for NaiveExtraction
        assertEquals(new HashSet<>(avlPats1),
                     new HashSet<>(avlPats3));
    }

}