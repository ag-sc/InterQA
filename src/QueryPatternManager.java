import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mariano on 13/07/2015.
 */
public class QueryPatternManager {
    List<QAPattern> queryPatterns = new ArrayList<QAPattern>();

    public QueryPatternManager(){

    }
    public void addQueryPattern(QAPattern pattern){
        queryPatterns.add(pattern);
    }
}
