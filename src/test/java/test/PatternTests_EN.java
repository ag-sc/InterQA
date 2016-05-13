package test;

import interQA.main.interQACLI;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import org.apache.commons.io.input.ReversedLinesFileReader;



public class PatternTests_EN extends TestCase {
    
         
    void checkSequenceByStrings(String sequence, ArrayList<String> SPARQLqueries) throws Exception {
        interQACLI cli = new interQACLI();
        String fileNameIn  = "test1.cli";
        String fileNameOut = "test1.out";
        PrintWriter writer = new PrintWriter(fileNameIn, "UTF-8"); //Overwrites if exists. Goes to class/
        writer.print("2\n"); //Selection by string. Warn: On Windows, println produces \r\n; on Linux produces only \n; on Mac produces only \r
        writer.println(sequence);
        writer.close();
        String args[] = {fileNameIn, fileNameOut};
        cli.main(args);

        //Read the last lines of the output, looking for a line with "SPARQL queries:\n"
        ReversedLinesFileReader object = new ReversedLinesFileReader(new File(fileNameOut)); //Apache commons io
        ArrayList<String> queries = new ArrayList<String>();
        String query = new String();
        while (!(query = object.readLine()).equals("SPARQL queries:")) {  //Reads from the end of the file till this line
            queries.add(query);
        }
        assertEquals(queries, SPARQLqueries);  //Good for ArrayList<String> comparison
    }

    
    // At least one test per pattern
    
        // P_I 
        // proceedings of 
        // start date of 
    
        public void C_P_L() throws Exception {
        //Which <Class:Noun> <Property:Verb> <Literal>?
        //Which conferences took place in 2015?
        //Which actors died in 1999?
        checkSequenceByStrings(
                      "which\n" +
                      "conferences\n" +
                      "took place in\n" +
                      "2015\n" ,
                new ArrayList<String>(
                        Arrays.asList( //We could have several queries here separated by comma
                                "SELECT DISTINCT ?uri WHERE { ?uri  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://lod.springer.com/data/ontology/class/Conference>.   ?uri  <http://lod.springer.com/data/ontology/property/confYear>  \"2015\"^^<http://www.w3.org/2001/XMLSchema#gYear>. }"
                        )
                )
        );
        }
        
        public void P_P_C() throws Exception {
        checkSequenceByStrings(
                      "give me the\n" +
                      "population\n" +
                      "and\n" +
                      "area\n" + 
                      "of\n" + 
                      "cities\n" , 
                new ArrayList<String>(
                        Arrays.asList( //We could have several queries here separated by comma
                                "SELECT DISTINCT ?a ?p WHERE { ?uri  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://dbpedia.org/ontology/City>. ?uri  <http://dbpedia.org/ontology/area> ?a . ?uri <http://dbpedia.org/ontology/population> ?p . }"
                        )
                )
        );
        }
}
