package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import interQA.lexicon.LexicalEntry;
import interQA.main.interQACLI;
import junit.framework.TestCase;
import static interQA.main.interQACLI.checkSequenceByStrings;



public class PatternTests_EN extends TestCase {
    // At least one test per pattern
    
        // P_I 
        // proceedings of 
        // start date of 
    
        public void test_C() throws Exception {
          // Give me all conferences  
         //SELECT DISTINCT ?x WHERE {  ?x <rdf:type>  <Class:Noun> . }
            assertEquals(
                 checkSequenceByStrings(   //OK
                        "give me all\n" +
                        "conferences\n" +
                        "q\n",
                         interQACLI.USECASE.SPRINGER,  LexicalEntry.Language.EN,
                         new String[]{"qpC1", "qpC2"}
                  ),
                  new HashSet<String>(
                         Arrays.asList(
                            "SELECT DISTINCT ?x WHERE {"+
                            "  ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lod.springer.com/data/ontology/class/Conference> . "+
                            "}"
                         )
                  )
            );
        }
        
        public void test_C_HowMany() throws Exception {
          // Give me all conferences  
         //SELECT DISTINCT ?x WHERE {  ?x <rdf:type>  <Class:Noun> . }
            assertEquals(
                 checkSequenceByStrings(   //OK
                        "how many\n"    +
                        "conferences\n" +
                        "are there\n"   +
                        "q\n"),
                  new HashSet<String>(
                            Arrays.asList(
                                "SELECT COUNT(DISTINCT ?x) WHERE {"+
                                "  ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lod.springer.com/data/ontology/class/Conference> . "+
                                "}"
                            )
                  )
            );   
        }
        /*public void test_C_HowManyV2() throws Exception {
            // Give me all conferences
            //SELECT DISTINCT ?x WHERE {  ?x <rdf:type>  <Class:Noun> . }
            assertEquals(
                    checkSequenceByStrings(   //OK
                            "how many\n"    +
                            "conference\n" +
                            "is there\n"   +
                            "q\n"),
                    new HashSet<String>(
                            Arrays.asList(
                                    "SELECT COUNT(DISTINCT ?x) WHERE {"+
                                            "  ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lod.springer.com/data/ontology/class/Conference> . "+
                                            "}"
                            )
                    )
            );
        }*/
        public void test_P_I() throws Exception{
          // who was the creator Next Top Model Romania  
         //SELECT DISTINCT ?x WHERE {  ?x <rdf:type>  <Class:Noun> . }
            assertEquals(
                   checkSequenceByStrings(     //OK
                        "who was the\n"   +
                        "creator\n"       +
                        "Power Rangers Zeo\n" +
                        "q\n",
                        interQACLI.USECASE.DBPEDIA,  LexicalEntry.Language.EN),
                   new HashSet<String>(
                            Arrays.asList(
                                "SELECT DISTINCT ?x WHERE {"+
                                " <http://dbpedia.org/resource/Power_Rangers_Zeo> <http://dbpedia.org/ontology/creator> ?x . "+
                                "}"
                            )
                   )
            ); 
        }
        
        public void test_C_P_I() throws Exception{
            // SELECT DISTINCT ?x {?x <rdf:Type> <Class:Noun>. ?x <Property:Verb> <Instance> }
            // what skier race FIS Alpine World Ski Championships 2013 ?
            assertEquals(
                    checkSequenceByStrings(   //Wrong!
                        "what\n"   +
                        "skier\n"  +
                        "race\n"   +
                        "FIS Alpine World Ski Championships 2013\n" + //9873 results :-S
                        "q\n",                                        //Exception in C_P_I_P_I.update(C_P_I_P_I.java:107)!!
                        interQACLI.USECASE.DBPEDIA,  LexicalEntry.Language.EN),
                    new HashSet<String>(
                        Arrays.asList(
                          "SELECT DISTINCT ?x {"+
                          " ?x  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://dbpedia.org/ontology/Skier> ."+
                          " ?x  <http://dbpedia.org/ontology/team> <http://dbpedia.org/resource/FIS_Alpine_World_Ski_Championships_2013> . "+
                          "}"
                        )
                    )
            );
        }
        
        public void test_C_P_I_P_I() throws Exception{
            //show me all conferences that take place Piran in 2009
            assertEquals(
                    checkSequenceByStrings(     //Wrong
                            "show me all\n" +
                            "conferences\n" +
                            "that\n"        +
                            "take place\n"  +
                            "Piran\n"       +
                            "in\n"          +
                            "2009\n"        +  //here I get an exception (SparqlQueryBuilder.java:55)
                            "q\n"),
                    new HashSet<String>(
                        Arrays.asList(
                           "SELECT  DISTINCT ?lit1 { ?lit1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> "
                                   + "<http://lod.springer.com/data/ontology/class/Conference> .?lit1  "
                                   + "<http://lod.springer.com/data/ontology/property/confCity>  ?x1 . "
                                   + "FILTER regex(?x1,\"Piran\") . "
                                   + "?lit1  <http://lod.springer.com/data/ontology/property/confYear>  ?x3 . "
                                   + "FILTER regex(?x3,\"2009\") . }"
                        )
                    )
            );
        }
        
      
        
