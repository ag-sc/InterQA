/**
 * Created by Mariano on 13/07/2015.
 */
import interQA.lexicon.InstanceSource;
import interQA.lexicon.Lexicon;
import interQA.patterns.QueryPattern1_1;
import interQA.patterns.QueryPattern2_1;
import interQA.patterns.QueryPattern2_2;
import interQA.patterns.QueryPatternManager;
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
        QueryPattern2_1 qp21 = new QueryPattern2_1(lexicon, instances);
        QueryPattern2_2 qp22 = new QueryPattern2_2(lexicon, instances);
        qm.addQueryPattern(qp11);
        qm.addQueryPattern(qp21);
        qm.addQueryPattern(qp22);

        StringBuffer parsedText = new StringBuffer();

        List<String> lsElem0qp11 = qp11.getNext(); // Shows "give me"
        List<String> lsElem0qp21 = qp21.getNext(); // Shows "who", "what"
        List<String> lsElem0qp22 = qp22.getNext(); // Shows "who", "what"
        //Display the union of both lists
        List<String> lsElem0display = new ArrayList<String>();
        lsElem0display.addAll(lsElem0qp11);
        lsElem0display.addAll(lsElem0qp21);
        lsElem0display.addAll(lsElem0qp22);

        //UI -> "who" --> both patterns will be valid
        parsedText.append("what");
        boolean parsesElem0qp11 = qp11.parses(parsedText.toString());
        boolean parsesElem0qp21 = qp21.parses(parsedText.toString());
        boolean parsesElem0qp22 = qp22.parses(parsedText.toString());

        // parsesElem0qp11 == false  --> reject p11
        // parsesElem0qp21 & parsesElem0qp22 are true --> go ahead
        List<String> lsElem1qp21 = qp21.getNext(); // Shows "employ", "play", attend".... "span"
        List<String> lsElem1qp22 = qp22.getNext(); // Shows NOTHING
        //Display the union of both lists
        List<String> lsElem1display = new ArrayList<String>();
        lsElem1display.addAll(lsElem1qp21);
        lsElem1display.addAll(lsElem1qp22);

        //UI -> "play" --> only pattern2 is valid
        parsedText.append("play");
        boolean parsesElem1qp21 = qp21.parses(parsedText.toString());  //True
        boolean parsesElem1qp22 = qp22.parses(parsedText.toString());  //False

        //true qp21 --> go ahead only with this

    }

}