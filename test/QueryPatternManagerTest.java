/**
 * Created by Mariano on 13/07/2015.
 */
import interQA.lexicon.InstanceSource;
import interQA.lexicon.Lexicon;
import interQA.patterns.QueryPattern1_1;
import interQA.patterns.QueryPattern9_1;
import interQA.patterns.QueryPattern9_2;
import interQA.patterns.QueryPatternManager;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class QueryPatternManagerTest {

    @Test
    public void testDeleteSecond() {
        // Load lexicon
        Lexicon lexicon = new Lexicon();
        lexicon.load("resources/dbpedia_en.rdf");
        InstanceSource instances = new InstanceSource("http://dbpedia.org/sparql");

        // Load query patterns
        QueryPatternManager qm = new QueryPatternManager();
        QueryPattern1_1 qp11 = new QueryPattern1_1(lexicon);
        QueryPattern9_1 qp21 = new QueryPattern9_1(lexicon, instances);
        QueryPattern9_2 qp22 = new QueryPattern9_2(lexicon, instances);
        qm.addQueryPattern(qp11);
        qm.addQueryPattern(qp21);
        qm.addQueryPattern(qp22);

        StringBuffer parsedText = new StringBuffer();
        List<String> opts = qm.getUIoptions();  // "what", "give me", "who"

        //UI selects "give me" --> one pattern will be valid
        parsedText.append("give me");
        qm.userSentence(parsedText.toString()); //Sends the user selection to the qm
        opts = qm.getUIoptions();  //get options available for last selection: "all"

        //UI selects "all"
        parsedText.append("all");
        qm.userSentence(parsedText.toString()); //Sends the user selection to the qm
        opts = qm.getUIoptions();  //get options available for last selection: many of them

        //UI deletes last selection
        qm.userSentence("give me");
        opts = qm.getUIoptions();  // you should get "all" again
        Assert.assertTrue(opts.get(0).equals("all"));

        //UI deletes last selection again (deletes everything)
        qm.userSentence("");
        opts = qm.getUIoptions();  // you should get "what" again
        Assert.assertTrue(opts.get(0).equals("what"));
    }

    @Test
    public void testDeleteFirst() {
        // Load lexicon
        Lexicon lexicon = new Lexicon();
        lexicon.load("resources/dbpedia_en.rdf");
        InstanceSource instances = new InstanceSource("http://dbpedia.org/sparql");

        // Load query patterns
        QueryPatternManager qm = new QueryPatternManager();
        QueryPattern1_1 qp11 = new QueryPattern1_1(lexicon);
        QueryPattern9_1 qp21 = new QueryPattern9_1(lexicon, instances);
        QueryPattern9_2 qp22 = new QueryPattern9_2(lexicon, instances);
        qm.addQueryPattern(qp11);
        qm.addQueryPattern(qp21);
        qm.addQueryPattern(qp22);

        List<String> opts = qm.getUIoptions();  // "what", "give me", "who"
        qm.userSentence("give me"); //Sends the user selection to the qm
        opts = qm.getUIoptions();  //get options available for last selection: many of them

        qm.userSentence("");  //Equivalent to remove "give me"
        opts = qm.getUIoptions();  // you should get "what", "give me", "who" again

        Assert.assertTrue(opts.get(0).equals("what"));

    }

    @Test
    public void testStartsEmpty() {
        // Load lexicon
        Lexicon lexicon = new Lexicon();
        lexicon.load("resources/dbpedia_en.rdf");
        InstanceSource instances = new InstanceSource("http://dbpedia.org/sparql");

        // Load query patterns
        QueryPatternManager qm = new QueryPatternManager();
        QueryPattern1_1 qp11 = new QueryPattern1_1(lexicon);
        QueryPattern9_1 qp21 = new QueryPattern9_1(lexicon, instances);
        QueryPattern9_2 qp22 = new QueryPattern9_2(lexicon, instances);
        qm.addQueryPattern(qp11);
        qm.addQueryPattern(qp21);
        qm.addQueryPattern(qp22);

        StringBuffer parsedText = new StringBuffer();
        List<String> opts = qm.getUIoptions();  // "what", "give me", "who"

        qm.userSentence(""); //Sends the user selection to the qm
        opts = qm.getUIoptions();  //get options available for last selection: "all"

        qm.userSentence("give me"); //Sends the user selection to the qm
        opts = qm.getUIoptions();  //get options available for last selection: many of them

        qm.userSentence("give meall");
        opts = qm.getUIoptions();  // you should get "all" again

        qm.userSentence("give me"); //Sends the user selection to the qm
        opts = qm.getUIoptions();  //get options available for last selection: many of them

        Assert.assertTrue(opts.get(0).equals("all"));

    }

    @Test
    public void testRightSentenceGetSPARQL() {
        // Load lexicon
        Lexicon lexicon = new Lexicon();
        lexicon.load("resources/dbpedia_en.rdf");
        InstanceSource instances = new InstanceSource("http://dbpedia.org/sparql");

        // Load query patterns
        QueryPatternManager qm = new QueryPatternManager();
        QueryPattern1_1 qp11 = new QueryPattern1_1(lexicon);
        QueryPattern9_1 qp21 = new QueryPattern9_1(lexicon, instances);
        QueryPattern9_2 qp22 = new QueryPattern9_2(lexicon, instances);
        qm.addQueryPattern(qp11);
        qm.addQueryPattern(qp21);
        qm.addQueryPattern(qp22);

        qm.userSentence("give meallpope");
        List<String> opts = null;
        opts = qm.getUIoptions();  // you should get ""
        Assert.assertTrue(opts.isEmpty());

        List<String> ql = qm.buildSPARQLqueries();
        Assert.assertTrue(ql.get(0).equals(
           "SELECT DISTINCT ?x WHERE {  ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Pope> . }"
        ));

    }

}