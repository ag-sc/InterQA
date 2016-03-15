package test;


import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Lexicon;
import java.util.HashMap;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author cunger
 */
public class LexiconTest extends TestCase {

    Lexicon lexicon = new Lexicon("en");

    public void test() throws Exception {
        
        lexicon.load("resources/springer_en.ttl");

        HashMap<String,List<LexicalEntry>> commonNouns = lexicon.getSubindex(LexicalEntry.POS.NOUN,null);
        
        assertEquals(true,commonNouns.containsKey("conference"));
        
        List<LexicalEntry> entries = commonNouns.get("conference");
        assertEquals(1,entries.size());
        assertEquals("conference",entries.get(0).getCanonicalForm());
        assertEquals("conference",entries.get(0).getForm(LexicalEntry.Feature.SINGULAR));
        assertEquals("conferences",entries.get(0).getForm(LexicalEntry.Feature.PLURAL));

    }

}
