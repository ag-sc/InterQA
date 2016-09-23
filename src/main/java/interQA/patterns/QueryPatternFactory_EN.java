package interQA.patterns;

import interQA.patterns.templates.*;
import interQA.elements.*;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.LexicalEntry.Feature;
import interQA.lexicon.Lexicon;
import interQA.Config.Usecase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class QueryPatternFactory_EN implements QueryPatternFactory {    
    
    Usecase usecase;
    Lexicon lexicon;
    DatasetConnector instances;   
    
    
    public QueryPatternFactory_EN(Usecase u, Lexicon l, DatasetConnector is) {
    
        usecase = u;
        lexicon = l;
        instances = is;
    }

    @Override
    public Set<QueryPattern> rollout() {
        
        ArrayList<String> plist = new ArrayList<>();
        
        plist.add("qpC1");
        plist.add("qpC2");
        plist.add("qpP_I1");
        plist.add("qpP_I2");
        plist.add("qpP_I3");
        plist.add("qpC_P_I1");
        plist.add("qpC_P_I2");
        plist.add("qpC_P_I3");
        plist.add("qpC_P_I4");
        plist.add("qpC_P_I5");
        plist.add("qpC_P_I6");
        plist.add("qpC_I_P1");
        plist.add("qpC_I_P2");
        plist.add("qpP_C_P_I1");
        plist.add("qpP_C_P_I2");
        plist.add("qpC_P_I_P_I1");
        plist.add("qpC_P_I_P_I2");
        plist.add("qpP_P_I");
        plist.add("qpP_P_C");
        //Please, add P_C implementation

        return(rollout(plist));
    }



    public Set<QueryPattern> rollout(ArrayList<String> plist) {
        
        Set<QueryPattern> patterns = new HashSet<>();
        
        // Give me all mountains.
        if (plist.contains("qpC1")) {
            QueryPattern qpC1 = new C(lexicon,instances);

            addDefGiveMePrefixes((StringElement) qpC1.getElement(0));
            addIndefGiveMePrefixes((StringElement) qpC1.getElement(0));
            addNouns(qpC1.getElement(1));

            qpC1.addAgreementDependency(0,1);
            
            patterns.add(qpC1);
        }
        
        // Which movies are there?
        if (plist.contains("qpC2")) {
            QueryPattern qpC2 = new C(lexicon,instances);

            StringElement e2_0 = (StringElement) qpC2.getElement(0);
            addWhichPrefixes(e2_0);

            addNouns(qpC2.getElement(1));

            StringElement e2_2 = (StringElement) qpC2.getElement(2);
            e2_2.add("are there", Feature.PLURAL);

            qpC2.addAgreementDependency(0,1);
            qpC2.addAgreementDependency(1,2);
            
            patterns.add(qpC2);
        }
        
        // Give me the mayor of Paris. 
        // Who is the president of Cameroon?
        if (plist.contains("qpP_I1")) {
            QueryPattern qpP_I1 = new P_I(lexicon,instances);

            StringElement e3_0 = (StringElement) qpP_I1.getElement(0);
            addDefGiveMePrefixes(e3_0);
            addIndefGiveMePrefixes(e3_0);
            addWhoWhatPrefixes(e3_0);

            addRelationalNouns(qpP_I1.getElement(1));

            qpP_I1.addAgreementDependency(0,1);
            
            patterns.add(qpP_I1);
        }
        
        // Who created Miffy? 
        // Who died in Berlin?
        if (plist.contains("qpP_I2")) {
            QueryPattern qpP_I2 = new P_I(lexicon, instances);

            StringElement e4_0 = (StringElement) qpP_I2.getElement(0);
            e4_0.add("who");
            e4_0.add("what");

            addVerbs(qpP_I2.getElement(1));

            qpP_I2.addAgreementDependency(0,1);

            patterns.add(qpP_I2);
        }
        
        // What was created by Walt Disney?
        if (plist.contains("qpP_I3")) {
            QueryPattern qpP_I3 = new P_I(lexicon, instances);

            StringElement e15_0 = (StringElement) qpP_I3.getElement(0);
            e15_0.add("who is", Feature.PRESENT, Feature.SINGULAR);
            e15_0.add("who was", Feature.PAST, Feature.SINGULAR);
            e15_0.add("what is", Feature.PRESENT, Feature.SINGULAR);
            e15_0.add("what was", Feature.PAST, Feature.SINGULAR);

            addParticiples(qpP_I3.getElement(1));

            patterns.add(qpP_I3);
        }
      
        // What actors play in Batman?
        if (plist.contains("qpC_P_I1")) {
            QueryPattern qpC_P_I1 = new C_P_I(lexicon, instances);

            StringElement e5_0 = (StringElement) qpC_P_I1.getElement(0);
            addWhichPrefixes(e5_0);

            addNouns(qpC_P_I1.getElement(1));
            addVerbs(qpC_P_I1.getElement(3));

            qpC_P_I1.addAgreementDependency(0,1);
            qpC_P_I1.addAgreementDependency(1,2);
            qpC_P_I1.addAgreementDependency(1,3);

            patterns.add(qpC_P_I1);
        }
            
        // Give me all actors that play in Batman.
        if (plist.contains("qpC_P_I2")) {
            QueryPattern qpC_P_I2 = new C_P_I(lexicon, instances);

            StringElement e6_0 = (StringElement) qpC_P_I2.getElement(0);
            addDefGiveMePrefixes(e6_0);
            addIndefGiveMePrefixes(e6_0);

            addNouns(qpC_P_I2.getElement(1));

            StringElement e6_2 = (StringElement) qpC_P_I2.getElement(2);
            e6_2.add("that");
            e6_2.add("which");
            e6_2.add("who");

            addVerbs(qpC_P_I2.getElement(3));

            qpC_P_I2.addAgreementDependency(0,1);
            qpC_P_I2.addAgreementDependency(1,2);
            qpC_P_I2.addAgreementDependency(1,3);

            patterns.add(qpC_P_I2);
        }
        
        // Which city is the capital of Denmark? 
        if (plist.contains("qpC_P_I3")) {
            QueryPattern qpC_P_I3 = new C_P_I(lexicon, instances);

            StringElement e7_0 = (StringElement) qpC_P_I3.getElement(0);
            addWhichPrefixes(e7_0);

            addNouns(qpC_P_I3.getElement(1));

            StringElement e7_2 = (StringElement) qpC_P_I3.getElement(2);
            e7_2.add("is a", Feature.SINGULAR, Feature.PRESENT);
            e7_2.add("is the", Feature.SINGULAR, Feature.PRESENT);
            e7_2.add("are the", Feature.PLURAL, Feature.PRESENT);
            e7_2.add("was a", Feature.SINGULAR, Feature.PAST);
            e7_2.add("was the", Feature.SINGULAR, Feature.PAST);
            e7_2.add("were the", Feature.SINGULAR, Feature.PAST);

            addRelationalNouns(qpC_P_I3.getElement(3));

            qpC_P_I3.addAgreementDependency(0,1);
            qpC_P_I3.addAgreementDependency(1,2);
            qpC_P_I3.addAgreementDependency(2,3);

            patterns.add(qpC_P_I3);
        }
        
        // Give me all movies directed by Steven Spielberg.
        if (plist.contains("qpC_P_I4")) {
            QueryPattern qpC_P_I4 = new C_P_I(lexicon, instances);

            StringElement e8_0 = (StringElement) qpC_P_I4.getElement(0);
            addDefGiveMePrefixes(e8_0);
            addIndefGiveMePrefixes(e8_0);

            addNouns(qpC_P_I4.getElement(1));
            addParticiples(qpC_P_I4.getElement(3));
            addPrepositions(qpC_P_I4.getElement(3));

            qpC_P_I4.addAgreementDependency(0,1);

            patterns.add(qpC_P_I4);
        }
        
        // Give me all movies that were directed by Steven Spielberg.
        if (plist.contains("qpC_P_I5")) {
            QueryPattern qpC_P_I5 = new C_P_I(lexicon, instances);

            StringElement e14_0 = (StringElement) qpC_P_I5.getElement(0);
            addDefGiveMePrefixes(e14_0);
            addIndefGiveMePrefixes(e14_0);

            addNouns(qpC_P_I5.getElement(1));
            
            StringElement e14_2 = (StringElement) qpC_P_I5.getElement(2);
            e14_2.add("that is", Feature.PRESENT, Feature.SINGULAR);
            e14_2.add("that was", Feature.PAST, Feature.SINGULAR);
            e14_2.add("that are", Feature.PRESENT, Feature.PLURAL);
            e14_2.add("that were", Feature.PAST, Feature.PLURAL);
            e14_2.add("which is", Feature.PRESENT, Feature.SINGULAR);
            e14_2.add("which was", Feature.PAST, Feature.SINGULAR);
            e14_2.add("which are", Feature.PRESENT, Feature.PLURAL);
            e14_2.add("which were", Feature.PAST, Feature.PLURAL);     
            e14_2.add("who is", Feature.PRESENT, Feature.SINGULAR);
            e14_2.add("who was", Feature.PAST, Feature.SINGULAR);
            e14_2.add("who are", Feature.PRESENT, Feature.PLURAL);
            e14_2.add("who were", Feature.PAST, Feature.PLURAL);
            
            addParticiples(qpC_P_I5.getElement(3));

            qpC_P_I5.addAgreementDependency(0,1);
            qpC_P_I5.addAgreementDependency(1,2);

            patterns.add(qpC_P_I5);
        }
        
        // Which languages are spoken in Switzerland? 
        if (plist.contains("qpC_P_I6")) {
            QueryPattern qpC_P_I6 = new C_P_I(lexicon, instances);

            addWhichPrefixes((StringElement) qpC_P_I6.getElement(0));

            addNouns(qpC_P_I6.getElement(1));

            StringElement cpi6_2 = (StringElement) qpC_P_I6.getElement(2);
            cpi6_2.add("is", Feature.SINGULAR, Feature.PRESENT);
            cpi6_2.add("are", Feature.PLURAL, Feature.PRESENT);
            cpi6_2.add("was", Feature.SINGULAR, Feature.PAST);
            cpi6_2.add("were", Feature.PLURAL, Feature.PAST);

            addRelationalAdjectives(qpC_P_I6.getElement(3));

            qpC_P_I6.addAgreementDependency(0,1);
            qpC_P_I6.addAgreementDependency(1,2);

            patterns.add(qpC_P_I6);
        }
 
        // Which countries have the Euro as currency?
        if (plist.contains("qpC_I_P1")) {
            QueryPattern qpC_I_P1 = new C_I_P(lexicon, instances);

            StringElement e10_0 = (StringElement) qpC_I_P1.getElement(0);
            addWhichPrefixes(e10_0);
            
            addNouns(qpC_I_P1.getElement(1));
            
            StringElement e10_2 = (StringElement) qpC_I_P1.getElement(2);
            e10_2.add("has", Feature.PRESENT, Feature.SINGULAR);
            e10_2.add("have", Feature.PRESENT, Feature.PLURAL);
            e10_2.add("had", Feature.PAST);
            
            StringElement e10_4 = (StringElement) qpC_I_P1.getElement(4);
            e10_4.add("as");

            addRelationalNouns(qpC_I_P1.getElement(5));

            qpC_I_P1.getElement(5).addAgrFeature(Feature.SINGULAR);
            qpC_I_P1.getElement(5).dontuseMarkers();      

            qpC_I_P1.addAgreementDependency(0,1);
            qpC_I_P1.addAgreementDependency(1,2);
            qpC_I_P1.addAgreementDependency(2,3);

            patterns.add(qpC_I_P1);
        }
        
        // Which countries does the Rhine flow through?
        // Which movies did Kurosawa direct?
        if (plist.contains("qpC_I_P2")) {
            QueryPattern qpC_I_P2 = new C_I_P(lexicon, instances);

            addWhichPrefixes((StringElement) qpC_I_P2.getElement(0));
            
            addNouns(qpC_I_P2.getElement(1));
            
            ((StringElement) qpC_I_P2.getElement(2)).add("does", Feature.PRESENT, Feature.SINGULAR);
            ((StringElement) qpC_I_P2.getElement(2)).add("do", Feature.PRESENT, Feature.PLURAL);
            ((StringElement) qpC_I_P2.getElement(2)).add("did", Feature.PAST);

            addVerbs(qpC_I_P2.getElement(5));
            
            qpC_I_P2.getElement(5).addAgrFeature(Feature.PLURAL);
            qpC_I_P2.getElement(5).addAgrFeature(Feature.PRESENT);

            qpC_I_P2.addAgreementDependency(0,1);

            patterns.add(qpC_I_P2);
        }
        
        // Give me the cinematographers of movies directed by Truffaut.
        if (plist.contains("qpP_C_P_I1")) {
            QueryPattern qpP_C_P_I1 = new P_C_P_I(lexicon, instances);

            StringElement e11_0 = (StringElement) qpP_C_P_I1.getElement(0);
            addDefGiveMePrefixes(e11_0);
            addIndefGiveMePrefixes(e11_0);

            addRelationalNouns(qpP_C_P_I1.getElement(1));
            addNouns(qpP_C_P_I1.getElement(3));
            addPrepositions(qpP_C_P_I1.getElement(5));
            addParticiples(qpP_C_P_I1.getElement(5));
            
            qpP_C_P_I1.addAgreementDependency(0,1);

            patterns.add(qpP_C_P_I1);
        }
        
        // Who appeared in movies by Truffaut?
        if (plist.contains("qpP_C_P_I2")) {
            QueryPattern qpP_C_P_I2 = new P_C_P_I(lexicon, instances);

            StringElement e12_0 = (StringElement) qpP_C_P_I2.getElement(0);
            e12_0.add("who");
            e12_0.add("what");

            addVerbs(qpP_C_P_I2.getElement(1));
            addNouns(qpP_C_P_I2.getElement(3));
            addPrepositions(qpP_C_P_I2.getElement(5));
            addParticiples(qpP_C_P_I2.getElement(5));
            
            qpP_C_P_I2.addAgreementDependency(0,1);

            patterns.add(qpP_C_P_I2);
        }
        
        // Which actors appeared in movies by Truffaut?
        // TODO C_P_C_P_I
        
//        //Show me all conferences that took place in Berlin in 2015.
        if (plist.contains("qpC_P_I_P_I1")) {
            QueryPattern qp = new C_P_I_P_I(lexicon, instances);

            StringElement e8_0 = (StringElement) qp.getElement(0);
            addDefGiveMePrefixes(e8_0);
            addIndefGiveMePrefixes(e8_0);

            addNouns(qp.getElement(1));

            StringElement e8_2 = (StringElement) qp.getElement(2);
            e8_2.add("that");

            addVerbs(qp.getElement(3));
            addPrepositions(qp.getElement(7));

            qp.addAgreementDependency(0,1);
            qp.addAgreementDependency(1,2);
            qp.addAgreementDependency(1,3);

            patterns.add(qp);
        }
       
//        //Which conferences took place in Berlin in 2015? // 
        if (plist.contains("qpC_P_I_P_I2")) {
            QueryPattern qp = new C_P_I_P_I(lexicon, instances);
            StringElement q9_0 = (StringElement) qp.getElement(0);
            addWhichPrefixes(q9_0);         

            addNouns(qp.getElement(1));
            addVerbs(qp.getElement(3));
            addPrepositions(qp.getElement(7));

            qp.addAgreementDependency(0,1);
            qp.addAgreementDependency(1,2);
            qp.addAgreementDependency(1,3);
            
            patterns.add(qp);
        }
      
        //What is the height and weight of Michael Jordan?

        if (plist.contains("qpP_P_I")) {
            QueryPattern qp = new P_P_I(lexicon, instances);

            StringElement e13_0 = (StringElement) qp.getElement(0);
            addWhoWhatPrefixes(e13_0);
            addDefGiveMePrefixes(e13_0);

            addRelationalNouns(qp.getElement(1));

            StringElement e13_2 = (StringElement) qp.getElement(2);
            e13_2.add("and");

            addRelationalNouns(qp.getElement(3));

            qp.addAgreementDependency(0,1);
            qp.addAgreementDependency(0,3);
            
            patterns.add(qp);
        }
        
        //What is the height and weight of NBA players?

        if (plist.contains("qpP_P_C")) {
            QueryPattern qp = new P_P_C(lexicon, instances);

            StringElement e17_0 = (StringElement) qp.getElement(0);
            addWhoWhatPrefixes(e17_0);
            addDefGiveMePrefixes(e17_0);

            addRelationalNouns(qp.getElement(1));

            StringElement e17_2 = (StringElement) qp.getElement(2);
            e17_2.add("and");

            addRelationalNouns(qp.getElement(3));
            addNouns(qp.getElement(5));

            qp.addAgreementDependency(0,1);
            qp.addAgreementDependency(0,3);
            
            patterns.add(qp);
        }
        
//        System.out.println("Loaded patterns:"); 
//        for (QueryPattern p : patterns) System.out.println("* " + p.getClass()); 
        
        return patterns;
    
    }
    
    
    /* Making the above relatively DRY... */
    
    private void addNouns(Element e) {
        
        e.addEntries(lexicon, LexicalEntry.POS.NOUN, null);
    }
    
    private void addRelationalNouns(Element e) {
        addRelationalNouns(e,false);
    }    
    private void addRelationalNouns(Element e, boolean withMarker) {
        e.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.NounPPFrame, withMarker);
        e.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.NounPossessiveFrame, withMarker);
    }

    private void addRelationalAdjectives(Element e) {
        addRelationalAdjectives(e,false);
    }    
    private void addRelationalAdjectives(Element e, boolean withMarker) {        
        e.addEntries(lexicon, LexicalEntry.POS.ADJECTIVE, vocab.AdjectivePPFrame, withMarker);
    }
    
    private void addVerbs(Element e) {
        addVerbs(e,false);
    }
    private void addVerbs(Element e, boolean withMarker) {
        e.addEntries(lexicon, LexicalEntry.POS.VERB, vocab.TransitiveFrame, withMarker);
        e.addEntries(lexicon, LexicalEntry.POS.VERB, vocab.IntransitivePPFrame, withMarker);
    }
    
    private void addPrepositions(Element e) {
        e.addEntries(lexicon,LexicalEntry.POS.PREPOSITION, vocab.PrepositionalPhraseFrame, false);
    }
    
    private void addParticiples(Element e) {
        e.addEntries(lexicon, LexicalEntry.POS.PARTICIPLE, null);
    }
    
    static public void addDefGiveMePrefixes(StringElement e) {

        e.add("give me the");
        e.add("show me the");
    }
    
    static public void addIndefGiveMePrefixes(StringElement e) {

        e.add("give me");
        e.add("give me all",Feature.PLURAL);
        e.add("show me all",Feature.PLURAL);
        e.add("list all",Feature.PLURAL);
    }
    
    private void addWhichPrefixes(StringElement e) {
        
        e.add("which");
        e.add("what");
        e.add("how many",Feature.PLURAL);
    }
    
    private void addWhoWhatPrefixes(StringElement e) {
        
        e.add("who is the",Feature.SINGULAR,Feature.PRESENT);
        e.add("who are the",Feature.PLURAL,Feature.PRESENT);
        e.add("who was the",Feature.SINGULAR,Feature.PAST);
        e.add("who were the",Feature.PLURAL,Feature.PAST);
        
        e.add("what is the",Feature.SINGULAR,Feature.PRESENT);
        e.add("what are the",Feature.PLURAL,Feature.PRESENT);
        e.add("what was the",Feature.SINGULAR,Feature.PAST);
        e.add("what were the",Feature.PLURAL,Feature.PAST);
    }
    
}
