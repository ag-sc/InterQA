package test.patterns;

import interQA.Config;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import interQA.main.interQACLI;
import interQA.patterns.QueryPatternFactory_EN;
import interQA.patterns.QueryPatternManager;
import junit.framework.TestCase;

import java.util.*;



/**
 * @author Mariano Rico
 */
public class QueryPatternManagerTests_Springer extends TestCase {

    Config.Language lang = Config.Language.EN;
    Lexicon lexicon = new Lexicon(lang);
    QueryPatternManager qm = new QueryPatternManager();
    DatasetConnector dataset  = null;
    Config.USECASE usecase = Config.USECASE.SPRINGER;

    //Executed before EACH test
    public void setUp() throws Exception {
        //Init SPRINGER
        lexicon.load("./src/main/java/resources/springer_en.ttl");
        lexicon.extractEntries();
        dataset = new DatasetConnector("http://es.dbpedia.org/sparql",lang, usecase);

        // Load query patterns
        QueryPatternFactory_EN qf_en = new QueryPatternFactory_EN(usecase,lexicon,dataset);
        qm.addQueryPatterns(
            qf_en.rollout(
                new ArrayList<String>(
                      Arrays.asList("qpC1", // Give me all mountains.
                                    "qpC2") // Which movies are there?
                )
            )
        );
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

}