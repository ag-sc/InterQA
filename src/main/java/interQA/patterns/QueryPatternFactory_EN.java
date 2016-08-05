package interQA.patterns;

import interQA.patterns.templates.*;
import interQA.elements.*;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.LexicalEntry.Feature;
import interQA.lexicon.Lexicon;
import interQA.main.interQACLI.USECASE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class QueryPatternFactory_EN implements QueryPatternFactory {    
    
    USECASE usecase;
    Lexicon lexicon;
    DatasetConnector instances;   
    
    
    public QueryPatternFactory_EN(USECASE u, Lexicon l, DatasetConnector is) {
    
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
        plist.add("qpC_P_I1");
        plist.add("qpC_P_I2");
        plist.add("qpC_P_I3");
        plist.add("qpC_P_I_P_I1");
        plist.add("qpC_P_I_P_I2");
        plist.add("qpC_P_I4");
        plist.add("qpP_P_I");
        plist.add("qpP_P_C");
        plist.add("qpC_P_P");
        //Please, add P_C implementation
        switch(usecase) {
            case SPRINGER:
                plist.add("qpSpringer1");
                plist.add("qpSpringer2");
                plist.add("qpSpringerC_P_I");
                plist.add("qpSpringer3");
                break;
        }
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
        
        
//        // Give me the mayor of Paris. 
//        // Who is the president of Cameroon?
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
        
//        // Who created Miffy? 
//        // Who died in Berlin?
        if (plist.contains("qpP_I2")) {
            QueryPattern qpP_I2 = new P_I(lexicon, instances);

            StringElement e4_0 = (StringElement) qpP_I2.getElement(0);
            e4_0.add("who");
            e4_0.add("what");

            addVerbs(qpP_I2.getElement(3));

            qpP_I2.addAgreementDependency(0,1);

            patterns.add(qpP_I2);
        }
      
//        // What actors play in Batman?
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
        
//        // Give me all actors that play in Batman.
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
        
////        //what are the BMW cars models and their prizes ?
//        if (plist.contains("qpC_P_P")) {
//            QueryPattern qp = new C_P_P(lexicon, instances);
//
//            StringElement e14_0 = (StringElement) qp.getElement(0);
//            addWhoWhatPrefixes(e14_0);
//            addDefGiveMePrefixes(e14_0);
//
//            addNouns(qp.getElement(1));
//
//            addRelationalNouns(qp.getElement(2));
//
//            StringElement e14_3 = (StringElement) qp.getElement(3);
//            e14_3.add("and");
//
//            StringElement e14_4 = (StringElement) qp.getElement(4);
//            e14_4.add("its", Feature.SINGULAR, Feature.NEUTER);
//            e14_4.add("his", Feature.SINGULAR, Feature.MASCULINE);
//            e14_4.add("her", Feature.SINGULAR, Feature.FEMININE);
//            e14_4.add("their", Feature.PLURAL);
//
//            addRelationalNouns(qp.getElement(5));
//
//            patterns.add(qp);
//        }
          
        
//        // USE-CASE SPECIFIC PATTERNS
//
//        switch (usecase) {
//        
//            case SPRINGER: {
//                
//        //        //Give me the proceedings of ISWC 2015.
//                if (plist.contains("qpSpringer1")) {
//                    QueryPattern qp = new SpringerQueryPattern4(lexicon, instances);
//
//                    StringElement e15_0 = (StringElement) qp.getElement(0);
//                    addDefGiveMePrefixes(e15_0);
//
//                    addRelationalNouns(qp.getElement(1));
//
//                    patterns.add(qp);
//                }
//
////                   proceedings of ISWC 2015
//               if (plist.contains("qpSpringer2")) {
//                   QueryPattern qp = new SpringerQueryPattern4(lexicon, instances);
//
//                   addRelationalNouns(qp.getElement(1));
//
//                   patterns.add(qp);
//               }
//
////                conferences in Spain/2015
//                if (plist.contains("qpSpringerC_P_I")) {
//                    QueryPattern qp = new C_P_I(lexicon, instances);
//
//                    addNouns(qp.getElement(1));
//
//                    addPrepositions(qp.getElement(3));
//
//                    patterns.add(qp);
//                }
//
////                Give me the start and end date of ISWC 2015.
//                if (plist.contains("qpSpringer3")) {
//                    QueryPattern qp = new SpringerQueryPattern5(lexicon, instances);
//
//                    StringElement e16_0 = (StringElement) qp.getElement(0);
//                    addDefGiveMePrefixes(e16_0);
//
//                    addRelationalNouns(qp.getElement(1));
//
//                    StringElement e16_2 = (StringElement) qp.getElement(2);
//                    e16_2.add("and");
//
//                    addRelationalNouns(qp.getElement(3));
//
//                    patterns.add(qp);
//                }
//                
//                break;
//            }
//        } //End switch(usecase)
        
        
        // Done. (Yay!)
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
    
    private void addVerbs(Element e) {
        addVerbs(e,false);
    }
    private void addVerbs(Element e, boolean withMarker) {
        e.addEntries(lexicon, LexicalEntry.POS.VERB, vocab.TransitiveFrame, withMarker);
        e.addEntries(lexicon, LexicalEntry.POS.VERB, vocab.IntransitivePPFrame, withMarker);
    }
    
    private void addPrepositions(Element e) {
        e.addEntries(lexicon,LexicalEntry.POS.PREPOSITION, vocab.PrepositionalFrame, false);
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
        
        e.add("do you know any",Feature.PLURAL);
        e.add("are there any",Feature.PLURAL);
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
