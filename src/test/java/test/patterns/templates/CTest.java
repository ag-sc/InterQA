package test.patterns.templates;


import interQA.Config;
import interQA.elements.Element;
import interQA.elements.StringElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import interQA.Config.Language;
import interQA.Config.USECASE;
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
public class CTest extends TestCase {

    QueryPatternManager qm = null;

    //Executed before EACH test
    public void setUp() throws Exception {
        //Init SPRINGER
        Config config = new Config();
        config.init(USECASE.SPRINGER,
                    Language.EN,
                    new ArrayList<String>(Arrays.asList("qpC1",  // Give me all mountains.
                                                         "qpC2") // Which movies are there?
                                                        )
                   );
        qm = config.getPatternManager();

    }
    public void testGiveMeAllconferences() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput("give me allconferences");  //I do not care about the available patterns
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(res,
                new ArrayList<String>(
                        Arrays.asList(
                                "SELECT DISTINCT ?x WHERE {"+
                                " ?x a <http://lod.springer.com/data/ontology/class/Conference> "+
                                "}"
                        )
                ));
    }
    public void testGiveMeTheStartDates() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput("show me thestart dates");  //I do not care about the available patterns
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(res,
                new ArrayList<String>(
                        Arrays.asList(
                                "SELECT DISTINCT ?lit WHERE {"+
                                  "?x <http://lod.springer.com/data/ontology/property/confStartDate> ?y."+
                                  "{ ?x  <http://lod.springer.com/data/ontology/property/confAcronym>  ?l1 . } UNION "+
                                  "{ ?x <http://lod.springer.com/data/ontology/property/confName> ?l1 . } FILTER regex(?l1,\"TRUST\"). "+
                                  "?x <http://lod.springer.com/data/ontology/property/confYear> ?l2 . FILTER regex(?l2,\"2008\").  "+
                                "}"
                        )
                ));
    }
}