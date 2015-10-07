/**
 * Created by Mariano on 13/07/2015.
 */
import interQA.QueryPattern1;
import interQA.QueryPattern2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class QueryPatternManagerTest {

    @Test
    public void testSomething() {
        QueryPattern1 qp1 = new QueryPattern1("test1.rdf");
        QueryPattern2 qp2 = new QueryPattern2("test2.rdf");
        StringBuffer parsedText = new StringBuffer();

        List<String> lsElem0qp1 = qp1.getNext(); // Shows "which", "what", "who"...
        List<String> lsElem0qp2 = qp2.getNext(); // Shows "which", "what", "who"...
        //Display the union of both lists
        List<String> lsElem0display = new ArrayList<String>();
        lsElem0display.addAll(lsElem0qp1);
        lsElem0display.addAll(lsElem0qp2);

        //UI -> "who" --> both patterns will be valid
        parsedText.append("what");
        boolean parsesElem0qp1 = qp1.parses(parsedText.toString());
        boolean parsesElem0qp2 = qp2.parses(parsedText.toString());

        //true both --> go ahead
        List<String> lsElem1qp1 = qp1.getNext(); // Shows "is", "are", "was"...
        List<String> lsElem1qp2 = qp2.getNext(); // Shows "movies", "films", "country"...
        //Display the union of both lists
        List<String> lsElem1display = new ArrayList<String>();
        lsElem1display.addAll(lsElem1qp1);
        lsElem1display.addAll(lsElem1qp2);

        //UI -> "films" --> only pattern2 is valid
        parsedText.append("films");
        boolean parsesElem1qp1 = qp1.parses(parsedText.toString());
        boolean parsesElem1qp2 = qp2.parses(parsedText.toString());

        //true qp2 --> go ahead only with this

    }

}