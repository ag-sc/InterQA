package interQA.patterns;

import interQA.elements.Element;
import interQA.elements.StringElement;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.LexicalEntry.Feature;
import interQA.lexicon.Lexicon;
import interQA.main.interQACLI.USECASE;
import interQA.patterns.springer.SpringerQueryPattern4;
import interQA.patterns.springer.SpringerQueryPattern5;
import interQA.patterns.templates.*;

import java.util.HashSet;
import java.util.Set;


public class QueryPatternFactory_ES implements QueryPatternFactory {

    USECASE usecase;
    Lexicon lexicon;
    DatasetConnector instances;


    public QueryPatternFactory_ES(USECASE u, Lexicon l, DatasetConnector is) {
    
        usecase = u;
        lexicon = l;
        instances = is;
    }

    
    @Override
    public Set<QueryPattern> rollout() {
        
        Set<QueryPattern> patterns = new HashSet<>();
        
        
//        // Give me all mountains.
          // Dame todas las montañas

        QueryPattern q1 = new C(lexicon,instances);
        
        addGiveMePrefixes((StringElement) q1.getElement(0));
        addNouns(q1.getElement(1));

        patterns.add(q1);
        
//        // Which movies are there?
          //¿Qué películas hay?
        
        QueryPattern q2 = new C(lexicon,instances);
        
        StringElement e2_0 = (StringElement) q2.getElement(0);
        addWhichPrefixes(e2_0);
        e2_0.add("¿Cuántas");
        
        addNouns(q2.getElement(1));
        
        StringElement e2_2 = (StringElement) q2.getElement(2);
        e2_2.add("¿Hay",Feature.SINGULAR);
        e2_2.add("¿Hay",Feature.PLURAL);

        patterns.add(q2);
        
        
//        // Give me the mayor of Paris. 
//        // Who is the president of Cameroon?
          //Dime el alcalde de Paris
          //¿Quién es el presidente de Camerún?
        
        QueryPattern q3 = new P_I(lexicon,instances);

        StringElement e3_0 = (StringElement) q3.getElement(0);
        addGiveMePrefixes(e3_0);
        addWhoWhatPrefixes(e3_0);

        addRelationalNouns(q3.getElement(1));
        
        patterns.add(q3);
        
        
//        // Who created Miffy? 
//        // Who died in Berlin?
          //¿Quién creó Miffy?
          //¿Quién murió en Berlín?
        
        QueryPattern q4 = new P_I(lexicon,instances);

        StringElement e4_0 = (StringElement) q4.getElement(0);
        e4_0.add("¿Quién");
        e4_0.add("¿Qué");

        addVerbs(q4.getElement(3));
        
        patterns.add(q4);
        
      
//        // What actors play in Batman?
          //¿Qué actores actúan en Batman?
        
        QueryPattern q5 = new C_P_I(lexicon,instances);

        StringElement e5_0 = (StringElement) q5.getElement(0);
        addWhichPrefixes(e5_0);
        
        addNouns(q5.getElement(1));
        
        addVerbs(q5.getElement(3));
        
        patterns.add(q5);
        
        
//        // Give me all actors that play in Batman.
          //Dime todos los actores que actúan en Batman
        
        QueryPattern q6 = new C_P_I(lexicon,instances);

        StringElement e6_0 = (StringElement) q6.getElement(0);
        addGiveMePrefixes(e6_0);

        addNouns(q6.getElement(1));
        
        StringElement e6_2 = (StringElement) q6.getElement(2);
        e6_2.add("que");
        //e6_2.add("which");
        //e6_2.add("who");
        
        addVerbs(q6.getElement(3));
        
        patterns.add(q6);
        
        
        // Which city is the capital of Denmark?
        //¿Qué ciudad es la capital de Dinamarca?
        
        QueryPattern q7 = new C_P_I(lexicon,instances);

        StringElement e7_0 = (StringElement) q7.getElement(0);
        addGiveMePrefixes(e7_0);
        addWhichPrefixes(e7_0);

        addNouns(q7.getElement(1));
        
        StringElement e7_2 = (StringElement) q7.getElement(2);
        e7_2.add("es una",Feature.SINGULAR,Feature.PRESENT);     //is a
        e7_2.add("es la",Feature.SINGULAR,Feature.PRESENT);   //is the
        e7_2.add("son las",Feature.PLURAL,Feature.PRESENT);  //are the
        e7_2.add("fue una",Feature.SINGULAR,Feature.PAST);       //was a
        e7_2.add("fue la",Feature.SINGULAR,Feature.PAST);     //was the
        e7_2.add("fueron las",Feature.PLURAL,Feature.PAST);    //were the
        
        addRelationalNouns(q7.getElement(3));
        
        patterns.add(q7);
        
        
        
//        //Show me all conferences that took place in Berlin in 2015.
          // Muéstrame todos los congresos que tuvieron lugar en Berlin en 2015
        QueryPattern q8 = new C_P_I_P_I(lexicon,instances);
        
        StringElement e8_0 = (StringElement) q8.getElement(0);
        addGiveMePrefixes(e8_0);
        
        addNouns(q8.getElement(1));
        
        StringElement e8_2 = (StringElement) q8.getElement(2);
        e8_2.add("que");
        
        addVerbs(q8.getElement(3));
        
        addPrepositionalVerbs(q8.getElement(5));
        
        patterns.add(q8);
        
       
//        //Which conferences took place in Berlin in 2015? //
          //¿Qué congresos tuvieron lugar en Berlín en 2015?
        
        QueryPattern q9 = new C_P_I_P_I(lexicon,instances);
        StringElement q9_0 = (StringElement) q9.getElement(0);
        addWhichPrefixes(q9_0);
        
        addNouns(q9.getElement(1));
        
        addVerbs(q9.getElement(3));
        
        addPrepositionalVerbs(q9.getElement(6));
        
        patterns.add(q9);
        
        
//        //Which movie star was born in Bielefeld ?
//        //Which conference took place in Berlin|2014? 
//        //How many conference took place in Berlin|2014?
        
        QueryPattern q10 = new C_P_I(lexicon,instances);
        
        StringElement e10_0 = (StringElement) q10.getElement(0);
        addWhichPrefixes(e10_0);
        e10_0.add("¿Cuántos");
        addNouns(q10.getElement(1));
        
        
        addVerbs(q10.getElement(3));
        
        patterns.add(q10);
        
        
//        //Which conference took place in Berlin|2004 in Berlin|2014 ?
//        //How many conference took place in Berlin|2004 in Berlin|2014 ?
//        //Which university was established in Bielefeld in 1969 ?
        
        QueryPattern q11 = new C_P_I_P_I(lexicon,instances);
        
        StringElement e11_0 = (StringElement) q11.getElement(0);
        addWhichPrefixes(e11_0);
        e11_0.add("¿Cuántos");
        addNouns(q11.getElement(1));
        
        addVerbs(q11.getElement(3));
        
        addVerbs(q11.getElement(5));
        
        patterns.add(q11);
        
        
        //Show me all conferences that took place in Berlin in 2015.
        QueryPattern q12 = new C_P_I_P_I(lexicon,instances);
        
        StringElement e12_0 = (StringElement) q12.getElement(0);
        addGiveMePrefixes(e12_0);
        
        addNouns(q12.getElement(1));
        
        StringElement e12_2 = (StringElement) q12.getElement(2);
        e12_2.add("que");
        
        addVerbs(q12.getElement(3));
        
        addPrepositionalVerbs(q12.getElement(5));
        
        patterns.add(q12);
        
      
        //What is the height and weight of Michael Jordan?
         //P_P_I

        //What is the height and weight of nba players_?
        
        QueryPattern q13 = new P_P_I(lexicon,instances);
        
        StringElement e13_0 = (StringElement) q13.getElement(0);
        addWhoWhatPrefixes(e13_0);
        
        addRelationalNouns(q13.getElement(1));
        
        StringElement e13_2 = (StringElement) q13.getElement(2);
        e13_2.add("y");
        
        addRelationalNouns(q13.getElement(3));
        
        patterns.add(q13);
        
        
//        //what are the BMW cars models and their prizes ?
        QueryPattern q14 = new C_P_P(lexicon,instances);
        
        StringElement e14_0 = (StringElement) q14.getElement(0);
        addWhoWhatPrefixes(e14_0);
        
        addNouns(q14.getElement(1));
        
        addRelationalNouns(q14.getElement(2));
        
        StringElement e14_3 = (StringElement) q14.getElement(3);
        e14_3.add("y");
        
        StringElement e14_4 = (StringElement) q14.getElement(4);
        //e14_4.add("its",Feature.SINGULAR,Feature.NEUTER); No aplica
        e14_4.add("su",Feature.SINGULAR,Feature.MASCULINE);  //his
        e14_4.add("su",Feature.SINGULAR,Feature.FEMININE);   //her
        e14_4.add("sus", Feature.PLURAL);                   //their
        
        addRelationalNouns(q14.getElement(5));
        
        patterns.add(q14);
        
          
        
        // USE-CASE SPECIFIC PATTERNS

        switch (usecase) {
        
            case SPRINGER: {
                
        //        //Give me the proceedings of ISWC 2015.
                QueryPattern q15 = new SpringerQueryPattern4(lexicon,instances);

                StringElement e15_0 = (StringElement) q15.getElement(0);
                addGiveMePrefixes(e15_0);

                addRelationalNouns(q15.getElement(1));

                patterns.add(q15);


//                   proceedings of ISWC 2015

               QueryPattern q17 = new SpringerQueryPattern4(lexicon,instances);

               addRelationalNouns(q17.getElement(1));

               patterns.add(q17);

//                conferences in Spain/2015

                QueryPattern q18 = new C_P_I(lexicon,instances);

                addNouns(q18.getElement(1));

                addPrepositionalVerbs(q18.getElement(3));

                patterns.add(q18);

//                Give me the start and end date of ISWC 2015.
                QueryPattern q16 = new SpringerQueryPattern5(lexicon,instances);

                StringElement e16_0 = (StringElement) q16.getElement(0);
                addGiveMePrefixes(e16_0);

                addRelationalNouns(q16.getElement(1));

                StringElement e16_2 = (StringElement) q16.getElement(2);
                e16_2.add("y");

                addRelationalNouns(q16.getElement(3));

                patterns.add(q16);
                
                break;
            }
        }
        
        
        // Done. (Yay!)
        return patterns;
    
    }
    
    
    /* Making the above relatively DRY... */
    
