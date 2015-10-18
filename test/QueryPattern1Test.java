import interQA.patterns.QueryPattern1;
import org.junit.Test;

import java.util.List;

/**
 * Created by Mariano on 26/06/2015.
 */
public class QueryPattern1Test {
    private QueryPattern1 qp1 = new QueryPattern1("test1.rdf");


    @Test
    public void testSomething() {
        StringBuffer parsedText = new StringBuffer();

        List<String> lsElem0 = qp1.getNext(); // Shows "who", "what"...
        //UI -> "who"
        parsedText.append("what");
        boolean parsesElem0 = qp1.parses(parsedText.toString());

        //true --> go ahead
        List<String> lsElem1 = qp1.getNext(); // Shows "is", "was"...
        //UI --> "is"
        parsedText.append("is");
        boolean parsesElem1 = qp1.parses(parsedText.toString());

        //true --> go ahead
        List<String> lsElem2 = qp1.getNext(); // Shows "the", "a"...
        //UI --> "the"
        parsedText.append("the");
        boolean parsesElem2 = qp1.parses(parsedText.toString());

        //true --> go ahead
        List<String> lsElem3 = qp1.getNext(); // Shows "movie by",  "movie with", "capital of"...
        //UI --> "movie by"
        parsedText.append("movie by");
        boolean parsesElem3 = qp1.parses(parsedText.toString());

        //Now I have to get the property associated to the canonical Form, and the "direction" of
        // the property (linear, reverse). Both from the lexicalEntry
        //With this information I can query the EP and get the instances (if linear, I get the object
        // of the triples; if reverse, the subject of the

        //true --> go ahead
        List<String> lsElem4 = qp1.getNext(); // "Tarantino", "Almodovar" (because the last section was "movie by")
        parsedText.append("Tarantino");
        boolean parsesElem4= qp1.parses(parsedText.toString());

        //true --> go ahead
        if (parsesElem4){
            String sparql = qp1.getSPARQLQuery();
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