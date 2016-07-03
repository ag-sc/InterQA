package test;

import java.util.ArrayList;
import java.util.Arrays;

import interQA.lexicon.LexicalEntry;
import interQA.main.interQACLI;
import junit.framework.TestCase;
import static interQA.main.interQACLI.checkSequenceByStrings;



public class PatternTests_DE extends TestCase {
    // At least one test per pattern
    
        // P_I 
        // proceedings of 
        // start date of 
    
        public void test_C() throws Exception {
          // Give me all conferences  
         //SELECT DISTINCT ?x WHERE {  ?x <rdf:type>  <Class:Noun> . }
            assertEquals(
                 checkSequenceByStrings(
                        "zeig mir alle\n" +
                        "Konferenzen\n" +
                        "q\n",
                         interQACLI.USECASE.SPRINGER, LexicalEntry.Language.DE),
                  new ArrayList<String>(
                         Arrays.asList(
                            "SELECT DISTINCT ?x WHERE {  ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lod.springer.com/data/ontology/class/Conference> . }"
                         )
                  )
            );
        }
        
        public void test_C_HowMany() throws Exception {
          // Give me all conferences  
         //SELECT DISTINCT ?x WHERE {  ?x <rdf:type>  <Class:Noun> . }
            assertEquals(
                 checkSequenceByStrings(
                        "wieviele\n"    +
                        "Konferenzen\n" +
                        "gibt es\n"   +
                        "q\n"),
                  new ArrayList<String>(
                            Arrays.asList(
                            "SELECT COUNT(DISTINCT ?x) WHERE {  ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lod.springer.com/data/ontology/class/Conference> . }"
                            )
                  )
            );   
        }

//        public void test_P_I() throws Exception{
//          // who was the creator Next Top Model Romania  
//         //SELECT DISTINCT ?x WHERE {  ?x <rdf:type>  <Class:Noun> . }
//            assertEquals(
//                   checkSequenceByStrings(
//                        "who was the\n"   +
//                        "creator\n"       +
//                        "Power Rangers Zeo\n" +
//                        "q\n",
//                        interQACLI.USECASE.DBPEDIA,  LexicalEntry.Language.EN),
//                   new ArrayList<String>(
//                            Arrays.asList(
//                            "SELECT DISTINCT ?x WHERE { <http://dbpedia.org/resource/Power_Rangers_Zeo> <http://dbpedia.org/ontology/creator> ?x . }"
//                            )
//                   )
//            ); 
//        }
//        
//        public void test_C_P_I() throws Exception{
//            // SELECT DISTINCT ?x {?x <rdf:Type> <Class:Noun>. ?x <Property:Verb> <Instance> }
//            // what skier race FIS Alpine World Ski Championships 2013 ?
//            assertEquals(
//                    checkSequenceByStrings(
//                        "what\n"   +
//                        "skier\n"  +   //Option not available!!!!!
//                        "race\n"   +
//                        "FIS Alpine World Ski Championships 2013\n" +
//                        "q\n",
//                        interQACLI.USECASE.SPRINGER,  LexicalEntry.Language.EN),
//                    new ArrayList<String>(
//                        Arrays.asList(
//                          "SELECT DISTINCT ?a { ?a  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://dbpedia.org/ontology/Skier> . ?a  <http://dbpedia.org/ontology/team> <http://dbpedia.org/resource/FIS_Alpine_World_Ski_Championships_2013> . }"
//                        )
//                    )
//            );
//        }
//        
        public void test_C_P_I_P_I() throws Exception{
            //show me all conferences that take place Piran in 2009
            assertEquals(
                    checkSequenceByStrings(
                            "zeig mir alle\n" +
                            "konferenzen\n" +
                            "die\n"        +
                            "2009\n"       +
                            "in\n"          +
                            "Piran\n"        +
                            "stattfanden\n" +
                            "q\n"),
                    new ArrayList<String>(  //The letter of variables depends on the execution. If random, set a seed!!!
                        Arrays.asList(
                           "SELECT DISTINCT DISTINCT ?lit1 {"+
                           " ?lit1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lod.springer.com/data/ontology/class/Conference> ."+
                           "?lit1  <http://lod.springer.com/data/ontology/property/confCity>  ?x1 ."+
                           " FILTER regex(?r,\"Piran\") ."+
                           " ?lit1  <http://lod.springer.com/data/ontology/property/confYear>  ?x2 ."+
                           " FILTER regex(?d,\"2009\") . "+
                           "}"
                           ,
                           "SELECT DISTINCT DISTINCT ?lit1 {"+
                           " ?lit1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lod.springer.com/data/ontology/class/Conference> ."+
                           "?lit1  <http://lod.springer.com/data/ontology/property/confCity>  ?x1 ."+
                           " FILTER regex(?l,\"Piran\") ."+
                           " ?lit1  <http://lod.springer.com/data/ontology/property/confYear>  ?x2 ."+
                           " FILTER regex(?e,\"2009\") . "+
                           "}"
                        )
                    )
            );
        }
        
      
        
