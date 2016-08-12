package test.patterns;


import interQA.elements.Element;
import interQA.elements.StringElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import interQA.main.interQACLI;
import interQA.patterns.QueryPatternFactory_EN;
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
public class QueryPatternManagerTests_Springer extends TestCase {

    LexicalEntry.Language lang = LexicalEntry.Language.EN;
    Lexicon lexicon = new Lexicon(lang);
    List<String> labels = new ArrayList<>();
    QueryPatternManager qm = new QueryPatternManager();
    DatasetConnector dataset  = null;
    interQACLI.USECASE usecase = interQACLI.USECASE.SPRINGER;

    //Executed before EACH test
    public void setUp() throws Exception {
        //Init SPRINGER
        lexicon.load("./src/main/java/resources/springer_en.ttl");
        labels.add("http://www.w3.org/2000/01/rdf-schema#label");
        labels.add("http://lod.springer.com/data/ontology/property/confName");
        labels.add("http://lod.springer.com/data/ontology/property/confAcronym");
        lexicon.extractEntries();
        dataset = new DatasetConnector("http://es.dbpedia.org/sparql",lang,labels);

        // Load query patterns
        QueryPatternFactory_EN qf_en = new QueryPatternFactory_EN(usecase,lexicon,dataset);
        qm.addQueryPatterns(qf_en.rollout());
    }

    public void testDelete1stdElement() throws Exception {

        List<String> queries0 = qm.getUIoptions();
        List<String> avlPats1 = qm.getRemainingActivePatterns("what");
        List<String> queries1 = qm.getUIoptions();
        List<String> avlPats2 = qm.getRemainingActivePatterns("");
        List<String> queries2 = qm.getUIoptions();

        assertEquals(new HashSet<>(queries0),
                     new HashSet<>(queries2));
    }

    public void testDelete2ndElement() throws Exception {

        List<String> queries0 = qm.getUIoptions();
        List<String> avlPats1 = qm.getRemainingActivePatterns("what");
        List<String> queries1 = qm.getUIoptions();
        List<String> avlPats2 = qm.getRemainingActivePatterns("whatconferences");
        List<String> queries2 = qm.getUIoptions();
        List<String> avlPats3 = qm.getRemainingActivePatterns("what");
        List<String> queries3 = qm.getUIoptions();

        assertEquals(new HashSet<>(queries1),
                     new HashSet<>(queries3));
        assertEquals(new HashSet<>(avlPats1),
                     new HashSet<>(avlPats3));
    }

}