    private void addNouns(Element e) {
        
        e.addEntries(lexicon, LexicalEntry.POS.NOUN, null);
    }
    
    private void addRelationalNouns(Element e) {
        addRelationalNouns(e,true);
    }    
    private void addRelationalNouns(Element e, boolean withMarker) {
        
        e.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.NounPPFrame, withMarker);
        e.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.NounPossessiveFrame, withMarker);
    }
    
    private void addVerbs(Element e) {
        addVerbs(e,true);
    }
    private void addVerbs(Element e, boolean withMarker) {
        e.addEntries(lexicon, LexicalEntry.POS.VERB, vocab.TransitiveFrame, withMarker);
        e.addEntries(lexicon, LexicalEntry.POS.VERB, vocab.IntransitivePPFrame, withMarker);
    }
    
    private void addPrepositionalVerbs(Element e){
        e.addEntries(lexicon,LexicalEntry.POS.PREPOSITION,vocab.PrepositionalFrame);
    }
    
    private void addGiveMePrefixes(StringElement e) {

        e.add("dime");                        //give me
        e.add("dime todos",Feature.PLURAL);  //give me all
        e.add("dame los",Feature.PLURAL); //give me the
        e.add("muéstrame todos",Feature.PLURAL); //show me all
        //e.add("show me the");                    //show me the
           e.add("muéstrame la", Feature.SINGULAR, Feature.FEMININE);     //show me the
           e.add("muéstrame las", Feature.PLURAL, Feature.FEMININE);      //show me the
           e.add("muéstrame el", Feature.SINGULAR, Feature.MASCULINE);    //show me the
           e.add("muéstrame los", Feature.PLURAL, Feature.MASCULINE);     //show me the

        e.add("lista todos",Feature.PLURAL); //list all
        
        e.add("¿Conoces algún",Feature.PLURAL); //do you know any
        e.add("¿Hay algún",Feature.PLURAL);  //are there any
    }
    
    private void addWhichPrefixes(StringElement e) {
        
        e.add("¿qué"); //which
        //e.add("what");  //what
    }
    
    private void addWhoWhatPrefixes(StringElement e) {
        
        e.add("¿quién es el",Feature.SINGULAR,Feature.PRESENT);  //who is the
        e.add("¿quiénes son los",Feature.PLURAL,Feature.PRESENT);   //who are the
        e.add("¿quién fue el",Feature.SINGULAR,Feature.PAST);  //who was the
        e.add("¿quienes fueron",Feature.PLURAL,Feature.PAST);    //who were the
        
        e.add("¿qué es el",Feature.SINGULAR,Feature.PRESENT);  //what is the
        e.add("¿cuáles son",Feature.PLURAL,Feature.PRESENT);  //what are the
        e.add("¿qué fue el",Feature.SINGULAR,Feature.PAST);  //what was the
        e.add("¿cuáles furon los",Feature.PLURAL,Feature.PAST);   //what were the
    }
    
}
