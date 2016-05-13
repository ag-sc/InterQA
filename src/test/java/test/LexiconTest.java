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
        
        lexicon.load("resources/springer_en.ttl");

        HashMap<String,List<LexicalEntry>> commonNouns = lexicon.getSubindex(LexicalEntry.POS.NOUN,vocab.NounPPFrame,true);
        
        assertEquals(true,commonNouns.containsKey("start date"));
        
        List<LexicalEntry> entries = commonNouns.get("start date");
        assertEquals(1,entries.size());
        assertEquals("start date",entries.get(0).getCanonicalForm());
        assertEquals("start date",entries.get(0).getForm(LexicalEntry.Feature.SINGULAR));
        assertEquals("start date",entries.get(0).getForm(LexicalEntry.Feature.PLURAL));

    }
    
    public void test_de() throws Exception {
        
        lexicon.load("resources/springer_de.ttl");

        HashMap<String,List<LexicalEntry>> commonNouns = lexicon.getSubindex(LexicalEntry.POS.NOUN,null,true);
        
        assertEquals(true,commonNouns.containsKey("Konferenz"));
        
        List<LexicalEntry> entries = commonNouns.get("Konferenz");
        assertEquals(1,entries.size());
        assertEquals("Konferenz",entries.get(0).getCanonicalForm());
        assertEquals("Konferenz",entries.get(0).getForm(LexicalEntry.Feature.SINGULAR));
        assertEquals("Konferenzen",entries.get(0).getForm(LexicalEntry.Feature.PLURAL));

    }

}
