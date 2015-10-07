package interQA; /**
 * Created by Mariano on 13/07/2015.
 */

import java.util.*;


public class QueryPattern2 implements QAPattern {

    // Which/What movies/films starred John Travolta?        //select where {?s a dbpedia:Film . ? dbpedia:starre}
    // Which/What movies/films were directed by Tarantino?   //
    // Which/What country/state/county has capital London?




    List<ParsableElement> elements = new ArrayList<ParsableElement>();
    List<String> selections = new ArrayList<String>(); //List of selected elements
    private int lastElementParsed = -1; //The first element parseable is 0

    public QueryPattern2(String fileName) {
        StringElement element0 = new StringElement();      //Element 0
        element0.add("what");
        elements.add(element0);

        ClassNoun element1cn = new ClassNoun("test2.rdf"); //Element 1
        elements.add(element1cn); // movies/films/country

        PropertyNoun element2pn = new PropertyNoun("test2.rdf"); //Element 2
        elements.add(element2pn); // starred/were directed by/has

        InstanceElement element4inst = new InstanceElement(element2pn);  //Element 3.

        //element3pn.setInstances(element3pn.getInstances());
        elements.add(element4inst);

    }

    @Override
    public boolean parses(String input) {
        return false;
    }

    @Override
    public List<String> getNext() {
        return null;
    }

    @Override
    public String getSPARQLQuery() {
        return null;
    }
}
