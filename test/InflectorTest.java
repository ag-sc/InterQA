
import interQA.lexicon.Inflector_en;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;


/**
 * @author cunger
 */
public class InflectorTest extends TestCase {

    Inflector_en infl = new Inflector_en();

    public void testMatches() throws Exception {

        assertEquals(infl.getPlural("flight"),"flights");
        assertEquals(infl.getPlural("fly"),"flies");
        assertEquals(infl.getPlural("dish"),"dishes");
        assertEquals(infl.getPlural("pie"),"pies");

        assertEquals(infl.getPast("try",3),"tried");
        assertEquals(infl.getPast("work",3),"worked");
        assertEquals(infl.getPast("produce",3),"produced");

    }


}