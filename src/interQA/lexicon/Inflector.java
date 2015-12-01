package interQA.lexicon;

/**
 *
 * @author cunger
 */
public interface Inflector {
    
    public String getPlural(String noun);
    
    public String getPresent(String verb, int person); 
    public String getPast(String verb, int person);
    public String getPastParticiple(String verb);
}
