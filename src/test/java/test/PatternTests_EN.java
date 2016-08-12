package test;

import java.util.Arrays;
import java.util.HashSet;

import interQA.Config.Language;
import interQA.Config.USECASE;
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
                 checkSequenceByStrings(    //OK. This case does not make any Select query to the EP
                        "give me all\n" +
                        "wrestlers\n" +
                        "q\n",
                        USECASE.DBPEDIA,  Language.EN),
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
                         USECASE.DBPEDIA,  Language.EN),
                  new HashSet<>(
                            Arrays.asList(
                                "SELECT DISTINCT (COUNT(?x) AS ?x_count) WHERE { ?x a <http://dbpedia.org/ontology/Wrestler> }"
                            )
                  )
            );   
        }

        public void test_P_I() throws Exception{
          // who was the creator Next Top Model Romania  
         //SELECT DISTINCT ?x WHERE {  ?x <rdf:type>  <Class:Noun> . }
            assertEquals(
                   checkSequenceByStrings(     //We have more than 10.000 creators, and the one for this seems to be further
                        "who was the\n" +      //But this depends on the EP (does not returns always the same data subset)
                        "creator\n" +
                        "of\n" +
                        "Zorro\n" +
                        "q\n",
                        USECASE.DBPEDIA,  Language.EN),
                   new HashSet<>(
                            Arrays.asList(
                                "SELECT DISTINCT ?x WHERE { <http://dbpedia.org/resource/Zorro> <http://dbpedia.org/ontology/creator> ?x }"
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
                        USECASE.DBPEDIA, Language.EN),
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
                    checkSequenceByStrings(     //Shows, besides the good one, several generic SPARQl queries
                            "show me all\n" +
                            "conferences\n" +
                            "that\n"        +
                            "took place\n"  +
                            "in\n"          +
                            "Spain\n"       +
                            "in\n"          + 
                            "2009\n"        +
                            "q\n"),
                    new HashSet<>(
                        Arrays.asList(
                             "SELECT DISTINCT ?x WHERE { "
                           + "?x a <http://lod.springer.com/data/ontology/class/Conference> ; "
                                + "<http://lod.springer.com/data/ontology/property/confCountry> \"Spain\"@en ; "
                                + "<http://lod.springer.com/data/ontology/property/confYear> \"2009-01-01+01:00\"^^<http://www.w3.org/2001/XMLSchema#gYear> }"                        )
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
                           Arrays.asList( 
                               "SELECT DISTINCT ?x WHERE {"+
                                   " ?x a <http://lod.springer.com/data/ontology/class/Conference> ;"+
                                   " <http://lod.springer.com/data/ontology/property/confCity> \"Berlin\"@en "+
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
                          "International Semantic Web Conference\n" +
                          "2009\n" +
                          "q\n"),
                     new HashSet<>(
                            Arrays.asList( //We could have several queries here separated by comma
                              "SELECT DISTINCT ?y WHERE {"+
                              "?x <http://lod.springer.com/data/ontology/property/confStartDate> ?y."+
                              "{ ?x  <http://lod.springer.com/data/ontology/property/confAcronym>  ?l1 . } "+
                              "UNION "+
                              "{ ?x <http://lod.springer.com/data/ontology/property/confName> ?l1 . } "+
                              "FILTER regex(?l1,\"International Semantic Web Conference\"). "+
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
                          "LTEC\n"        +     //Available options: conference or conferences, not individuals
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
                          "Aarhus\n" +       //The list of options does not have individuals only things like town, location, etc...
                          "q\n",
                          USECASE.DBPEDIA, Language.EN),
                    new HashSet<>(
                            Arrays.asList(  
                              "SELECT DISTINCT ?x ?y WHERE { <http://dbpedia.org/resource/Aarhus> <http://dbpedia.org/ontology/populationUrban> ?y ; <http://dbpedia.org/ontology/areaUrban> ?x }"
                            )
                    )
             );
        }
        
        public void test_P_P_C() throws Exception {
            assertEquals(
                    checkSequenceByStrings(   
                          "give me the\n" +
                          "area\n" +  
                          "and\n" +  
                          "population\n" +
                          "of\n" +  
                          "cities\n" +       
                          "q\n",
                          USECASE.DBPEDIA, Language.EN),
                    new HashSet<>(
                            Arrays.asList(
                                    "SELECT DISTINCT ?x ?y WHERE { ?i <http://dbpedia.org/ontology/areaMetro> ?x ; <http://dbpedia.org/ontology/populationUrban> ?y ; a <http://dbpedia.org/ontology/City> }",
                                    "SELECT DISTINCT ?x ?y WHERE { ?i <http://dbpedia.org/ontology/areaMetro> ?x ; <http://dbpedia.org/ontology/populationMetro> ?y ; a <http://dbpedia.org/ontology/City> }",
                                    "SELECT DISTINCT ?x ?y WHERE { ?i <http://dbpedia.org/ontology/area> ?x ; <http://dbpedia.org/ontology/populationUrban> ?y ; a <http://dbpedia.org/ontology/City> }",
                                    "SELECT DISTINCT ?x ?y WHERE { ?i <http://dbpedia.org/ontology/areaTotal> ?x ; <http://dbpedia.org/ontology/populationUrban> ?y ; a <http://dbpedia.org/ontology/City> }",
                                    "SELECT DISTINCT ?x ?y WHERE { ?i <http://dbpedia.org/ontology/areaUrban> ?x ; <http://dbpedia.org/ontology/populationTotal> ?y ; a <http://dbpedia.org/ontology/City> }",
                                    "SELECT DISTINCT ?x ?y WHERE { ?i <http://dbpedia.org/ontology/areaTotal> ?x ; <http://dbpedia.org/ontology/populationTotal> ?y ; a <http://dbpedia.org/ontology/City> }",
                                    "SELECT DISTINCT ?x ?y WHERE { ?i <http://dbpedia.org/ontology/areaTotal> ?x ; <http://dbpedia.org/ontology/populationMetro> ?y ; a <http://dbpedia.org/ontology/City> }",
                                    "SELECT DISTINCT ?x ?y WHERE { ?i <http://dbpedia.org/ontology/area> ?x ; <http://dbpedia.org/ontology/populationMetro> ?y ; a <http://dbpedia.org/ontology/City> }",
                                    "SELECT DISTINCT ?x ?y WHERE { ?i <http://dbpedia.org/ontology/areaMetro> ?x ; <http://dbpedia.org/ontology/populationTotal> ?y ; a <http://dbpedia.org/ontology/City> }",
                                    "SELECT DISTINCT ?x ?y WHERE { ?i <http://dbpedia.org/ontology/areaUrban> ?x ; <http://dbpedia.org/ontology/populationMetro> ?y ; a <http://dbpedia.org/ontology/City> }",
                                    "SELECT DISTINCT ?x ?y WHERE { ?i <http://dbpedia.org/ontology/area> ?x ; <http://dbpedia.org/ontology/populationTotal> ?y ; a <http://dbpedia.org/ontology/City> }",
                                    "SELECT DISTINCT ?x ?y WHERE { ?i <http://dbpedia.org/ontology/areaUrban> ?x ; <http://dbpedia.org/ontology/populationUrban> ?y ; a <http://dbpedia.org/ontology/City> }"
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
                          "Zurich\n" +
                          "q\n"),
                    new HashSet<>(
                            Arrays.asList( //We could have several queries here separated by comma
                                "SELECT DISTINCT (COUNT(?x) AS ?x_count) WHERE {"+
                                " ?x a <http://lod.springer.com/data/ontology/class/Conference>"+
                                " ; <http://lod.springer.com/data/ontology/property/confCity> \"Zurich\"@en"+
                                " }"
                            )
                    )
            );
        }
}
