package interQA.main;


import interQA.patterns.QueryPatternFactory_ES;
import interQA.patterns.QueryPatternManager;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.LexicalEntry.Language;
import interQA.lexicon.Lexicon;
import interQA.lexicon.SparqlQueryBuilder;
import interQA.patterns.QueryPatternFactory_DE;
import interQA.patterns.QueryPatternFactory_EN;
import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Mariano on 02/10/2015.
 */
public class interQACLI {
    
    public enum USECASE  { SPRINGER, DBPEDIA }
    static String typeNumberOrCommand = "Please, type a number (or 'q' to quit, 'd' to delete the last selection):";
    static String typeStringOrCommand = "Please, type an option (or 'q' to quit, 'd' to delete the last selection):";
    static String typeCommand         = "Please, type a command: 'q' to quit, 'd' to delete the last selection";
    static String trapSentence = "SPARQL queries:";
    
    /**
     * This is the main of the class. A return in this method is an automatic exit.
     * Without any parameter, runs on console using the Springer dataset (hosted in esDBpedia) in English.
     * @param args
     */
    public static void  main(String args[]){
        if (args.length == 0) {  //No args
            mainProcess(args, USECASE.DBPEDIA, Language.EN);
        }else{                  //We provide args
            if (args.length == 2) { //2 params mean in and out file names
                mainProcess(args, USECASE.SPRINGER, Language.EN);
            }else{                 //1, 3, 4, 5 params...
                if (args.length == 4) { //4 params mean in and out file names, USECASE and Lang
                    USECASE usecase = USECASE.valueOf(args[2]);    //The arg can be String SPRINGER or DBPEDIA
                    Language lang = Language.valueOf(args[3]); //The arg can be String EN or DE
                    mainProcess(args, usecase, lang);
                }else {
                    if (args.length > 4) { //more than 4 params mean in and out file names, USECASE, Lang and a serie of query-pattern names
                        USECASE usecase = USECASE.valueOf(args[2]);    //The arg can be String SPRINGER or DBPEDIA
                        Language lang = Language.valueOf(args[3]); //The arg can be String EN or DE
                        ArrayList<String> qpNames= new ArrayList<>();
                        for (int i =4; i < args.length; i++){
                            qpNames.add(args[i]);
                        }
                        mainProcess(args, usecase, lang, qpNames);
                    }else {     //Only 1 and 3 params mean error.
                        System.out.println("Wrong number of params.");
                        System.exit(1);
                    }
                }
            }
        }

    }

    /**
     * This is NOT the main of the class to avoid automatic exit in return.
     * Uses Springer dataset (hosted in esDBpedia) in English
     * @param args
     */
    public static void  mainProcess(String args[]){
        mainProcess(args, USECASE.SPRINGER,  Language.EN);
    }


    /**
     * Specialization to support other datasets and languages
     * @param args
     * @param usecase
     * @param language
     */
    public static void  mainProcess(String args[], USECASE usecase, Language language ) {
        mainProcess(args, usecase, language, null );
    }

