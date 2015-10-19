package interQA.main;


import interQA.lexicon.InstanceSource;
import interQA.lexicon.Lexicon;
import interQA.patterns.QueryPatternManager;
import interQA.patterns.QueryPattern1_1;
import interQA.patterns.QueryPattern2_1;
import interQA.patterns.QueryPattern2_2;
import interQA.patterns.QueryPattern2_3;
import interQA.patterns.QueryPattern3_1;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by Mariano on 02/10/2015.
 */
public class interQACLI {
    
    public static void  main(String args[]){
        
        // INIT
        
        // Load lexicon     
        Lexicon lexicon = new Lexicon();
        lexicon.load("resources/dbpedia_en.rdf");
        InstanceSource instances = new InstanceSource("http://dbpedia.org/sparql");
        
        // Load query patterns
        QueryPatternManager qm = new QueryPatternManager();
        qm.addQueryPattern(new QueryPattern1_1(lexicon));
        qm.addQueryPattern(new QueryPattern2_1(lexicon,instances));
        qm.addQueryPattern(new QueryPattern2_2(lexicon,instances));
        qm.addQueryPattern(new QueryPattern2_3(lexicon,instances));
        qm.addQueryPattern(new QueryPattern3_1(lexicon,instances));
        
        // RUN 
        
        Scanner scanner = new Scanner(System.in);
        StringBuffer sbInternal = new StringBuffer();
        StringBuffer sbExternal = new StringBuffer();
        
        List<String> opts = null;
        
        do {
            System.out.println("Current sentence: " + sbExternal.toString());

            opts = qm.getUIoptions();
            
            if (opts.isEmpty()) {
                System.out.println("SPARQL queries:");
                for (String query : qm.buildSPARQLqueries()) {
                     System.out.println(query);
                }
                System.exit(0);
            } 
            
            int index = 1;
            System.out.println("Choose one option (or 'q' to quit):");
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
                            if (comm.equals("q")) {
                                System.exit(0);
                            }else {
                                System.out.println("Invalid command");
                                throw(new NumberFormatException());
                            }
                        }
                    }
                    break;
                } catch (NumberFormatException e) {
                    //if () = Integer.parseInt(scanner.nextLine());
                    System.out.println("Please, type a number (or 'q' to quit)");
                }

            }
            String selectedOpt = opts.get(num - 1);
            sbInternal.append(selectedOpt);
            sbExternal.append(" " + selectedOpt);
            List<String> avlPats = qm.userSelects(sbInternal.toString());
            System.out.println("Number of patterns available: " + avlPats.toString());
        }while (opts.size() != 0);

    }
}
