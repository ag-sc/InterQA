package interQA.lexicon;

import java.util.Arrays;

/**
 *
 * @author cunger
 */
public class Inflector_en implements Inflector {
    
    
    String[] vowels = { "a", "e", "i", "o", "u" };
    
    
    @Override
    public String getPlural(String noun) {
      
        String n = noun.toLowerCase();
        
        if (n.endsWith("s")  ||
            n.endsWith("z")  || 
            n.endsWith("x")  ||
            n.endsWith("sh") || 
            n.endsWith("ch")) {
            return noun + "es";
        }
        if (n.endsWith("y")) {
            String secondLastChar = n.charAt(n.length()-2)+"";
            if (Arrays.asList(vowels).contains(secondLastChar)) {
                return noun + "s";
            }
            else {
                return noun.substring(0,noun.length()-1) + "ies";
            }
        }
        if (n.endsWith("o")) {
            String secondLastChar = n.substring(n.length()-2,n.length()-2);
            if (secondLastChar.equals("y") || Arrays.asList(vowels).contains(secondLastChar)) {
                return noun + "s";
            }
            else {
                return noun + "es";
            }
        }   
        return noun + "s";
    }
    
    public String getAorAn(String noun) {
      
        String n = noun.toLowerCase();
        
        if (n.startsWith("eu")  || 
            n.startsWith("uni") ||
            n.startsWith("up")) {
            return "a";
        }
        if (n.startsWith("un")) {
            return "an"; 
        }
        if (n.startsWith("a") || 
            n.startsWith("e") || 
            n.startsWith("i") || 
            n.startsWith("o")) {
            return "an";
        }
        return "a"; 
    }
    
    @Override
    public String getPresent(String verb, int person) {
        
        // regular verbs behave like noun plurals
        return getPlural(verb);
    }
    
    @Override
    public String getPast(String verb, int person) {
     
        String v = verb.toLowerCase();
        
        if (v.endsWith("s")  ||
            v.endsWith("z")  ||
            v.endsWith("x")  || 
            v.endsWith("sh") || 
            v.endsWith("ch")) {
            return verb + "ed";
        }
        String lastChar       = v.charAt(v.length()-1)+"";
        String secondLastChar = v.charAt(v.length()-2)+"";
        String thirdLastChar  = v.charAt(v.length()-3)+"";
        if (!Arrays.asList(vowels).contains(lastChar) 
         &&  Arrays.asList(vowels).contains(secondLastChar)
         && !Arrays.asList(vowels).contains(thirdLastChar)) {
         return verb + lastChar + "ed";
        }
        if (lastChar.equals("y") && !Arrays.asList(vowels).contains(secondLastChar)) {
            return verb.substring(0,verb.length()-1) + "ied";
        }
        if (v.endsWith("e")) {
            return verb + "d";
        }        
        return verb + "ed";
    }
    
    @Override
    public String getPastParticiple(String verb) {
        
        return getPast(verb,3);
    }
    
    @Override 
    public String getComparative(String adjective) {
        // TODO
        return "more " + adjective;
    }

    @Override 
    public String getSuperlative(String adjective) {
        // TODO
        return "most " + adjective;
    }
}
