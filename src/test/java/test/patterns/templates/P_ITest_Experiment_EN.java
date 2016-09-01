package test.patterns.templates;


import interQA.Config;
import interQA.Config.Language;
import interQA.Config.Usecase;
import interQA.lexicon.DatasetConnector;
import interQA.patterns.QueryPatternManager;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


/**
 * @author Mariano Rico
 */
public class P_ITest_Experiment_EN extends TestCase {

    Config config = null;

    //Executed before EACH test
    public void setUp() throws Exception {
        //Init SPRINGER
        Config config = new Config();
        config.init(Usecase.EXPERIMENT,
                    Language.EN,
                    new ArrayList<String>(Arrays.asList("qpP_I1",  // who is the composer for X
                                                                   // who were....
                                                        "qpP_I2")  // who starred/appeared X? //1.209.398 starred
                                                        )          // who compose the music for X //201.444 ?i musicComposer ?x
                   );
        this.config = config;

    }

    public void testWhoComposeTheMusicFor() throws Exception {

        QueryPatternManager qm = config.getPatternManager();

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                    "who",        //"give me the", "who is the" or "who was the" or "who are the" --> No continuation
                                                                    "compose the music",
                                                                    "for"));

        List<String> opts = qm.getUIoptions();
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                new HashSet<>(
                        Arrays.asList(
                                "SELECT DISTINCT ?x WHERE { ?I <http://dbpedia.org/ontology/musicComposer> ?x }"
                        )
                ));
    }

    public void testWhoComposeTheMusicForABerlinRomance() throws Exception {

        QueryPatternManager qm = config.getPatternManager();

        List<String> avlPats = qm.getActivePatternsBasedOnUserInput(String.join("",
                                                                    "who",        //"give me the", "who is the" or "who was the" or "who are the" --> No continuation
                                                                    "compose the music",
                                                                    "for",
                                                                    "A Berlin Romance"));
        List<String> opts = qm.getUIoptions();
        List<String> res = qm.buildSPARQLqueries();

        assertEquals(new HashSet<>(res),
                     new HashSet<>(
                            Arrays.asList(
                                    "SELECT DISTINCT ?x WHERE { <http://dbpedia.org/resource/A_Berlin_Romance> <http://dbpedia.org/ontology/musicComposer> ?x }"
                            )
                     ));
    }

}