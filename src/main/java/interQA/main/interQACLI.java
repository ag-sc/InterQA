package interQA.main;


import interQA.patterns.QueryPatternManager;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry.Language;
import interQA.lexicon.Lexicon;
import interQA.patterns.QueryPatternFactory_EN;

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
        
    	List<String> labels = new ArrayList<>();
        labels.add("http://www.w3.org/2000/01/rdf-schema#label");
        labels.add("http://lod.springer.com/data/ontology/property/confName");
        labels.add("http://lod.springer.com/data/ontology/property/confAcronym");
        
        // Load lexicon  
        
        Lexicon lexicon = new Lexicon(Language.EN);
        lexicon.load("./src/main/java/resources/springer_en.ttl");
        DatasetConnector dataset = new DatasetConnector("http://es.dbpedia.org/sparql",Language.EN,labels);
       
                
        // Load query patterns
        
        QueryPatternManager    qm = new QueryPatternManager(); 
        QueryPatternFactory_EN qf = new QueryPatternFactory_EN(lexicon,dataset);
        
        qm.addQueryPatterns(qf.rollout());
        
        
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
