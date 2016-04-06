package interQA.patterns;

import interQA.patterns.templates.*;
import interQA.elements.*;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.LexicalEntry.Feature;
import interQA.lexicon.Lexicon;
import java.util.HashSet;
import java.util.Set;


public class QueryPatternFactory_EN implements QueryPatternFactory {    
    
    Lexicon lexicon;
    DatasetConnector instances;   
    
    
    public QueryPatternFactory_EN(Lexicon l, DatasetConnector is) {
    
        lexicon = l;
        instances = is;
    }

    
    @Override
    public Set<QueryPattern> rollout() {
        
        Set<QueryPattern> patterns = new HashSet<>();
        
        
        // Give me all mountains.

        QueryPattern q1 = new C(lexicon,instances);
        
        addGiveMePrefixes((StringElement) q1.getElement(0));
        addNouns(q1.getElement(1));

        patterns.add(q1);
        
        // Which movies are there?
        
        QueryPattern q2 = new C(lexicon,instances);
        
        StringElement e2_0 = (StringElement) q2.getElement(0);
        addWhichPrefixes(e2_0);
        
        addNouns(q2.getElement(1));
        
        StringElement e2_2 = (StringElement) q2.getElement(2);
        e2_2.add("is there",Feature.SINGULAR);
        e2_2.add("are there",Feature.PLURAL);

        patterns.add(q2);
        
        
        // Give me the mayor of Paris. 
        // Who is the president of Cameroon?
        
        QueryPattern q3 = new P_I(lexicon,instances);

        StringElement e3_0 = (StringElement) q3.getElement(0);
        addGiveMePrefixes(e3_0);
        addWhoWhatPrefixes(e3_0);

        addRelationalNouns(q3.getElement(2));
 
        patterns.add(q3);
        
        
        // Who created Miffy? 
        // Who died in Berlin?
        
        QueryPattern q4 = new P_I(lexicon,instances);

        StringElement e4_0 = (StringElement) q4.getElement(0);
        e4_0.add("who");
        e4_0.add("what");

        addVerbs(q4.getElement(3));
        
        patterns.add(q4);
        
        
        // What actors play in Batman?
        
        QueryPattern q5 = new C_P_I(lexicon,instances);

        StringElement e5_0 = (StringElement) q5.getElement(0);
        addWhichPrefixes(e5_0);
        
        addVerbs(q5.getElement(3));
        
        patterns.add(q5);
        
        
        // Give me all actors that play in Batman.
        
        QueryPattern q6 = new C_P_I(lexicon,instances);

        StringElement e6_0 = (StringElement) q6.getElement(0);
        addGiveMePrefixes(e6_0);

        addNouns(q6.getElement(1));
        
        StringElement e6_3 = (StringElement) q6.getElement(3);
        e6_3.add("that");
        e6_3.add("which");
        e6_3.add("who");
        
        addVerbs(q6.getElement(4));
        
        patterns.add(q6);
        
        
        // Which city is the capital of Denmark? 
        
        QueryPattern q7 = new C_P_I(lexicon,instances);

        StringElement e7_0 = (StringElement) q7.getElement(0);
        addGiveMePrefixes(e7_0);
        addWhichPrefixes(e7_0);

        addNouns(q7.getElement(1));
        
        StringElement e7_3 = (StringElement) q7.getElement(3);
        e7_3.add("is a",Feature.SINGULAR,Feature.PRESENT);
        e7_3.add("is the",Feature.SINGULAR,Feature.PRESENT);
        e7_3.add("are the",Feature.SINGULAR,Feature.PRESENT);
        e7_3.add("was a",Feature.SINGULAR,Feature.PAST);
        e7_3.add("was the",Feature.SINGULAR,Feature.PAST);
        e7_3.add("were the",Feature.SINGULAR,Feature.PAST);
        
        addRelationalNouns(q7.getElement(4));
        
        patterns.add(q7);
        
        
        
        // Done. (Yay!)
        
        return patterns;
    
    }
    
    
    /* Making the above relatively DRY... */
    
    private void addNouns(Element e) {
        
        e.addEntries(lexicon, LexicalEntry.POS.NOUN, null);
    }
    
    private void addRelationalNouns(Element e) {
        
        e.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.NounPPFrame);
        e.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.NounPossessiveFrame);
    }
    
    private void addVerbs(Element e) {
        
        e.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.TransitiveFrame);
        e.addEntries(lexicon, LexicalEntry.POS.NOUN, vocab.IntransitivePPFrame);
    }
    
    private void addGiveMePrefixes(StringElement e) {

        e.add("give me");
        e.add("give me all",Feature.PLURAL);
        e.add("give me the",Feature.PLURAL);
        e.add("show me all",Feature.PLURAL);
        e.add("show me the");
        e.add("list all",Feature.PLURAL);
        
        e.add("do you know any",Feature.PLURAL);
        e.add("are there any",Feature.PLURAL);
    }
    
    private void addWhichPrefixes(StringElement e) {
        
        e.add("which");
        e.add("what");
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