    /**
     * Specialization to support other datasets and languages; and specific queryPatterns specified by "its name".
     * If qpNames is null, it will consider all the query patterns available.
     * @param args
     * @param usecase
     * @param language
     * @param qpNames
     */
    public static void  mainProcess(String args[], USECASE usecase, Language language, ArrayList<String> qpNames ){

        // INIT
        Lexicon lexicon = new Lexicon(language);
        DatasetConnector dataset;
        QueryPatternManager qm = new QueryPatternManager();

        switch (usecase) {

            case SPRINGER: {

                List<String> labels = new ArrayList<>();
                labels.add("http://www.w3.org/2000/01/rdf-schema#label");
                labels.add("http://lod.springer.com/data/ontology/property/confName");
                labels.add("http://lod.springer.com/data/ontology/property/confAcronym");

                // Load lexicon

                switch (language) {
                    case EN: lexicon.load("./src/main/java/resources/springer_en.ttl"); break;
                    case DE: lexicon.load("./src/main/java/resources/springer_de.ttl"); break;
                    case ES: lexicon.load("./src/main/java/resources/springer_es.ttl"); break;
                }
                 
                dataset = new DatasetConnector("http://es.dbpedia.org/sparql",language,labels);

                // Load query patterns

                switch (language) {
                    case EN: {
                        QueryPatternFactory_EN qf_en = new QueryPatternFactory_EN(usecase,lexicon,dataset);
                        qm.addQueryPatterns(qpNames == null? qf_en.rollout(): qf_en.rollout(qpNames)); //Support for query patterns "by name"
                        break;
                    }
                    case DE: {
                        QueryPatternFactory_DE qf_de = new QueryPatternFactory_DE(usecase,lexicon,dataset);
                        qm.addQueryPatterns(qf_de.rollout());
                        break;
                    }
                    case ES: {
                        QueryPatternFactory_ES qf_es = new QueryPatternFactory_ES(usecase,lexicon,dataset);
                        qm.addQueryPatterns(qf_es.rollout());
                        break;
                    }
                }

                break;
            }

            case DBPEDIA: {

                List<String> labels = new ArrayList<>();
                labels.add("http://www.w3.org/2000/01/rdf-schema#label");

                // Load lexicon

                switch (language) {
                    case EN: lexicon.load("./src/main/java/resources/dbpedia_en.rdf"); break;
                    case DE: lexicon.load("./src/main/java/resources/dbpedia_de.rdf"); break;
                }
                                
                dataset = new DatasetConnector("http://dbpedia.org/sparql",language,labels);

                // Load query patterns

                switch (language) {
                    case EN: {
                        QueryPatternFactory_EN qf_en = new QueryPatternFactory_EN(usecase,lexicon,dataset);
                        qm.addQueryPatterns(qf_en.rollout());
                        break;
                    }
                    case DE: {
                        QueryPatternFactory_DE qf_de = new QueryPatternFactory_DE(usecase,lexicon,dataset);
                        qm.addQueryPatterns(qf_de.rollout());
                        break;
                    }
                }

                break;
            }
        }





        // RUN

        //We only check the first two args. They will be interpreted as file paths (unless named stdin and stdout)
        if (args.length != 0){  // Only in this case we change stdin and stdout
            //Follows only if there are two params
            String inFileWithPath =  args[0];  //First argument
            String outFileWithPath = args[1];  //Second argument
            if (!inFileWithPath.equals("stdin") && !outFileWithPath.equals("stdout")) {
                try {
                    //Reassign the standard input to the given file
                    System.setIn(new FileInputStream(new File(inFileWithPath)));
                    //Reassign the standard output to the given file
                    System.setOut(new PrintStream(new FileOutputStream(new File(outFileWithPath))));
                } catch (FileNotFoundException e) {
                    System.out.println("Fatal error. The argument " + inFileWithPath + " is not a valid input file, or " +
                            "the argument " + outFileWithPath + " is not a valid output file.");
                    System.exit(1);
                }
            }
        }
        
        Scanner scanner = new Scanner(System.in);
        StringBuffer sbWholeSentenceInternal = new StringBuffer();
        StringBuffer sbWholeSentenceExternal = new StringBuffer();
        String lastSelection = new String("");

        
        List<String> opts = null;
        List<String> queries = null;
        //Select interaction mode
        System.out.println("Welcome to interQACLI");
        System.out.println("You are using dataset " + usecase.name() + " and language " + language.name() + ".");
        System.out.println("You are using these query patterns: " + (qpNames!= null? qpNames: "the ones defined in the case."));

        int interMode = 0;
        do {
            System.out.println("Choose the interaction way:");
            System.out.println("1) Number-based.");
            System.out.println("2) String-based.");
            System.out.println("Type 1 or 2 and press Enter");
            System.out.print("My selection:");
            try {
                if (scanner.hasNextInt()) {//Typed an int
                    int num = scanner.nextInt(); //If you press 1+Enter, the Enter (a \n character) keeps in the scanner
                    scanner.skip(Pattern.compile(".*\\s")); //Skips all that finishes with a \s (whitespace character): [ \t\n\x0B\f\r]
                    if (num != 1 && num != 2) { //INvalid num
                        System.out.println("Please, select a valid number");
                        throw (new NumberFormatException());
                    } else { //Valid num
                        System.out.println("Thanks!.");
                        interMode = num;
                    }
                } else { //Typed a non-int

                }
            } catch (NumberFormatException e) {
                System.out.println(typeNumberOrCommand);
            }
        }while(interMode == 0);
        
        do {
            
            System.out.println("Current sentence: " + sbWholeSentenceExternal.toString());
            
            queries = qm.buildSPARQLqueries();
            
            if(!queries.isEmpty()){
               System.out.println(trapSentence);
                for (String query : queries) {
                     System.out.println(query);
                } 
            }
           
             
            TreeSet<String> optsOrdered = new TreeSet<>(qm.getUIoptions());
            opts = new ArrayList<>(optsOrdered);

            if (opts.size() >  0) { //If there are options, show them
                int index = 1;
                System.out.println(interMode == 1 ? typeNumberOrCommand : typeStringOrCommand);
                for (String str : opts) {
                    System.out.println(index++ + ": " + str);
                }
            }else{
                System.out.println(typeCommand);
            }
            int num = 0;
             while (true) {
                if (interMode == 1) { //Interaction by means of numbers
                      try{
                        if (scanner.hasNextInt()) {
                            num = scanner.nextInt(); //If you press 1+Enter, the Enter (a \n character) keeps in the scanner
                            scanner.skip(Pattern.compile(".*\\s")); //Skips all that finishes with a \s (whitespace character): [ \t\n\x0B\f\r]
                            if (num < 1 || num > opts.size()) {
                                System.out.println("Please, select a valid number");
                                throw (new NumberFormatException());
                            }
                        } else {
                            if (scanner.hasNextLine()) {
                                String comm = scanner.nextLine();
                                if (comm.equals("q") || comm.equals("d")) {
                                    if (comm.equals("q")) {
                                        return; //System.exit(0) is not valid for testing
                                    }
                                    if (comm.equals("d")) {
                                        num = -1;
                                    }
                                } else {
                                    System.out.println("Invalid command");
                                    throw (new NumberFormatException());
                                }
                            }
                        }
                        break;
                    } catch (NumberFormatException e) {
                        //if () = Integer.parseInt(scanner.nextLine());
                        System.out.println(typeNumberOrCommand);
                    }
                }
                if (interMode == 2) { //Interaction by means of strings
                     try{
                        if (scanner.hasNextLine()) { //This is always true
                            String str = scanner.nextLine(); //Removes the trailing \n
                            //scanner.skip(Pattern.compile(".*\\s")); //Skips all that finishes with a \s (whitespace character): [ \t\n\x0B\f\r]
                            if(opts.contains(str)){ //If the str is in the list of options
                                num = 1 + opts.indexOf(str);
                            }else{
                                //Check if it is a command
                                if (str.equals("q") || str.equals("d")) {
                                    if (str.equals("q")) {
                                        return; //System.exit(0) is not valid for testing. And return in a main() exists.
                                    }
                                    if (str.equals("d")) {
                                        num = -1;
                                    }
                                } else {
                                    System.out.println("String '"+ str +"' is not available in the options list. Please, type a valid string");
                                    /**
                                     * WARNING. If the options list has a "d" or "q" element, we will have an ambiguity:
                                     * We do not know if you mean a command or an option.
                                     * By now, we will assume that you mean an option.
                                     */
                                    //System.exit(1);
                                    throw (new NumberFormatException());
                                }
                            }
                        }
                        break;
                    } catch (NumberFormatException e) {
                        //if () = Integer.parseInt(scanner.nextLine());
                        System.out.println(typeStringOrCommand);
                    }
                     
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
            System.out.println("Number of patterns available: " + avlPats.size() + " " + avlPats.toString());
        }while (opts.size() != 0);


    }

    public static ArrayList<String> checkSequence(String sequence, String type) throws Exception {
        interQACLI cli = new interQACLI();
        String fileNameIn  = "test1.cli";
        String fileNameOut = "test1.out";
        PrintWriter writer = new PrintWriter(fileNameIn, "UTF-8"); //Overwrites if exists. Goes to class/
        switch (type){
            case "ByNumber": writer.println("1"); //Selection by number
                             break;
            case "ByString": writer.print("2\n"); //Selection by string. Warn: On Windows, println produces \r\n; on Linux produces only \n; on Mac produces only \r
                             break;
            default:         writer.println("1"); //Selection by number
                             break;
        }
        writer.println(sequence);
        writer.close();
        String args[] = {fileNameIn, fileNameOut};
        cli.mainProcess(args);

        //Read the last lines of the output, looking for a line with trapSentence
        ReversedLinesFileReader object = new ReversedLinesFileReader(new File(fileNameOut)); //Apache commons io
        ArrayList<String> queries = new ArrayList<String>();
        String query = new String();
        while (!(query = object.readLine()).equals(trapSentence)) {  //Reads from the end of the file till this line
            queries.add(query);
        }
        return(queries);
    }

    /**
     * Simulates the user interaction by selecting numbers. Uses files that are overwritten in each test
     * @param sequence is the sequence of tokens typed by the user
     * @throws Exception
     */
    public static ArrayList<String> checkSequenceByNumber(String sequence) throws Exception {

        return checkSequence (sequence, "ByNumber");
    }

    /**
     * Simulates the user interaction by typing strings. Uses files that are overwritten in each test
     * By defaukt uses Springer dataset and English
     * @param sequence is the sequence of tokens typed by the user
     * @throws Exception
     */
    public static Set<String> checkSequenceByStrings(String sequence) throws Exception {
        return (checkSequenceByStrings(sequence,USECASE.SPRINGER,  Language.EN));
    }

    /**
     * Simulates the user interaction by typing strings. Uses files that are overwritten in each test
     * Specialized version for other datasets and languages. Uses all the query patterns available.
     * @param sequence is the sequence of tokens typed by the user
     * @throws Exception
     */
    public static Set<String> checkSequenceByStrings(String sequence, USECASE usecase, Language language) throws Exception {
        return checkSequenceByStrings(sequence, usecase, language, null);
    }

    /**
     * Simulates the user interaction by typing strings. Uses files that are overwritten in each test
     * Specialized version for other datasets and languages. Can specify query patterns "by name".
     * @param sequence is the sequence of tokens typed by the user
     * @throws Exception
     */
    public static Set<String> checkSequenceByStrings(String sequence, USECASE usecase, Language language, String[] qpNames) throws Exception {
        interQACLI cli = new interQACLI();
        String fileNameIn  = "test1.cli";
        String fileNameOut = "test1.out";
        PrintWriter writer = new PrintWriter(fileNameIn, "UTF-8"); //Overwrites if exists. Goes to class/
        writer.print("2\n"); //Selection by string. Warn: On Windows, println produces \r\n; on Linux produces only \n; on Mac produces only \r
        writer.println(sequence);
        writer.close();
        String args[] = {fileNameIn, fileNameOut};
        ArrayList<String> qps = null;
        if (qpNames != null){
             qps = new  ArrayList<String>(Arrays.asList(qpNames)); //Converts String[] to ArrayList
        }
        cli.mainProcess(args, usecase, language,qps);

        //Read the last lines of the output, looking for a line with the trapSentence
        ReversedLinesFileReader object = new ReversedLinesFileReader(new File(fileNameOut)); //Apache commons io
        ArrayList<String> queries = new ArrayList<String>();
        String query = new String();
        while (!(query = object.readLine()).equals(trapSentence)) {  //Reads from the end of the file till this line
            queries.add(query);
        }
        int lastInvalidLine = //From here onwards we have to delete. There are 3 possible terminations
                              queries.indexOf(typeNumberOrCommand) != -1? //finishes with typeNumberOrCommand?
                                   queries.indexOf(typeNumberOrCommand)      //yes
                                  :                                         //no
                                   queries.indexOf(typeStringOrCommand) != -1?  //finishes with typeStringOrCommand?
                                       queries.indexOf(typeStringOrCommand)  //yes
                                      :                                      //no
                                       queries.indexOf(typeCommand)             //Assume finishes with typeCommand
                             ;
        List<String> toDelete = queries.subList(0, 1 + lastInvalidLine);
        toDelete.clear();

        //Converts ArrayList to Set
        Set<String> queriesSet = new HashSet<String>(queries);

        return(queriesSet);
    }
}
