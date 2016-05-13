package test;

import interQA.main.interQACLI;
import junit.framework.TestCase;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.junit.Assert;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;


/**
 * Created by Mariano on 01/02/2016.
 */
public class interQACLITest extends TestCase {

    
    void checkSequence(String sequence, ArrayList<String> SPARQLqueries) throws Exception {
        interQACLI cli = new interQACLI();
        String fileNameIn  = "test1.cli";
        String fileNameOut = "test1.out";
        PrintWriter writer = new PrintWriter(fileNameIn, "UTF-8"); //Overwrites if exists. Goes to class/
        writer.println("1"); //Selection by number
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

    public void testWhich_C_P_L() throws Exception {
        //Which <Class:Noun> <Property:Verb> <Literal>?
        //Which conferences took place in 2015?
        //Which actors died in 1999?
        checkSequence("3 \n" +     //which
                      "2 \n" +     //conferences
                      "3 \n" +     //took place in
                      "1079 \n" +  //2015  ---> mix of places and years. TODO: provide a visual separation.
                      "2 \n",      //?
                new ArrayList<String>(
                        Arrays.asList( //We could have several queries here separated by comma
                                "SELECT DISTINCT ?uri WHERE { ?uri  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://lod.springer.com/data/ontology/class/Conference>.   ?uri  <http://lod.springer.com/data/ontology/property/confYear>  \"2015\"^^<http://www.w3.org/2001/XMLSchema#gYear>. }"
                        )
                )
        );
    }

    public void testWhich_C_P_L_P_L() throws Exception {
        //Which <Class:Noun> <Property:Verb> <Literal> <Property:Preposition> <Literal> ?
        //Which conferences took place in 2015 in Berlin?
        checkSequence("3 \n" +     //which
                      "2 \n" +     //conferences
                      "3 \n" +     //took place in
                      "1079 \n" +  //2015  ---> mix of places and years. TODO: provide a visual separation.
                      "1 \n" +     //in
                      "218\n"+     //Berlin
                      "1",         //?
                new ArrayList<String>(
                        Arrays.asList( //We could have several queries here separated by comma
                                "SELECT DISTINCT ?uri WHERE { ?uri  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://lod.springer.com/data/ontology/class/Conference>.   ?uri  <http://lod.springer.com/data/ontology/property/confYear>  \"2015\"^^<http://www.w3.org/2001/XMLSchema#gYear>.   ?uri  <http://lod.springer.com/data/ontology/property/confCity>  \"Berlin\"@en.}"
                        )
                )
        );
    }

    public void testGive_me_all_C_that_are_P_L_1() throws Exception {
        //Show me all <Class:Noun> that <Property:Verb> <Literal> <Property:Preposition> <Literal> .
        //Give me all conferences that are held in Berlin.
        checkSequence("7 \n" +     //give me
                      "1 \n" +     //all
                      "5 \n" +     //conferences
                      "1 \n" +     //that
                      "1 \n" +     //are
                      "1 \n" +     //held in
                      "554 \n"+    //Berlin
                      "1",         //.
                new ArrayList<String>(
                        Arrays.asList( //We could have several queries here separated by comma
                                "SELECT DISTINCT ?uri WHERE { ?uri  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://lod.springer.com/data/ontology/class/Conference>.   ?uri  <http://lod.springer.com/data/ontology/property/confCity>  \"Berlin\"@en. }"
                        )
                )
        );
    }



    public void Give_me_all_C_that_P_L_2() throws Exception {
        //Show me all <Class:Noun> that <Property:Verb> <Literal> <Property:Preposition> <Literal> .
        //Show me all conferences that took place in 2015.
        checkSequence("7 \n" +     //show me
                      "1 \n" +     //all
                      "5 \n" +     //conferences
                      "1 \n" +     //that
                      "3 \n" +     //took place in
                      "1079 \n"+   //2015 ---> mix of places and years. TODO: provide a visual separation.
                      "2",         //.
                new ArrayList<String>(
                        Arrays.asList( //We could have several queries here separated by comma
                                "SELECT DISTINCT ?uri WHERE { ?uri  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://lod.springer.com/data/ontology/class/Conference>.   ?uri  <http://lod.springer.com/data/ontology/property/confYear>  \"2015\"^^<http://www.w3.org/2001/XMLSchema#gYear>. }"
                        )
                )
        );
    }

    public void testGive_me_all_C_that_P_L_P_L() throws Exception {
        //Show me all <Class:Noun> that <Property:Verb> <Literal> <Property:Preposition> <Literal> .
        //Show me all conferences that took place in Berlin in 2015. TODO: QP012 made "Which conferences took place in 2015 in Berlin?". Is really a new case?
        checkSequence("4 \n" +     //show me
                      "1 \n" +     //all
                      "5 \n" +     //conferences
                      "1 \n" +     //that
                      "3 \n" +     //took place in
                      "308\n" +    //Berlin
                      "1 \n" +     //in
                      "23 \n"+     //2015 --->  OK: only dates
                      "1",         //.
                new ArrayList<String>(
                        Arrays.asList( //We could have several queries here separated by comma
                                "SELECT DISTINCT ?uri WHERE { ?uri  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://lod.springer.com/data/ontology/class/Conference>.   ?uri  <http://lod.springer.com/data/ontology/property/confCity>  \"Berlin\"@en.   ?uri  <http://lod.springer.com/data/ontology/property/confYear>  \"2015\"^^<http://www.w3.org/2001/XMLSchema#gYear>.}"
                        )
                )
        );
    }

    public void testSpringerQueryPattern4() throws Exception {
        //List|(Give|Show me) all|the <NounPP:Property> <Literal> <Literal>.
        //Show me the proceedings of ISWC 2015.
        checkSequence("4 \n" +     //show me
                      "2 \n" +     //the
                      "2 \n" +     //proceedings of ---> EXCEPTION!!!
                      "1021 \n" +  //International Semantic Web Conference    TODO: there are no acronyms
                      "8 \n" +     //2015
                      "1",         //.
                new ArrayList<String>(
                        Arrays.asList( //We could have several queries here separated by comma
                                "SELECT DISTINCT ?x WHERE {?x <http://lod.springer.com/data/ontology/property/hasConference> ?y.{ ?y  <http://lod.springer.com/data/ontology/property/confAcronym>  \"International Semantic Web Conference\"@en . } UNION { ?y <http://lod.springer.com/data/ontology/property/confName> \"International Semantic Web Conference\"@en . }?y <http://lod.springer.com/data/ontology/property/confYear> \"2015\"^^<http://www.w3.org/2001/XMLSchema#gYear> . }"
                        )
                )
        );
    }

    public void testSpringerQueryPattern5() throws Exception {
        //Give me the <Property:Noun> and <Property:Noun> <Literal> <Literal>
        //Give me the start and end date of ISWC 2015.
        checkSequence("7 \n" +     //give me
                      "2 \n" +     //the
                      "6 \n" +     //start date of TODO: there is no "start and end" ---> EXCEPTION!!!
                      "1021 \n" +  //International Semantic Web Conference    TODO: there are no acronyms
                      "8 \n" +     //2015
                      "1",         //.
                new ArrayList<String>(
                        Arrays.asList( //We could have several queries here separated by comma
                                "SELECT DISTINCT ?lit WHERE {?x <http://lod.springer.com/data/ontology/property/confStartDate> ?lit.{ ?x  <http://lod.springer.com/data/ontology/property/confAcronym>  \"International Semantic Web Conference\"@en . } UNION { ?x <http://lod.springer.com/data/ontology/property/confName> \"International Semantic Web Conference\"@en . }?x <http://lod.springer.com/data/ontology/property/confYear> \"2015\"^^<http://www.w3.org/2001/XMLSchema#gYear> . }"
                        )
                )
        );
    }


}