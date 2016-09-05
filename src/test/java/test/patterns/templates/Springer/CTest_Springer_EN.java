package test.patterns.templates.Springer;


import interQA.Config;
import interQA.elements.Element;
import interQA.elements.StringElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import interQA.Config.Language;
import interQA.Config.Usecase;
import interQA.patterns.QueryPatternManager;
import interQA.patterns.templates.C;
import interQA.patterns.templates.QueryPattern;
import junit.framework.TestCase;

import java.util.*;

import static interQA.patterns.QueryPatternFactory_EN.addDefGiveMePrefixes;
import static interQA.patterns.QueryPatternFactory_EN.addIndefGiveMePrefixes;


/**
 * @author Mariano Rico
 */
public class CTest_Springer_EN extends TestCase {

    QueryPatternManager qm = null;

    //Executed before EACH test
    public void setUp() throws Exception {
        //Init SPRINGER
        Config config = new Config();
        config.init(Usecase.SPRINGER,
                    Language.EN,
                    new ArrayList<String>(Arrays.asList("qpC1",  // Give me all mountains.
                                                        "qpC2")  // Which movies are there?
                                                        )
                   );
        qm = config.getPatternManager();

    }
    public void testGiveMeAllConferences() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                    "give me all",
                                                                    "conferences"));
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                     new HashSet<>(
                            Arrays.asList(
                                    "SELECT DISTINCT ?x WHERE {"+
                                    " ?x a <http://lod.springer.com/data/ontology/class/Conference> "+
                                    "}"
                            )
                     ));
    }
    public void testHowManyConferences() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("", //Separator
                                                                    "how many",
                                                                    "conferences",
                                                                    "are there"));
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                     new HashSet<>(
                            Arrays.asList(
                                    "SELECT DISTINCT (COUNT(?x) AS ?x_count) WHERE {"+
                                    " ?x a <http://lod.springer.com/data/ontology/class/Conference> "+
                                    "}"
                            )
                     ));
    }
}