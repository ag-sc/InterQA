import interQA.lexicon.Lexicon;
import interQA.patterns.QueryPattern1_1;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by Mariano on 26/06/2015.
 */
public class QueryPattern1_1Test {
    // Load lexicon
    private Lexicon lexicon = new Lexicon();
    private QueryPattern1_1 qp1 = null;


    @Test
    public void testFullSequence() {
        // Load lexicon
        lexicon.load("resources/dbpedia_en.rdf");
        qp1 = new QueryPattern1_1(lexicon);

        StringBuffer parsedText = new StringBuffer();

        List<String> lsElem0 = qp1.getNext(); // Shows "give me", "who", "what"...
        //UI -> "give me"
        parsedText.append("give me");
        boolean parsesElem0 = qp1.parses(parsedText.toString());

        //true --> go ahead
        List<String> lsElem1 = qp1.getNext(); // Shows "all"
        //UI --> "all"
        parsedText.append("all");
        boolean parsesElem1 = qp1.parses(parsedText.toString());

        //true --> go ahead
        List<String> lsElem2 = qp1.getNext(); // Shows "actor"...
        //UI --> "actor"
        parsedText.append("actor");
        boolean parsesElem2 = qp1.parses(parsedText.toString());

        //true --> go ahead
        List<String> lsElem3 = qp1.getNext(); // Last element ==> returns null
        //End
        if (lsElem3 == null){
            List<String> sparql = qp1.buildSPARQLqueries();
            System.out.println(sparql);
        }

        //Assert.assertTrue(true);
    }

    @Test
    public void testSequenceWithDelete(){
        // Load lexicon
        lexicon.load("resources/dbpedia_en.rdf");
        qp1 = new QueryPattern1_1(lexicon);

        StringBuffer parsedText = new StringBuffer();

        List<String> lsElem0 = qp1.getNext(); // Shows "give me", "who", "what"...
        //UI -> "give me"
        parsedText.append("give me");
        boolean parsesElem0 = qp1.parses(parsedText.toString());

        //true --> go ahead
        List<String> lsElem1 = qp1.getNext(); // Shows "all"

        //Reset
        parsedText = new StringBuffer("give me");  //This means reset
        boolean parsesElem1 = qp1.parses(parsedText.toString());
        //returns true
        //THis should be the initial "give me", "who", "what"...
        List<String> newres = qp1.getNext(); // Shows "all"
        Assert.assertTrue(newres.get(0).equals("all"));

    }

}