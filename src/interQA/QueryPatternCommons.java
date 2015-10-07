package interQA;

import de.citec.sc.matoll.core.LexicalEntry;

import java.util.*;

/**
 * Created by Mariano on 13/07/2015.
 */
public class QueryPatternCommons {
    /**
     * Utility to remove the lang tags
     * @param ls has strings with @xx
     * @param lang is "en", "es"...
     */
    static public List<String> removeLang(List<String> ls, String lang){
        List<String> mylist = new ArrayList<String>();
        for (String str: ls){
            mylist.add(str.replaceAll("@" + lang, ""));
        }
        return mylist;
    }
    /**
     * Utility to remove the lang tags
     * @param string with @xx (e.g. "movie@en by@en")
     * @param lang is "en", "es"...
     */
    static public String removeLang(String string, String lang){
        return string.replaceAll("@" + lang, "");
    }

    /**
     * Utility to add the lang tags
     * @param canonicalPlusForm withOUT @xx (e.g. "movie by")
     * @param lang is "en", "es"...
     * @return The form as knows by the interQA.PropertyNoun (i.e. "movie@en by@en")
     */
    static public String addLang(String canonicalPlusForm, String lang){
        //Cut the string by (any) spaces
        List<String> itemsWithOut = Arrays.asList(canonicalPlusForm.split("\\s+"));
        List<String> itemsWithLang = new ArrayList<>();
        for (String item : itemsWithOut){
            itemsWithLang.add(item + "@" + lang);
        }
        //Join splits with a space
        StringBuffer buffer = new StringBuffer();
        for(String itemLang : itemsWithLang){
            buffer.append(itemLang + " ");
        }
        //Remove the last space
        String resOK = buffer.toString().substring(0, buffer.length() - 1);
        return resOK;
    }

    static public void addParseableString(HashMap<String,List<LexicalEntry>> map, String str, LexicalEntry entry) {
        if (map.containsKey(str)){   //If the string is in the map
            map.get(str).add(entry); //add the lexical entry for the string
        }
        else {                      //if the string is not in the map
            ArrayList<LexicalEntry> lex = new ArrayList<LexicalEntry>();
            lex.add(entry);         //Create a new map for the string
            map.put(str, lex);
        }
    }
}
