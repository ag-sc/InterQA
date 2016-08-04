package test;

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
          // Give me all wrestlers
         //SELECT DISTINCT ?x WHERE {  ?x <rdf:type>  <Class:Noun> . }
            assertEquals(
                 checkSequenceByStrings(
                        "give me all\n" +
                        "wrestlers\n" +
                        "q\n",
                        interQACLI.USECASE.DBPEDIA,  LexicalEntry.Language.EN),
                  new HashSet<>(
                         Arrays.asList(
                            "SELECT DISTINCT ?x WHERE { ?x a <http://dbpedia.org/ontology/Wrestler> }"
                         )
                  )
            );
        }
        
        public void test_C_HowMany() throws Exception {
          // How many wrestlers are there
         //SELECT DISTINCT ?x WHERE {  ?x <rdf:type>  <Class:Noun> . }
            assertEquals(
                 checkSequenceByStrings(
                        "how many\n"    +
                        "wrestlers\n" +
                        "are there\n"   +
                        "q\n",
                         interQACLI.USECASE.DBPEDIA,  LexicalEntry.Language.EN),
                  new HashSet<>(
                            Arrays.asList(
                                "SELECT DISTINCT (COUNT(?x) AS ?x_count) WHERE { ?x a <http://dbpedia.org/ontology/Wrestler> }"
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
                        "who was the\n" +
                        "creator\n" +
                        "of\n" +
                        "Power Rangers Zeo\n" +
                        "q\n",
                        interQACLI.USECASE.DBPEDIA,  LexicalEntry.Language.EN),
                   new HashSet<>(
                            Arrays.asList(
                                "SELECT DISTINCT ?x WHERE { <http://dbpedia.org/resource/Power_Rangers_Zeo> <http://dbpedia.org/ontology/creator> ?x }"
                            )
                   )
            ); 
        }
        
        public void test_C_P_I() throws Exception{
            // SELECT DISTINCT ?x {?x <rdf:Type> <Class:Noun>. ?x <Property:Verb> <Instance> }
            // what skier race FIS Alpine World Ski Championships 2013 ?
            assertEquals(
                    checkSequenceByStrings( // no query is built
                        "what\n"    +
                        "skiers\n"  +
                        "race\n"    +
                        "for\n"     +
                        "2006 Winter Olympics\n" +
                        "q\n", 
                        interQACLI.USECASE.DBPEDIA,  LexicalEntry.Language.EN),
                    new HashSet<>(
                        Arrays.asList(
                          "SELECT DISTINCT ?x WHERE { ?x a <http://dbpedia.org/ontology/Skier> ; <http://dbpedia.org/ontology/team> <http://dbpedia.org/resource/2006_Winter_Olympics> }"
                        )
                    )
            );
        }
        
        public void test_C_P_I_P_I() throws Exception{
            //show me all conferences that take place Piran in 2009
            assertEquals(
                    checkSequenceByStrings(
                            "show me all\n" +
                            "conferences\n" +
                            "that\n"        +
                            "took place\n"  +
                            "in\n"          +
                            "Piran\n"       +
                            "in\n"          + 
                            "2009\n"        +
                            "q\n"),
                    new HashSet<>(
                        Arrays.asList(
                           "SELECT DISTINCT ?lit1 { ?lit1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> "
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
                     checkSequenceByStrings(
                          "which\n"       +
                          "conferences\n" +
                          "took place\n"  +
                          "in\n"          +
                          "Berlin\n"      +
                          "q\n"),
                     new HashSet<>(
                           Arrays.asList( //We could have several queries here separated by comma
                             "SELECT DISTINCT ?x WHERE {"+
                             " ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lod.springer.com/data/ontology/class/Conference> ."+
                             " ?x <http://lod.springer.com/data/ontology/property/confCity> \"Berlin\"@EN . "+
                             "}"
                           )
                     )
             );
        }
        
        public void test_SpringerPattern4() throws Exception{
            //give me the start dates International Working Conference on Requirements Engineering: Foundation for Software Quality 2009
            assertEquals(
                     checkSequenceByStrings(   // No option after the name of the conference
                          "give me the\n" +
                          "start dates\n" +
                          "of\n"          +
                          "International Working Conference on Requirements Engineering: Foundation for Software Quality\n" +
                          "2009\n" +
                          "q\n"),
                     new HashSet<>(
                            Arrays.asList( //We could have several queries here separated by comma
                              "SELECT DISTINCT ?y WHERE {"+
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
                    checkSequenceByStrings(
                          "give me the\n" +
                          "start dates\n" +
                          "and\n"         +
                          "end dates\n"   +
                          "of\n"          +
                          "LTEC\n"        +
                          "2015\n" +
                          "q\n"),
                    new HashSet<>(
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
        
        public void test_P_P_I() throws Exception {
            assertEquals(
                    checkSequenceByStrings(  
                          "what is the\n" +
                          "area\n" +  
                          "and\n" +  
                          "population\n" +
                          "of\n" +
                          "Aarhus\n" +       
                          "q\n",
                          interQACLI.USECASE.DBPEDIA,LexicalEntry.Language.EN),
                    new HashSet<>(
                            Arrays.asList( // We will have several queries here 
                                    "SELECT DISTINCT ?x ?y WHERE {"+
                                    " <http://dbpedia.org/resource/Aarhus> <http://dbpedia.org/ontology/areaTotal> ?x ."+
                                    " <http://dbpedia.org/resource/Aarhus> <http://dbpedia.org/ontology/populationTotal> ?y . "+
                                    "}"
                            )
                    )
             );
        }
        
        public void test_P_P_C() throws Exception {
            assertEquals(
                    checkSequenceByStrings(   
                          "give me the\n" +
                          "population\n" +  
                          "and\n" +  
                          "area" +
                          "of\n" +  
                          "cities\n" +       
                          "q\n",
                          interQACLI.USECASE.DBPEDIA,LexicalEntry.Language.EN),
                    new HashSet<>(
                            Arrays.asList( // We will have several queries
                                    "SELECT DISTINCT ?x ?y WHERE {"+
                                    " ?i <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/City> . "+
                                    " ?i <http://dbpedia.org/ontology/populationTotal> ?x . "+
                                    " ?i <http://dbpedia.org/ontology/areaTotal> ?y ."+
                                    "}"
                            )
                    )
             );
        }
        
        public void test_C_P_Iv3() throws Exception{
            //how many conferences took place Atlanta, GA
            assertEquals(
                    checkSequenceByStrings(
                          "how many\n"    +
                          "conferences\n" +
                          "took place\n"  +
                          "in\n"          +
                          "Atlanta, GA\n" +
                          "q\n"),
                    new HashSet<>(
                            Arrays.asList( //We could have several queries here separated by comma
                                    "SELECT (COUNT(?x) AS ?x_count) WHERE {"+
                                    " ?x a <http://lod.springer.com/data/ontology/class/Conference> ."+
                                    " ?x <http://lod.springer.com/data/ontology/property/confCity> \"Atlanta, GA\"@EN . }"
                            )
                    )
            );
        }
}
