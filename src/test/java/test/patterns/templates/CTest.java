package test.patterns.templates;


import interQA.elements.Element;
import interQA.elements.StringElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
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

    LexicalEntry.Language lang = LexicalEntry.Language.EN;
    Lexicon lexicon = new Lexicon(lang);
    List<String> labels = new ArrayList<>();
    QueryPatternManager qm = new QueryPatternManager();
    DatasetConnector dataset  = null;

    //Executed before EACH test
    public void setUp() throws Exception {
        //Init SPRINGER
        lexicon.load("./src/main/java/resources/springer_en.ttl");
        labels.add("http://www.w3.org/2000/01/rdf-schema#label");
        labels.add("http://lod.springer.com/data/ontology/property/confName");
        labels.add("http://lod.springer.com/data/ontology/property/confAcronym");
        dataset = new DatasetConnector("http://es.dbpedia.org/sparql",lang,labels);

        Set<QueryPattern> patterns = new HashSet<>();
//        // Give me all conferences.
        QueryPattern q1 = new C(lexicon,dataset);

        addDefGiveMePrefixes((StringElement) q1.getElement(0));
        addIndefGiveMePrefixes((StringElement) q1.getElement(0));

        //AddNoun equivalent
        Element e =  q1.getElement(1);
        e.addEntries(lexicon, LexicalEntry.POS.NOUN, null);

        patterns.add(q1);
        qm.addQueryPatterns(patterns);
    }

    public void testGiveMeAllconferences() throws Exception {

        List<String> avlPats = qm.userSentence("give me allconferences");  //I do not care about the available patterns
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

        List<String> avlPats = qm.userSentence("show me thestart dates");  //I do not care about the available patterns
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