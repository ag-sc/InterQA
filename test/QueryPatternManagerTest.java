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
    public void testSomething() {
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

    }

}