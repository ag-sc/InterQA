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
    
        public void C() throws Exception {
          // Give me all conferences  
         //SELECT DISTINCT ?x WHERE {  ?x <rdf:type>  <Class:Noun> . }  
            checkSequenceByStrings(
                        "give me all\n"+
                        "conferences\n",
                        new ArrayList<String>(
                            Arrays.asList(
                            "SELECT DISTINCT ?x WHERE {  ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lod.springer.com/data/ontology/class/Conference> . }"
                            )
                        )
            );   
        }
        
        public void C_HowMany() throws Exception {
          // Give me all conferences  
         //SELECT DISTINCT ?x WHERE {  ?x <rdf:type>  <Class:Noun> . }  
            checkSequenceByStrings(
                        "how many\n"+
                        "conferences\n"+
                        "are there\n",
                        new ArrayList<String>(
                            Arrays.asList(
                            "SELECT COUNT(DISTINCT ?x) WHERE {  ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lod.springer.com/data/ontology/class/Conference> . }"
                            )
                        )
            );   
        }
        public void P_I() throws Exception{
          // who was the creator Next Top Model Romania  
         //SELECT DISTINCT ?x WHERE {  ?x <rdf:type>  <Class:Noun> . }  
            checkSequenceByStrings(
                        "who was the\n"+
                        "creator\n"+
                        "Power Rangers Zeo\n",
                        new ArrayList<String>(
                            Arrays.asList(
                            "SELECT DISTINCT ?x WHERE { <http://dbpedia.org/resource/Power_Rangers_Zeo> <http://dbpedia.org/ontology/creator> ?x . }"
                            )
                        )
            ); 
        }
        
        public void C_P_I() throws Exception{
            // SELECT DISTINCT ?x {?x <rdf:Type> <Class:Noun>. ?x <Property:Verb> <Instance> }
            // what skier race FIS Alpine World Ski Championships 2013 ?
            checkSequenceByStrings(
                    "what\n"+
                    "skier\n"+
                    "race\n"+
                    "FIS Alpine World Ski Championships 2013\n",
                    new ArrayList<String>(
                        Arrays.asList(
                        "SELECT DISTINCT ?a { ?a  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://dbpedia.org/ontology/Skier> . ?a  <http://dbpedia.org/ontology/team> <http://dbpedia.org/resource/FIS_Alpine_World_Ski_Championships_2013> . }"))
            );
            
            
        }
        
        public void C_P_I_P_I() throws Exception{
            //show me all conferences that take place Piran in 2009
            checkSequenceByStrings(
                    "show me all\n"+
                            "conferences\n"+
                            "that\n"+
                            "take place\n"+
                            "Piran\n"+
                            "in\n"+
                            "2009\n",
                    new ArrayList<String>(
                        Arrays.asList(
                        "SELECT DISTINCT DISTINCT ?lit1 { ?lit1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lod.springer.com/data/ontology/class/Conference> .?lit1  <http://lod.springer.com/data/ontology/property/confCity>  ?r . FILTER regex(?r,\"Piran\") . ?lit1  <http://lod.springer.com/data/ontology/property/confYear>  ?d . FILTER regex(?d,\"2009\") . }",
                        "SELECT DISTINCT DISTINCT ?lit1 { ?lit1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lod.springer.com/data/ontology/class/Conference> .?lit1  <http://lod.springer.com/data/ontology/property/confCity>  ?l . FILTER regex(?l,\"Piran\") . ?lit1  <http://lod.springer.com/data/ontology/property/confYear>  ?e . FILTER regex(?e,\"2009\") . }"
                        )));
        }
        
      
        
        public void C_P_Iv2() throws Exception {
        //Which <Class:Noun> <Property:Verb> <Instance>?
        //Which conferences took place in 2015?
        //Which actors died in 1999?
        checkSequenceByStrings(
                      "which\n" +
                      "conferences\n" +
                      "took place\n" +
                      "Berlin\n" ,
                new ArrayList<String>(
                        Arrays.asList( //We could have several queries here separated by comma
                                "SELECT DISTINCT ?x WHERE { ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lod.springer.com/data/ontology/class/Conference> .?x <http://lod.springer.com/data/ontology/property/confCity> \"Berlin\"@EN . }"
                        )
                )
        );
        }
        
        public void SpringerPattern4() throws Exception{
            //give me the start dates International Working Conference on Requirements Engineering: Foundation for Software Quality 2009
            checkSequenceByStrings(
                      "give me the\n" +
                      "start dates\n" +
                      "International Working Conference on Requirements Engineering: Foundation for Software Quality\n" +
                      "2009\n" ,
                new ArrayList<String>(
                        Arrays.asList( //We could have several queries here separated by comma
                                "SELECT DISTINCT ?lit WHERE {?x <http://lod.springer.com/data/ontology/property/confStartDate> ?y.{ ?x  <http://lod.springer.com/data/ontology/property/confAcronym>  ?l1 . } UNION { ?x <http://lod.springer.com/data/ontology/property/confName> ?l1 . } FILTER regex(?l1,\"International Working Conference on Requirements Engineering: Foundation for Software Quality\"). ?x <http://lod.springer.com/data/ontology/property/confYear> ?l2 . FILTER regex(?l2,\"2009\").  }"
                        )
                )
        );}
            
        public void SpringerPattern5() throws Exception{
            //give me the start dates and end dates LTEC 2015
            checkSequenceByStrings(
                      "give me the\n" +
                      "start dates\n" +
                      "and\n" +
                      "end dates\n" +
                      "LTEC\n" +
                      "2015\n" ,
                new ArrayList<String>(
                        Arrays.asList( //We could have several queries here separated by comma
                                "SELECT DISTINCT ?lit1 ?lit2  { ?x  <http://lod.springer.com/data/ontology/property/confStartDate> ?lit1.?x <http://lod.springer.com/data/ontology/property/confEndDate> ?lit1.?x <http://lod.springer.com/data/ontology/property/confYear> ?l1 . FILTER regex(?l1,\"2015\"). { ?x  <http://lod.springer.com/data/ontology/property/confAcronym>  ?l2 . } UNION { ?x <http://lod.springer.com/data/ontology/property/confName> ?l2 . } FILTER regex(?l2,\"LTEC\"). }"
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
        
        public void C_P_Iv3() throws Exception{
            //how many conferences took place Atlanta, GA
        
        checkSequenceByStrings(
                      "how many\n" +
                      "conferences\n" +
                      "took place\n" +
                      "Atlanta, GA\n" ,
                new ArrayList<String>(
                        Arrays.asList( //We could have several queries here separated by comma
                                "SELECT COUNT(DISTINCT ?x) WHERE { ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lod.springer.com/data/ontology/class/Conference> .?x <http://lod.springer.com/data/ontology/property/confCity> \"Atlanta, GA\"@EN . }\n" 
                        )
                )
        );
        }
}
