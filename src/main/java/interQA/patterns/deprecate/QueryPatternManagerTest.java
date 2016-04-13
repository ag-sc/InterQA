package interQA.patterns.deprecate;

/**
 * Created by Mariano on 13/07/2015.
 */
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.Lexicon;
//import interQA.patterns.Give_me_all_C;
//import interQA.patterns.Who_P_I;
import interQA.patterns.QueryPatternManager;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class QueryPatternManagerTest {

    //@Test
//    public void testDeleteSecond() {
//        // Load lexicon
//        Lexicon lexicon = new Lexicon("en");
//        lexicon.load("resources/dbpedia_en.rdf");
//        DatasetConnector instances = new DatasetConnector("http://dbpedia.org/sparql","en");
//
//        // Load query patterns
//        QueryPatternManager qm = new QueryPatternManager();
//        Give_me_all_C qp11 = new Give_me_all_C(lexicon, instances);
//        Who_P_I qp22 = new Who_P_I(lexicon, instances);
//        qm.addQueryPatterns(qp11);
//        qm.addQueryPatterns(qp22);
//
//        StringBuffer parsedText = new StringBuffer();
//        List<String> opts = qm.getUIoptions();  // "what", "give me", "who"
//
//        //UI selects "give me" --> one pattern will be valid
//        parsedText.append("give me");
//        qm.userSentence(parsedText.toString()); //Sends the user selection to the qm
//        opts = qm.getUIoptions();  //get options available for last selection: "all"
//
//        //UI selects "all"
//        parsedText.append("all");
//        qm.userSentence(parsedText.toString()); //Sends the user selection to the qm
//        opts = qm.getUIoptions();  //get options available for last selection: many of them
//
//        //UI deletes last selection
//        qm.userSentence("give me");
//        opts = qm.getUIoptions();  // you should get "all" again
//        Assert.assertTrue(opts.get(0).equals("all"));
//
//        //UI deletes last selection again (deletes everything)
//        qm.userSentence("");
//        opts = qm.getUIoptions();  // you should get "what" again
//        Assert.assertTrue(opts.get(0).equals("what"));
//    }
//
//    @Test
//    public void testDeleteFirst() {
//        // Load lexicon
//        Lexicon lexicon = new Lexicon("en");
//        lexicon.load("resources/dbpedia_en.rdf");
//        DatasetConnector instances = new DatasetConnector("http://dbpedia.org/sparql","en");
//
//        // Load query patterns
//        QueryPatternManager qm = new QueryPatternManager();
//        Give_me_all_C qp11 = new Give_me_all_C(lexicon, instances);
//        Who_P_I qp22 = new Who_P_I(lexicon, instances);
//        qm.addQueryPatterns(qp11);
//        qm.addQueryPatterns(qp22);
//
//        List<String> opts = qm.getUIoptions();  // "what", "give me", "who"
//        qm.userSentence("give me"); //Sends the user selection to the qm
//        opts = qm.getUIoptions();  //get options available for last selection: many of them
//
//        qm.userSentence("");  //Equivalent to remove "give me"
//        opts = qm.getUIoptions();  // you should get "what", "give me", "who" again
//
//        Assert.assertTrue(opts.get(0).equals("what"));
//
//    }
//
//    @Test
//    public void testStartsEmpty() {
//        // Load lexicon
//        Lexicon lexicon = new Lexicon("en");
//        lexicon.load("resources/dbpedia_en.rdf");
//        DatasetConnector instances = new DatasetConnector("http://dbpedia.org/sparql","en");
//
//        // Load query patterns
//        QueryPatternManager qm = new QueryPatternManager();
//        Give_me_all_C qp11 = new Give_me_all_C(lexicon, instances);
//        Who_P_I qp22 = new Who_P_I(lexicon, instances);
//        qm.addQueryPatterns(qp11);
//        qm.addQueryPatterns(qp22);
//
//        StringBuffer parsedText = new StringBuffer();
//        List<String> opts = qm.getUIoptions();  // "what", "give me", "who"
//
//        qm.userSentence(""); //Sends the user selection to the qm
//        opts = qm.getUIoptions();  //get options available for last selection: "all"
//
//        qm.userSentence("give me"); //Sends the user selection to the qm
//        opts = qm.getUIoptions();  //get options available for last selection: many of them
//
//        qm.userSentence("give meall");
//        opts = qm.getUIoptions();  // you should get "all" again
//
//        qm.userSentence("give me"); //Sends the user selection to the qm
//        opts = qm.getUIoptions();  //get options available for last selection: many of them
//
//        Assert.assertTrue(opts.get(0).equals("all"));
//
//    }
//
//    @Test
//    public void testRightSentenceGetSPARQL() {
//        // Load lexicon
//        Lexicon lexicon = new Lexicon("en");
//        lexicon.load("resources/dbpedia_en.rdf");
//        DatasetConnector instances = new DatasetConnector("http://dbpedia.org/sparql","en");
//
//        // Load query patterns
//        QueryPatternManager qm = new QueryPatternManager();
//        Give_me_all_C qp11 = new Give_me_all_C(lexicon, instances);
//        Who_P_I qp22 = new Who_P_I(lexicon, instances);
//        qm.addQueryPatterns(qp11);
//        qm.addQueryPatterns(qp22);
//
//        qm.userSentence("give meallpopes");
//        List<String> opts = null;
//        opts = qm.getUIoptions();  // you should get ""
//        Assert.assertTrue(opts.isEmpty());
//        
//        List<String> ql = qm.buildSPARQLqueries();        
//        Assert.assertTrue(ql.get(0).equals(
//           "SELECT DISTINCT ?x WHERE {  ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Pope> . }"
//        ));
//
//    }
//
//    @Test
//    public void testRightSentenceGetSPARQLPoltergeist() {
//        // Load lexicon
//        Lexicon lexicon = new Lexicon("en");
//        lexicon.load("resources/dbpedia_en.rdf");
//        DatasetConnector instances = new DatasetConnector("http://dbpedia.org/sparql","en");
//
//        // Load query patterns
//        QueryPatternManager qm = new QueryPatternManager();
//        Give_me_all_C qp11 = new Give_me_all_C(lexicon, instances);
//        Who_P_I qp22 = new Who_P_I(lexicon, instances);
//        qm.addQueryPatterns(qp11);
//        qm.addQueryPatterns(qp22);
//
//        qm.userSentence("give meallpope");
//        List<String> opts = null;
//        opts = qm.getUIoptions();  // you should get ""
//        Assert.assertTrue(opts.isEmpty());
//
//        qm.userSentence("give meall");
//        opts = qm.getUIoptions();  // you should get many options
//        Assert.assertTrue(opts.size() > 1);
//
//    }
    
}