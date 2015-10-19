import interQA.elements.StringElement;
import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * Created by Mariano on 26/06/2015.
 */
public class StringElementTest extends TestCase {

//    public void testMatches() throws Exception {
//        StringElement element = new StringElement();
//        element.add("which");
//        element.add("what");
//        element.add("who");
//        assertEquals(element.matches("which"), true);
//        assertEquals(element.matches("who"), true);
//    }

    public void test1Parse() throws Exception {
        StringElement element = new StringElement();
        element.add("which");
        element.add("what");
        element.add("who");
        assertEquals(element.parse("whois"), "is");
        assertEquals(element.parse("who is"), " is");
        assertEquals(element.parse("my"), null);
    }
    public void test2Parse() throws Exception {
        StringElement element = new StringElement();
        element.add("which are");
        element.add("what is");
        element.add("who are");
        assertEquals(element.parse("which are the"), " the");
        assertEquals(element.parse("who are people"), " people");
        assertEquals(element.parse("what is the capital"), " the capital");
    }

    public void test3Parse() throws Exception {
        StringElement element = new StringElement();
        element.add("who");
        element.add("who is");
        element.add("who are");
        assertEquals(element.parse("who is"), "");  //matches the biggest
    }

    public void testLookahead() throws Exception {
        StringElement element = new StringElement();
        element.add("is");  //Good previous: [Who]
        element.add("the"); //Good previous: [What, is]
        element.add("are"); //Good previous: [What]
        ArrayList<String> previous = new ArrayList<>();
        previous.add ("Who");
//        assertEquals(element.lookahead(previous), "is");
//        assertEquals(element.lookahead("who is"), " is");
//        assertEquals(element.lookahead("my"), null);
    }
}