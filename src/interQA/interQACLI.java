package interQA;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by Mariano on 02/10/2015.
 */
public class interQACLI {
    public static void  main(String args[]){
        QueryPatternManager qm = new QueryPatternManager();
        qm.addQueryPattern(new QueryPattern1("test1.rdf"));
        qm.addQueryPattern(new QueryPattern2("test2.rdf"));
        Scanner scanner = new Scanner(System.in);
        StringBuffer sbInternal = new StringBuffer();
        StringBuffer sbExternal = new StringBuffer();
        List<String> opts = null;
        do {
            opts = qm.getUIoptions();
            int index = 1;
            System.out.println("Current sentence: " + sbExternal.toString());
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
