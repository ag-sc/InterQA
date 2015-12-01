
import interQA.lexicon.LexicalEntry;
import junit.framework.TestCase;


/**
 * Created by Mariano on 29/06/2015.
 */
public class LexicalEntryTest extends TestCase {

    LexicalEntry lexentry = new LexicalEntry();

    public void testMatches() throws Exception {
        
        final String strTestingCF  = "testingCF";
        final String strTestingRef = "testingRef";
        
        lexentry.setCanonicalForm(strTestingCF);
        lexentry.setReference(strTestingRef);

        assertEquals(lexentry.getCanonicalForm(),strTestingCF);
        assertEquals(lexentry.getReference(),strTestingRef);
    }


}