        public void test_C_P_Iv2() throws Exception {
        //Which <Class:Noun> <Property:Verb> <Instance>?
        //Which conferences took place in 2015?
        //Which actors died in 1999?
            assertEquals(
                     checkSequenceByStrings( //The letter of variables depends on the execution. If random, set a seed!!!
                          "welche\n"       +
                          "Konferenzen\n" +
                          "fanden in\n"  +
                          "Berlin\n"      +
                          "statt\n" +
                          "q\n"),
                     new ArrayList<String>(
                           Arrays.asList( //We could have several queries here separated by comma
                             "SELECT DISTINCT ?x WHERE { ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lod.springer.com/data/ontology/class/Conference> .?x <http://lod.springer.com/data/ontology/property/confCity> \"Berlin\"@EN . }"
                           )
                     )
             );
        }
        
        public void test_SpringerPattern4() throws Exception{
            //give me the start dates International Working Conference on Requirements Engineering: Foundation for Software Quality 2009
            assertEquals(
                     checkSequenceByStrings(
                          "gib mir die\n" +
                          "Anfangsdaten\n" +
                          "International Working Conference on Requirements Engineering: Foundation for Software Quality\n" +
                          "2009\n" +
                          "q\n"),
                     new ArrayList<String>(
                            Arrays.asList( //We could have several queries here separated by comma
                              "SELECT DISTINCT ?lit WHERE {?x <http://lod.springer.com/data/ontology/property/confStartDate> ?y.{ ?x  <http://lod.springer.com/data/ontology/property/confAcronym>  ?l1 . } UNION { ?x <http://lod.springer.com/data/ontology/property/confName> ?l1 . } FILTER regex(?l1,\"International Working Conference on Requirements Engineering: Foundation for Software Quality\"). ?x <http://lod.springer.com/data/ontology/property/confYear> ?l2 . FILTER regex(?l2,\"2009\").  }"
                            )
                     )
            );
        }
            
        public void test_SpringerPattern5() throws Exception{
            //give me the start dates and end dates LTEC 2015
            assertEquals(
                    checkSequenceByStrings(
                          "gib mir die\n" +
                          "Startdaten\n" +
                          "und\n"         +
                          "Enddaten\n"   +
                          "LTEC\n"        +
                          "2015\n" +
                          "q\n"),
                    new ArrayList<String>(
                            Arrays.asList( //We could have several queries here separated by comma
                              "SELECT DISTINCT ?lit1 ?lit2  { ?x  <http://lod.springer.com/data/ontology/property/confStartDate> ?lit1.?x <http://lod.springer.com/data/ontology/property/confEndDate> ?lit1.?x <http://lod.springer.com/data/ontology/property/confYear> ?l1 . FILTER regex(?l1,\"2015\"). { ?x  <http://lod.springer.com/data/ontology/property/confAcronym>  ?l2 . } UNION { ?x <http://lod.springer.com/data/ontology/property/confName> ?l2 . } FILTER regex(?l2,\"LTEC\"). }"
                            )
                    )
            );
        }
        
//        
//        public void test_P_P_C() throws Exception {
//            assertEquals(
//                    checkSequenceByStrings(
//                          "give me the\n" +
//                          "population\n"  +  //Option not available!!!!
//                          "and\n"         +
//                          "area\n"        +
//                          "of\n"          +
//                          "cities\n" +
//                          "q\n"),
//                    new ArrayList<String>(
//                            Arrays.asList( //We could have several queries here separated by comma
//                                    "SELECT DISTINCT ?a ?p WHERE { ?uri  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://dbpedia.org/ontology/City>. ?uri  <http://dbpedia.org/ontology/area> ?a . ?uri <http://dbpedia.org/ontology/population> ?p . }"
//                            )
//                    )
//             );
//        }
//        
        public void test_C_P_Iv3() throws Exception{
            //how many conferences took place Atlanta, GA
            assertEquals(
                    checkSequenceByStrings(  //The letter of variables depends on the execution. If random, set a seed!!!
                          "wieviele\n"    +
                          "Konferenzen\n" +
                          "fanden in\n"  +
                          "Atlanta, GA\n" +
                          "statt\n" +
                          "q\n"),
                    new ArrayList<String>(
                            Arrays.asList( //We could have several queries here separated by comma
                                    "SELECT COUNT(DISTINCT ?x) WHERE { ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lod.springer.com/data/ontology/class/Conference> .?x <http://lod.springer.com/data/ontology/property/confCity> \"Atlanta, GA\"@EN . }\n"
                            )
                    )
            );
        }
}
