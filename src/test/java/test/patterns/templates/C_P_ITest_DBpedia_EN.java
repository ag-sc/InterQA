package test.patterns.templates;


import interQA.Config;
import interQA.Config.Language;
import interQA.Config.USECASE;
import interQA.patterns.QueryPatternManager;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


/**
 * @author Mariano Rico
 */
public class C_P_ITest_DBpedia_EN extends TestCase {

    QueryPatternManager qm = null;

    //Executed before EACH test
    public void setUp() throws Exception {
        Config config = new Config();
        config.init(USECASE.DBPEDIA,
                    Language.EN,
                    new ArrayList<String>(Arrays.asList("qpC_P_I1",  // what skiers
                                                        "qpC_P_I2",  // give me all skiers
                                                        "qpC_P_I3")  // which skiers
                                                        )
                   );
        //By default uses NaiveExtraction and does not use historical cache
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
        //We get 1 active pattern (qpC_P_I1)
        List<String> res = qm.buildSPARQLqueries(); //Last valid partial query now it is the ultimate
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

    public void testWholeSequenceMinimumCodeDoesNOTwork() throws Exception {
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

    public void testWholeSequenceMinimumCodeYESwork() throws Exception {

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

}