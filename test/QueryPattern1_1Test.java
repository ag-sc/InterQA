import interQA.lexicon.Lexicon;
import interQA.patterns.QueryPattern1_1;
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
    public void testSomething() {
        // Load lexicon
        lexicon.load("resources/dbpedia_en.rdf");
        qp1 = new QueryPattern1_1(lexicon);


        StringBuffer parsedText = new StringBuffer();

        List<String> lsElem0 = qp1.getNext(); // Shows "who", "what"...
        //UI -> "give me"
        parsedText.append("give me");
        boolean parsesElem0 = qp1.parses(parsedText.toString());

        //true --> go ahead
        List<String> lsElem1 = qp1.getNext(); // Shows "is", "was"...
        //UI --> "all"
        parsedText.append("all");
        boolean parsesElem1 = qp1.parses(parsedText.toString());

        //true --> go ahead
        List<String> lsElem2 = qp1.getNext(); // Shows "the", "a"...
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

    public void testParse(){

    }

//    @Test
//    public void testGetInstancesForCanonicalPlusForm(){
//       List<String> inst = interQA.QueryPattern1.getInstancesForCanonicalPlusForm("some URL", "capital of");
//        Assert.assertEquals(inst.toString(), "[http://dbpedia.org/resource/England, http://dbpedia.org/resource/Spain]");
//    }

}