import de.citec.sc.matoll.core.Language;
import de.citec.sc.matoll.core.LexicalEntry;
import junit.framework.TestCase;


/**
 * Created by Mariano on 29/06/2015.
 */
public class LexicalEntryTest extends TestCase {

    LexicalEntry lexentry = new LexicalEntry(Language.EN);

    public void testMatches() throws Exception {
        final String strTestingCF= "testingCF";
        lexentry.setCanonicalForm(strTestingCF);

        assertEquals(lexentry.getLanguage(), Language.EN);
        assertEquals(lexentry.getCanonicalForm(), strTestingCF);
    }


}