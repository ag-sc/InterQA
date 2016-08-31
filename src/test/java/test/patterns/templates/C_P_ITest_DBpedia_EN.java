package test.patterns.templates;


import interQA.Config;
import interQA.Config.Language;
import interQA.Config.Usecase;
import interQA.elements.StringElement;
import interQA.lexicon.LexicalEntry;

import static interQA.Config.ExtractionMode.ExhaustiveExtraction;
import static interQA.patterns.QueryPatternFactory.vocab;
import interQA.patterns.QueryPatternManager;
import interQA.patterns.templates.C_P_I;
import interQA.patterns.templates.QueryPattern;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


/**
 * @author Mariano Rico
 */
public class C_P_ITest_DBpedia_EN extends TestCase {

    Config config = null;
    QueryPatternManager qm = null;

    //Executed before EACH test
    public void setUp() throws Exception {
        config = new Config();
        config.init(Usecase.DBPEDIA,
                    Language.EN,
                    new ArrayList<String>(Arrays.asList("qpC_P_I1",  // what skiers
                                                        "qpC_P_I2",  // give me all skiers
                                                        "qpC_P_I3")  // which skiers
                                                        )
                   );
        //By default uses NaiveExtraction and does not use historical cache
        //This test works for both modes
        //config.setCacheMode(ExhaustiveExtraction, true); //The exhaustive with historical requires up to 8GB heap

        qm = config.getPatternManager();

    }
    public void testUpToClassElement() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                                "what",
                                                                                "skiers"));
        //We get 1 active pattern (qpC_P_I1).
        List<String> res = qm.buildSPARQLqueries(); //Last valid partial query
        List<String> opts = qm.getUIoptions();

        assertEquals(new HashSet<>(res),
                new HashSet<>(
                        Arrays.asList(
                                "SELECT DISTINCT ?x WHERE { ?x a <http://dbpedia.org/ontology/Skier> }"
                        )
                ));
        assertEquals(opts.size(), 13); //among others, "race"
    }

    public void testUpToPropertyElement() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                                "what",
                                                                                "skiers",
                                                                                "race"));
        //We get 1 active pattern (qpC_P_I1).
        List<String> res = qm.buildSPARQLqueries(); //Last valid partial query
        List<String> opts = qm.getUIoptions();

        assertEquals(new HashSet<>(res),
                new HashSet<>(
                        Arrays.asList(
                                "SELECT DISTINCT ?x WHERE { ?x a <http://dbpedia.org/ontology/Skier> ; <http://dbpedia.org/ontology/team> ?I }"
                        )
                ));
        assertEquals(opts.size(), 1); // "for"
    }

    public void testUpToInstanceElement() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                    "what",
                                                                    "skiers",
                                                                    "race",
                                                                    "for"));
        //"race" produces a select query:
        // SELECT DISTINCT * WHERE { ?x ?P2 ?I2 ; a <http://dbpedia.org/ontology/Skier> ; <http://dbpedia.org/ontology/team> ?I1 OPTIONAL { ?I1 <http://www.w3.org/2000/01/rdf-schema#label> ?l } }
        //This is an extractive query with 1.082.256 results

        //However we also make this query
        // SELECT DISTINCT * WHERE { ?x a <http://dbpedia.org/ontology/Skier> ; <http://dbpedia.org/ontology/team> ?I OPTIONAL { ?I <http://www.w3.org/2000/01/rdf-schema#label> ?l } }
        //That only has 8267 results... why??

        //We get 1 active pattern (qpC_P_I1). If we run will all the patterns we get another: C_P_I_P_I
        List<String> res = qm.buildSPARQLqueries(); //Last valid partial query
        List<String> opts = qm.getUIoptions();

        assertEquals(new HashSet<>(res),
                     new HashSet<>(
                            Arrays.asList(  // If we run will all the patterns we get another:
                                   //SELECT DISTINCT ?x WHERE { ?x a <http://dbpedia.org/ontology/Skier> ; <http://dbpedia.org/ontology/team> ?I1 }
                                    "SELECT DISTINCT ?x WHERE { ?x a <http://dbpedia.org/ontology/Skier> ; <http://dbpedia.org/ontology/team> ?I }"
                            )
                     ));
        assertEquals(opts.size(), 88); //Among others, "FIS Alpine World Ski Championships 2007"
    }

    public void testInstances() throws Exception {

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                                "what",
                                                                                "skiers",
                                                                                "race",
                                                                                "for",
                                                                                "FIS Alpine World Ski Championships 2007" ));
        //Number of patterns available: 2 [C_P_I_P_I, C_P_I]
        List<String> res = qm.buildSPARQLqueries();
        List<String> opts = qm.getUIoptions();

        assertEquals(new HashSet<>(res),
                new HashSet<>(
                        Arrays.asList(
                                "SELECT DISTINCT ?x WHERE { ?x a <http://dbpedia.org/ontology/Skier>"+
                                " ; <http://dbpedia.org/ontology/team> <http://dbpedia.org/resource/FIS_Alpine_World_Ski_Championships_2007> "+
                                "}"
                        )
                ));
        assertEquals(avlPats.size(), 1); //There must be 1 active pattern
        assertEquals(opts.size(), 0);    //Although we still have an active pattern, there are no more options
    }

    public void testWholeSequence() throws Exception {
        qm.getActivePatternsBasedOnUserInput(String.join("",
                                                         "what"));
        qm.buildSPARQLqueries();
        qm.getUIoptions();

        qm.getActivePatternsBasedOnUserInput(String.join("",
                                                         "what",
                                                         "skiers"));
        qm.buildSPARQLqueries();
        qm.getUIoptions();

        qm.getActivePatternsBasedOnUserInput(String.join("",
                                                        "what",
                                                        "skiers",
                                                        "race"));
        qm.buildSPARQLqueries();
        qm.getUIoptions();

        qm.getActivePatternsBasedOnUserInput(String.join("",
                                                        "what",
                                                        "skiers",
                                                        "race",
                                                        "for"));
        qm.buildSPARQLqueries();
        qm.getUIoptions();

        List<String> avlPats =
        qm.getActivePatternsBasedOnUserInput(String.join("",
                                                        "what",
                                                        "skiers",
                                                        "race",
                                                        "for",
                                                        "FIS Alpine World Ski Championships 2007" ));
        List<String> res = qm.buildSPARQLqueries();
        List<String> opts = qm.getUIoptions();

        assertEquals(avlPats.size(), 1); //There must be 1 active pattern
        assertEquals(opts.size(), 0);
        assertEquals(new HashSet<>(res),
                new HashSet<>(
                        Arrays.asList(
                                "SELECT DISTINCT ?x WHERE { ?x a <http://dbpedia.org/ontology/Skier>"+
                                        " ; <http://dbpedia.org/ontology/team> <http://dbpedia.org/resource/FIS_Alpine_World_Ski_Championships_2007> "+
                                        "}"
                        )
                ));

    }

    public void testWholeSequenceMinimumCode() throws Exception {
        qm.getActivePatternsBasedOnUserInput(String.join("",
                "what"));

        qm.getActivePatternsBasedOnUserInput(String.join("",
                "what",
                "skiers"));

        qm.getActivePatternsBasedOnUserInput(String.join("",
                "what",
                "skiers",
                "race"));

        qm.getActivePatternsBasedOnUserInput(String.join("",
                "what",
                "skiers",
                "race",
                "for"));

        List<String> avlPats =
                qm.getActivePatternsBasedOnUserInput(String.join("",
                        "what",
                        "skiers",
                        "race",
                        "for",
                        "FIS Alpine World Ski Championships 2007" ));
        List<String> res = qm.buildSPARQLqueries();
        List<String> opts = qm.getUIoptions();

        assertEquals(avlPats.size(), 1); //There must be 1 active pattern
        assertEquals(opts.size(), 0);
        assertEquals(new HashSet<>(res),
                new HashSet<>(
                        Arrays.asList(
                                "SELECT DISTINCT ?x WHERE { ?x a <http://dbpedia.org/ontology/Skier>"+
                                        " ; <http://dbpedia.org/ontology/team> <http://dbpedia.org/resource/FIS_Alpine_World_Ski_Championships_2007> "+
                                        "}"
                        )
                ));

    }

    public void testWholeSequenceMinimumCodeDidNOTwork() throws Exception {
        qm.getActivePatternsBasedOnUserInput(String.join("",
                "what",
                "skiers",
                "race"));

        qm.getActivePatternsBasedOnUserInput(String.join("",
                "what",
                "skiers",
                "race",
                "for"));

        List<String> avlPats =
                qm.getActivePatternsBasedOnUserInput(String.join("",
                        "what",
                        "skiers",
                        "race",
                        "for",
                        "FIS Alpine World Ski Championships 2007" ));
        List<String> res = qm.buildSPARQLqueries();
        List<String> opts = qm.getUIoptions();

        assertEquals(avlPats.size(), 1); //There must be 1 active pattern
        assertEquals(opts.size(), 0);
        assertEquals(new HashSet<>(res),
                new HashSet<>(
                        Arrays.asList(
                                "SELECT DISTINCT ?x WHERE { ?x a <http://dbpedia.org/ontology/Skier>"+
                                        " ; <http://dbpedia.org/ontology/team> <http://dbpedia.org/resource/FIS_Alpine_World_Ski_Championships_2007> "+
                                        "}"
                        )
                ));

    }

    public void testWholeSequenceMinimumCodeDidYesWork() throws Exception {

        qm.getActivePatternsBasedOnUserInput(String.join("",
                                            "what",
                                            "skiers",
                                            "race",
                                            "for"));

        List<String> avlPats =
                qm.getActivePatternsBasedOnUserInput(String.join("",
                                                    "what",
                                                    "skiers",
                                                    "race",
                                                    "for",
                                                    "FIS Alpine World Ski Championships 2007" ));
        List<String> res = qm.buildSPARQLqueries();
        List<String> opts = qm.getUIoptions();

        assertEquals(avlPats.size(), 1); //There must be 1 active pattern
        assertEquals(opts.size(), 0);
        assertEquals(new HashSet<>(res),
                new HashSet<>(
                        Arrays.asList(
                                "SELECT DISTINCT ?x WHERE { ?x a <http://dbpedia.org/ontology/Skier>"+
                                        " ; <http://dbpedia.org/ontology/team> <http://dbpedia.org/resource/FIS_Alpine_World_Ski_Championships_2007> "+
                                        "}"
                        )
                ));

    }
    
    public void testParse() {
        
        QueryPattern p = new C_P_I(config.getLexicon(), config.getDatasetConnector());

        StringElement e5_0 = (StringElement) p.getElement(0);
        e5_0.add("what");

        p.getElement(1).addEntries(config.getLexicon(), LexicalEntry.POS.NOUN, null);
        p.getElement(3).addEntries(config.getLexicon(), LexicalEntry.POS.VERB, vocab.IntransitivePPFrame, false);

        p.addAgreementDependency(0,1);
        p.addAgreementDependency(1,2);
        p.addAgreementDependency(1,3);

        boolean success;
        
        QueryPattern clone3 = p.clone();
        success = clone3.parse("whatskiersraceforCanada at the 2006 Winter Olympics");
        assertTrue(success);
        
        QueryPattern clone2 = p.clone();
        success = clone2.parse("whatskiersracefor");
        assertTrue(success);
        
        QueryPattern clone4 = p.clone();
        success = clone4.parse("whatskiersraceforCanada at the 2006 Winter Olympics");
        assertTrue(success);
    }

}