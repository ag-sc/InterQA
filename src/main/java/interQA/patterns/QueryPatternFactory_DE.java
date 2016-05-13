package interQA.patterns;

import interQA.patterns.templates.*;
import interQA.elements.*;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.LexicalEntry.Feature;
import interQA.lexicon.Lexicon;
import interQA.main.interQACLI.USECASE;
import interQA.patterns.deprecate.SpringerQueryPattern4;
import interQA.patterns.deprecate.SpringerQueryPattern5;
import java.util.HashSet;
import java.util.Set;


public class QueryPatternFactory_DE implements QueryPatternFactory {    
    
    USECASE usecase;
    Lexicon lexicon;
    DatasetConnector instances;   
    
    
    public QueryPatternFactory_DE(USECASE u, Lexicon l, DatasetConnector is) {
    
        usecase = u;
        lexicon = l;
        instances = is;
    }

    
    @Override
    public Set<QueryPattern> rollout() {
        
        Set<QueryPattern> patterns = new HashSet<>();
        
        
        // Zeig mir alle Berge.

        QueryPattern q1 = new C(lexicon,instances);
        
        addGiveMePrefixes((StringElement) q1.getElement(0));
        addNouns(q1.getElement(1));

        patterns.add(q1);
        
        // Welche Filme gibt es?
        
        QueryPattern q2 = new C(lexicon,instances);
        
        StringElement e2_0 = (StringElement) q2.getElement(0);
        addWhichPrefixes(e2_0);
        addHowManyPrefixes(e2_0);
        
        addNouns(q2.getElement(1));
        
        StringElement e2_2 = (StringElement) q2.getElement(2);
        e2_2.add("gibt es");

        patterns.add(q2);
        
        
        // Gib mir den Bürgermeister von Paris. 
        // Wer ist der Bürgermeister von Paris?
        
        QueryPattern q3 = new P_I(lexicon,instances);

        StringElement e3_0 = (StringElement) q3.getElement(0);
        addGiveMePrefixes(e3_0);
        addWhoWhatPrefixes(e3_0);

        addRelationalNouns(q3.getElement(2));
 
        patterns.add(q3);
        
        
        // Wer entwickelte Minecraft? 
        // Wer starb in Berlin?
        
        QueryPattern q4 = new P_I(lexicon,instances);

        StringElement e4_0 = (StringElement) q4.getElement(0);
        e4_0.add("wer");
        e4_0.add("was");

        addVerbs(q4.getElement(3));
        
        patterns.add(q4);
        
        
        // Welche Schauspieler spielen in Batman mit?
        
        QueryPattern q5 = new C_P_I(lexicon,instances);

        StringElement e5_0 = (StringElement) q5.getElement(0);
        addWhichPrefixes(e5_0);
        
        addVerbs(q5.getElement(3));
        
        patterns.add(q5);
        
        
        // Zeige alle Schauspieler die in Batman mitspielen.
        
        QueryPattern q6 = new C_P_I(lexicon,instances);

        StringElement e6_0 = (StringElement) q6.getElement(0);
        addGiveMePrefixes(e6_0);

        addNouns(q6.getElement(1));
        
        StringElement e6_2 = (StringElement) q6.getElement(2);
        addRelativePronouns(e6_2);
        
        addVerbs(q6.getElement(3));
        
        patterns.add(q6);
        
        
        // Welche Stadt ist die Hauptstadt von Dänemark? 
        
        QueryPattern q7 = new C_P_I(lexicon,instances);

        StringElement e7_0 = (StringElement) q7.getElement(0);
        addGiveMePrefixes(e7_0);
        addWhichPrefixes(e7_0);

        addNouns(q7.getElement(1));
        
        StringElement e7_2 = (StringElement) q7.getElement(2);
        e7_2.add("ist eine",Feature.SINGULAR,Feature.PRESENT,Feature.FEMININE);
        e7_2.add("ist ein",Feature.SINGULAR,Feature.PRESENT,Feature.MASCULINE);
        e7_2.add("ist ein",Feature.SINGULAR,Feature.PRESENT,Feature.NEUTER);
        e7_2.add("ist die",Feature.SINGULAR,Feature.PRESENT,Feature.FEMININE);
        e7_2.add("ist der",Feature.SINGULAR,Feature.PRESENT,Feature.MASCULINE);
        e7_2.add("ist das",Feature.SINGULAR,Feature.PRESENT,Feature.NEUTER);
        e7_2.add("sind die",Feature.SINGULAR,Feature.PRESENT);
        e7_2.add("war eine",Feature.SINGULAR,Feature.PAST,Feature.FEMININE);
        e7_2.add("war ein",Feature.SINGULAR,Feature.PAST,Feature.MASCULINE);
        e7_2.add("war ein",Feature.SINGULAR,Feature.PAST,Feature.NEUTER);
        e7_2.add("war die",Feature.SINGULAR,Feature.PAST,Feature.FEMININE);
        e7_2.add("war der",Feature.SINGULAR,Feature.PAST,Feature.MASCULINE);
        e7_2.add("war das",Feature.SINGULAR,Feature.PAST,Feature.NEUTER);
        e7_2.add("waren die",Feature.SINGULAR,Feature.PAST);
        
        addRelationalNouns(q7.getElement(3));
        
        patterns.add(q7);
        
        
        
        // Ziege alle Konferenzen die 2015 in Berlin stattfanden. 
        QueryPattern q8 = new C_P_I_P_I(lexicon,instances);
        
        StringElement e8_0 = (StringElement) q8.getElement(0);
        addGiveMePrefixes(e8_0);
        
        addNouns(q8.getElement(1));
        
        StringElement e8_2 = (StringElement) q8.getElement(2);
        addRelativePronouns(e8_2);
        
        addVerbs(q8.getElement(3));
        
        addPrepositionalVerbs(q8.getElement(6)); // TODO "2015" is without preposition or verb or any other string!
        
        patterns.add(q8);
        
        
        // Welche Konferenzen fanden 105 in Berlin statt? 
        
        QueryPattern q9 = new C_P_I_P_I(lexicon,instances);
        StringElement q9_0 = (StringElement) q9.getElement(0);
        addWhichPrefixes(q9_0);
        
        addNouns(q9.getElement(1));
        
        addVerbs(q9.getElement(3));
        
        addPrepositionalVerbs(q9.getElement(6)); // TODO dito
        
        patterns.add(q9);
        
        
        // Welche Schriftsteller wurden in Bielefeld geboren? // TODO This probably works differently in German...
        // Welche Konferenz fand in Berlin statt? 
        // Wieviele Konferenzen fanden in Berlin statt?
        
        QueryPattern q10 = new C_P_I(lexicon,instances);
        
        StringElement e10_0 = (StringElement) q10.getElement(0);
        addWhichPrefixes(e10_0);
        addHowManyPrefixes(e10_0);
        addNouns(q10.getElement(1));
        
        addVerbs(q10.getElement(3));
        
        patterns.add(q10);
        
        
        // Which conference took place in Berlin|2004 in Berlin|2014 ?
        // How many conference took place in Berlin|2004 in Berlin|2014 ?
        // Which university was established in Bielefeld in 1969 ?
        
        QueryPattern q11 = new C_P_I_P_I(lexicon,instances);
        
        StringElement e11_0 = (StringElement) q11.getElement(0);
        addWhichPrefixes(e11_0);
        e11_0.add("how many");
        addNouns(q11.getElement(1));
        
        addVerbs(q11.getElement(3));
        
        addVerbs(q11.getElement(5));
        
        patterns.add(q11);
        
        
        // Zeige alle Konferenzen die 2015 in Berlin stattfanden.
        
        QueryPattern q12 = new C_P_I_P_I(lexicon,instances);
        
        StringElement e12_0 = (StringElement) q12.getElement(0);
        addGiveMePrefixes(e12_0);
        
        addNouns(q12.getElement(1));
        
        StringElement e12_2 = (StringElement) q12.getElement(2);
        addRelativePronouns(e12_2);
        
        addVerbs(q12.getElement(3));
        
        addPrepositionalVerbs(q12.getElement(5)); // TODO dito
        
        patterns.add(q12);
        
        
        // Was ist die Höhe und Breite von Autos? 
        
        QueryPattern q13 = new P_P_C(lexicon,instances);
        
        StringElement e13_0 = (StringElement) q13.getElement(0);
        addDefiniteGiveMePrefixes(e13_0);
        addWhoWhatPrefixes(e13_0);
        
        addRelationalNouns(q13.getElement(1),false);
        
        StringElement e13_2 = (StringElement) q13.getElement(2);
        e13_2.add("und");
        
        addRelationalNouns(q13.getElement(3));
        
        StringElement e13_4 = (StringElement) q13.getElement(4);
        
        addNouns(q13.getElement(5));
        
        patterns.add(q13);
        
        
        // Was sind BMW-Modelle und ihr Preis? 
        
        QueryPattern q14 = new C_P_P(lexicon,instances);
        
        StringElement e14_0 = (StringElement) q14.getElement(0);
        addDefiniteGiveMePrefixes(e14_0);
        addWhoWhatPrefixes(e14_0);
        
        addNouns(q14.getElement(1));
        
        addRelationalNouns(q14.getElement(2));
        
        StringElement e14_3 = (StringElement) q14.getElement(3);
        e14_3.add("und");
        
        StringElement e14_4 = (StringElement) q14.getElement(4);
        e14_4.add("ihre",Feature.SINGULAR,Feature.FEMININE);
        e14_4.add("ihr",Feature.SINGULAR,Feature.MASCULINE);
        e14_4.add("ihr",Feature.SINGULAR,Feature.NEUTER);
        e14_4.add("ihre", Feature.PLURAL);
        
        addRelationalNouns(q14.getElement(5));
        
        patterns.add(q14);

       
        
        // KEYWORD-LIKE PATTERNS
        
        // Konferenzen in Spanien
        
        QueryPattern q18 = new C_P_I(lexicon,instances);
        
        addNouns(q18.getElement(1));
        
        addPrepositionalVerbs(q18.getElement(3));
        
        patterns.add(q18);
        
        
          
        
        // USE-CASE SPECIFIC PATTERNS
        
        switch (usecase) {
        
            case SPRINGER: {
        
                //Give me the proceedings of ISWC 2015.
                QueryPattern q15 = new SpringerQueryPattern4(lexicon,instances);

                StringElement e15_0 = (StringElement) q15.getElement(0);
                addGiveMePrefixes(e15_0);

                addRelationalNouns(q15.getElement(1));

                patterns.add(q15);

                // proceedings of ISWC 2015

                QueryPattern q17 = new SpringerQueryPattern4(lexicon,instances);

                addRelationalNouns(q17.getElement(1));

                patterns.add(q17);       

                //Give me the start and end date of ISWC 2015.
                QueryPattern q16 = new SpringerQueryPattern5(lexicon,instances);

                StringElement e16_0 = (StringElement) q16.getElement(0);
                addGiveMePrefixes(e16_0);

                addRelationalNouns(q16.getElement(1));

                StringElement e16_2 = (StringElement) q16.getElement(2);
                e16_2.add("und");

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

        e.add("gib mir");
        e.add("gib mir alle",Feature.PLURAL);
        e.add("gib mir die",Feature.PLURAL);
        e.add("gib mir die",Feature.SINGULAR,Feature.FEMININE);
        e.add("gib mir das",Feature.SINGULAR,Feature.NEUTER);
        e.add("gib mir den",Feature.SINGULAR,Feature.MASCULINE);
        e.add("zeige alle",Feature.PLURAL);
        e.add("zeig mir");
        
        e.add("gibt es",Feature.PLURAL);
    }
    
    private void addDefiniteGiveMePrefixes(StringElement e) {

        e.add("gib mir die",Feature.PLURAL);
        e.add("gib mir die",Feature.SINGULAR,Feature.FEMININE);
        e.add("gib mir das",Feature.SINGULAR,Feature.NEUTER);
        e.add("gib mir den",Feature.SINGULAR,Feature.MASCULINE);
    }
    
    private void addWhichPrefixes(StringElement e) {
        
        e.add("welche",Feature.PLURAL);
        e.add("welche",Feature.SINGULAR,Feature.FEMININE);
        e.add("welcher",Feature.SINGULAR,Feature.MASCULINE);
        e.add("welches",Feature.SINGULAR,Feature.NEUTER);
    }
    
    private void addHowManyPrefixes(StringElement e) {
        
        e.add("wieviel",Feature.PLURAL);
        e.add("wieviele",Feature.PLURAL);
    }
    
    private void addWhoWhatPrefixes(StringElement e) {
        
        e.add("wer ist der",Feature.SINGULAR,Feature.PRESENT,Feature.MASCULINE);
        e.add("wer ist die",Feature.PLURAL,Feature.PRESENT,Feature.FEMININE);
        e.add("wer war der",Feature.SINGULAR,Feature.PAST,Feature.MASCULINE);
        e.add("wer war die",Feature.PLURAL,Feature.PAST,Feature.FEMININE);
        e.add("wer waren die",Feature.PLURAL,Feature.PAST);
        
        e.add("was ist der",Feature.SINGULAR,Feature.PRESENT,Feature.MASCULINE);
        e.add("was ist die",Feature.PLURAL,Feature.PRESENT,Feature.FEMININE);
        e.add("was ist das",Feature.PLURAL,Feature.PRESENT,Feature.NEUTER);
        e.add("was war der",Feature.SINGULAR,Feature.PAST,Feature.MASCULINE);
        e.add("was war die",Feature.PLURAL,Feature.PAST,Feature.FEMININE);
        e.add("was war das",Feature.PLURAL,Feature.PAST,Feature.NEUTER);
        e.add("was waren die",Feature.PLURAL,Feature.PAST);
    }
    
    private void addRelativePronouns(StringElement e) {
        
        e.add("die",Feature.PLURAL);
        e.add("die",Feature.FEMININE);
        e.add("der",Feature.MASCULINE);
        e.add("das",Feature.NEUTER);
    }
}
