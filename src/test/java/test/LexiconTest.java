package test;


import interQA.lexicon.LexicalEntry;
import interQA.lexicon.LexicalEntry.Language;
import interQA.lexicon.Lexicon;
import interQA.lexicon.Vocabulary;
import java.util.HashMap;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author cunger
 */
public class LexiconTest extends TestCase {

    Vocabulary vocab = new Vocabulary();
    
    Lexicon lexicon = new Lexicon(Language.EN);

    public void test_en() throws Exception {
        
        lexicon.load("src/resources/springer_en.ttl");

        HashMap<String,List<LexicalEntry>> commonNouns = lexicon.getSubindex(LexicalEntry.POS.NOUN,null,true);
        
        assertEquals(true,commonNouns.containsKey("conference"));
        
        List<LexicalEntry> entries = commonNouns.get("conference");
        assertEquals(1,entries.size());
        assertEquals("conference",entries.get(0).getCanonicalForm());
        assertEquals("conference",entries.get(0).getForm(LexicalEntry.Feature.SINGULAR));
        assertEquals("conferences",entries.get(0).getForm(LexicalEntry.Feature.PLURAL));

    }
    
    public void test_de() throws Exception {
        
        lexicon.load("src/resources/springer_de.ttl");

        HashMap<String,List<LexicalEntry>> commonNouns = lexicon.getSubindex(LexicalEntry.POS.NOUN,null,true);
        
        assertEquals(true,commonNouns.containsKey("Konferenz"));
        
        List<LexicalEntry> entries = commonNouns.get("Konferenz");
        assertEquals(1,entries.size());
        assertEquals("Konferenz",entries.get(0).getCanonicalForm());
        assertEquals("Konferenz",entries.get(0).getForm(LexicalEntry.Feature.SINGULAR));
        assertEquals("Konferenzen",entries.get(0).getForm(LexicalEntry.Feature.PLURAL));

    }

}