        public void test_C_P_Iv2() throws Exception {
        //Which <Class:Noun> <Property:Verb> <Instance>?
        //Which conferences took place in 2015?
        //Which actors died in 1999?
            assertEquals(
                     checkSequenceByStrings(   //OK
                          "which\n"       +
                          "conferences\n" +
                          "took place\n"  +
                          "Berlin\n"      +
                          "q\n"),
                     new HashSet<String>(
                           Arrays.asList( //We could have several queries here separated by comma
                             "SELECT DISTINCT ?x WHERE {"+
                             " ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lod.springer.com/data/ontology/class/Conference> ."+
                             "?x <http://lod.springer.com/data/ontology/property/confCity> \"Berlin\"@EN . "+
                             "}"
                           )
                     )
             );
        }
        
        public void test_SpringerPattern4() throws Exception{
            //give me the start dates International Working Conference on Requirements Engineering: Foundation for Software Quality 2009
            assertEquals(
                     checkSequenceByStrings(   //OK
                          "give me the\n" +
                          "start dates\n" +
                          "International Working Conference on Requirements Engineering: Foundation for Software Quality\n" +
                          "2009\n" +
                          "q\n"),
                     new HashSet<String>(
                            Arrays.asList( //We could have several queries here separated by comma
                              "SELECT DISTINCT ?lit WHERE {"+
                              "?x <http://lod.springer.com/data/ontology/property/confStartDate> ?y."+
                              "{ ?x  <http://lod.springer.com/data/ontology/property/confAcronym>  ?l1 . } "+
                              "UNION "+
                              "{ ?x <http://lod.springer.com/data/ontology/property/confName> ?l1 . } "+
                              "FILTER regex(?l1,\"International Working Conference on Requirements Engineering: Foundation for Software Quality\"). "+
                              "?x <http://lod.springer.com/data/ontology/property/confYear> ?l2 . "+
                              "FILTER regex(?l2,\"2009\").  "+
                              "}"
                            )
                     )
            );
        }
            
        public void test_SpringerPattern5() throws Exception{
            //give me the start dates and end dates LTEC 2015
            assertEquals(
                    checkSequenceByStrings(   //OK
                          "give me the\n" +
                          "start dates\n" +
                          "and\n"         +
                          "end dates\n"   +
                          "LTEC\n"        +
                          "2015\n" +
                          "q\n"),
                    new HashSet<String>(
                            Arrays.asList( //We could have several queries here separated by comma
                              "SELECT DISTINCT ?lit1 ?lit2  {"+
                              " ?x  <http://lod.springer.com/data/ontology/property/confStartDate> ?lit1."+
                              "?x <http://lod.springer.com/data/ontology/property/confEndDate> ?lit1."+
                              "?x <http://lod.springer.com/data/ontology/property/confYear> ?l1 . FILTER regex(?l1,\"2015\")."+
                              " { ?x  <http://lod.springer.com/data/ontology/property/confAcronym>  ?l2 . }"+
                              " UNION"+
                              " { ?x <http://lod.springer.com/data/ontology/property/confName> ?l2 . }"+
                              " FILTER regex(?l2,\"LTEC\"). "+
                              "}"
                            )
                    )
            );
        }
        
        
        public void test_P_P_C() throws Exception {
            assertEquals(
                    checkSequenceByStrings(   //Wrong!
                          "give me the\n" +
                          "population\n"  +  //Option not available! The plural form "populations' produces 17.799 results but'and' is not an option available
                          "and\n"         +  //now tries this option, but this is not valid either
                          "area\n"        +  //Same
                          "of\n"          +  //Same
                          "cities\n" +       //Same
                          "q\n",
                          interQACLI.USECASE.DBPEDIA,  LexicalEntry.Language.EN),
                    new HashSet<String>(
                            Arrays.asList( //We could have several queries here separated by comma
                                    /*"SELECT DISTINCT ?x WHERE {  ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/City> . }"*/
                                    "SELECT DISTINCT ?a ?p WHERE {"+
                                    " ?uri  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://dbpedia.org/ontology/City>."+
                                    " ?uri  <http://dbpedia.org/ontology/area> ?a ."+
                                    " ?uri <http://dbpedia.org/ontology/population> ?p . "+
                                    "}"
                            )
                    )
             );
        }
        
        public void test_C_P_Iv3() throws Exception{
            //how many conferences took place Atlanta, GA
            assertEquals(
                    checkSequenceByStrings(     //OK. Solved issue with variable names and queries order
                          "how many\n"    +
                          "conferences\n" +
                          "took place\n"  +
                          "Atlanta, GA\n" +
                          "q\n"),
                    new HashSet<String>(
                            Arrays.asList( //We could have several queries here separated by comma
                                    "SELECT DISTINCT ?x WHERE {"+
                                    " ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lod.springer.com/data/ontology/class/Conference> ."+
                                    "?x <http://lod.springer.com/data/ontology/property/confCity> \"Atlanta, GA\"@EN . "+
                                    "}",
                                    "SELECT COUNT(DISTINCT ?x) WHERE {"+
                                    " ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lod.springer.com/data/ontology/class/Conference> ."+
                                    "?x <http://lod.springer.com/data/ontology/property/confCity> \"Atlanta, GA\"@EN . }"
                            )
                    )
            );
        }
}
