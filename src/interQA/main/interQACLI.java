package interQA.main;


import interQA.lexicon.InstanceSource;
import interQA.lexicon.Lexicon;
import interQA.lexicon.LiteralSource;
import interQA.patterns.QueryPatternManager;
import interQA.patterns.QueryPattern1_1;
import interQA.patterns.QueryPattern9_1;
import interQA.patterns.QueryPattern9_2;
import interQA.patterns.QueryPattern9_3;
import interQA.patterns.QueryPattern0_1;
import interQA.patterns.QueryPattern0_3;
import interQA.patterns.QueryPattern3_1;
import interQA.patterns.*;
import interQA.patterns.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by Mariano on 02/10/2015.
 */
public class interQACLI {
    
    public static void  main(String args[]){
        
        // INIT
        List<String> LabelProps = new ArrayList<>();
        LabelProps.add("http://lod.springer.com/data/ontology/property/confName");
        LabelProps.add("http://lod.springer.com/data/ontology/property/confAcronym");
        
        // Load lexicon     
        Lexicon lexicon = new Lexicon("en");
        lexicon.load("resources/springer_en.ttl");
        InstanceSource instances = new InstanceSource("http://es.dbpedia.org/sparql","en");
        LiteralSource literals = new LiteralSource("http://es.dbpedia.org/sparql","en",LabelProps);
                
        // Load query patterns
        QueryPatternManager qm = new QueryPatternManager();
        
        qm.addQueryPattern(new SpringerQueryPattern0_1_1(lexicon,instances,literals));
        qm.addQueryPattern(new SpringerQueryPattern0_1_2(lexicon,instances,literals));
        qm.addQueryPattern(new SpringerQueryPattern0_2(lexicon,instances,literals));
        qm.addQueryPattern(new SpringerQueryPattern0_3_1(lexicon,instances,literals));
        qm.addQueryPattern(new SpringerQueryPattern0_3_2(lexicon,instances,literals));
        qm.addQueryPattern(new SpringerQueryPattern0_4(lexicon,instances,literals));
        qm.addQueryPattern(new SpringerQueryPattern0_5(lexicon,instances,literals));
        
        /*qm.addQueryPattern(new QueryPattern0_1(lexicon,instances));
        qm.addQueryPattern(new QueryPattern0_2(lexicon,instances));
        qm.addQueryPattern(new QueryPattern0_3(lexicon,instances));
        qm.addQueryPattern(new QueryPattern1_1(lexicon,instances));
        qm.addQueryPattern(new QueryPattern1_2(lexicon,instances));
        qm.addQueryPattern(new QueryPattern1_3(lexicon,instances));
        qm.addQueryPattern(new QueryPattern1_4(lexicon,instances));
        qm.addQueryPattern(new QueryPattern1_5(lexicon,instances));
        qm.addQueryPattern(new QueryPattern3_1(lexicon,instances));
        qm.addQueryPattern(new QueryPattern3_1_1(lexicon,instances));
        qm.addQueryPattern(new QueryPattern3_2(lexicon,instances));
        qm.addQueryPattern(new QueryPattern9_1(lexicon,instances));
        qm.addQueryPattern(new QueryPattern9_2(lexicon,instances));
        qm.addQueryPattern(new QueryPattern9_3(lexicon,instances));*/
        
        
        
        // RUN

        //If we provide arguments, they will be interpreted as file paths
        if (args.length != 0){
            if (args.length != 2) { //We allow only two params
               System.out.println("Only two params are allowed. Both are file paths. The first is the stdin, the second is the stdout.");
                System.exit(1);
            }
            //Follows only if there are two params
            String inFileWithPath =  args[0];  //First argument
            String outFileWithPath = args[1];  //Second argument
            try {
                //Reassign the standard input to the given file
                System.setIn(new FileInputStream(new File(inFileWithPath)));
                //Reassign the standard output to the given file
                System.setOut(new PrintStream(new FileOutputStream(new File(outFileWithPath))));
            }catch (FileNotFoundException e){
                System.out.println("Fatal error. The argument " + inFileWithPath + " is not a valid input file, or " +
                                                "the argument " + outFileWithPath + " is not a valid output file.");
                System.exit(1);
            }
        }
        
        Scanner scanner = new Scanner(System.in);
        StringBuffer sbWholeSentenceInternal = new StringBuffer();
        StringBuffer sbWholeSentenceExternal = new StringBuffer();
        String lastSelection = new String("");
        
        List<String> opts = null;
        
        do {
            System.out.println("Current sentence: " + sbWholeSentenceExternal.toString());

            opts = qm.getUIoptions();
            
            if (opts.isEmpty()) {
                System.out.println("SPARQL queries:");
                for (String query : qm.buildSPARQLqueries()) {
                     System.out.println(query);
                }
                break; //Better than exit in order to be used in test units
            } 
            
            int index = 1;
            System.out.println("Choose one option (or 'q' to quit, 'd' to delete the last selection):");
            for (String str : opts) {
                System.out.println(index++ + ": " + str);
            }
            int num = 0;
            while (true) {
                try {
                    if (scanner.hasNextInt()){
                        num = scanner.nextInt(); //If you press 1+Enter, the Enter (a \n character) keeps in the scanner
                        scanner.skip(Pattern.compile(".*\\s")); //Skips all that finishes with a whitespace character: [ \t\n\x0B\f\r]
                        if (num < 1 || num > opts.size()) {
                            System.out.println("Please, select a valid number");
                            throw(new NumberFormatException());
                        }
                    }else{
                        if (scanner.hasNextLine()) {
                            String comm = scanner.nextLine();
                            if (comm.equals("q") || comm.equals("d")) {
                                if (comm.equals("q")) {
                                    System.exit(0);
                                }
                                if (comm.equals("d")) {
                                    num = -1;
                                }
                            }else {
                                System.out.println("Invalid command");
                                throw(new NumberFormatException());
                            }
                        }
                    }
                    break;
                } catch (NumberFormatException e) {
                    //if () = Integer.parseInt(scanner.nextLine());
                    System.out.println("Please, type a number (or 'q' to quit, 'd' to delete the last selection)");
                }

            }
            if (num == -1 ){ //The user selected to delete the last option
                String renewedInternalSentence = sbWholeSentenceInternal.delete(sbWholeSentenceInternal.length() - lastSelection.length(),
                        sbWholeSentenceInternal.length())
                        .toString();
                sbWholeSentenceInternal.delete(0, sbWholeSentenceInternal.length()); //Empties the buffer
                sbWholeSentenceInternal.append(renewedInternalSentence);                     //Adds the renewed sentence

                String renewedExternalSentence = sbWholeSentenceExternal.delete(sbWholeSentenceExternal.length() - lastSelection.length() - 1,
                        sbWholeSentenceExternal.length())
                        .toString();
                sbWholeSentenceExternal.delete(0, sbWholeSentenceExternal.length()); //Empties the buffer
                sbWholeSentenceExternal.append(renewedExternalSentence);
            }else {  //The user selected one option from the options list
                lastSelection = opts.get(num - 1);
                sbWholeSentenceInternal.append(lastSelection);
                sbWholeSentenceExternal.append(" " + lastSelection);
            }
            List<String> avlPats = qm.userSentence(sbWholeSentenceInternal.toString());
            System.out.println("Number of patterns available: " + avlPats.toString());
        }while (opts.size() != 0);

    }
}
