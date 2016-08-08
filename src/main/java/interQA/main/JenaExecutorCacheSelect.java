package interQA.main;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.apache.jena.sparql.resultset.ResultsFormat;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Mariano on 21/07/2016.
 */
public class JenaExecutorCacheSelect{
    private Map<String, ResultSetRewindable> cache = null;
    private Boolean isFirstTime = true;
    static private final String fileNameTail = "cacheSelect.ser";
    static private ResultsFormat format = ResultsFormat.FMT_RS_TSV;//FMT_RS_XML;//

    public ResultSet executeWithCache(String endpoint, String sparqlQuery) {
        ResultSet res = null;

        if (isFirstTime){
            //Checks if there is a cache serialization in the file system
            File f = new File(getCacheFileName(endpoint));
            if (f.isFile() && f.canRead()) { // If there is a cache file... load it.
                readCacheFromDisk(endpoint);
            }else{  //There is no cache file
                //We use the static object cache
                cache = new HashMap<>();
            }
            isFirstTime = false;
        }

        //We use the cache
        if (cache.containsKey(sparqlQuery)) { //the sparqlQuery is in the cache
            res = cache.get(sparqlQuery);         //get the results from the cache
        } else {                               //the sparqlQuery is NOT in the cache
            QueryExecution ex =
                    QueryExecutionFactory.sparqlService(endpoint, sparqlQuery);
            try {
                res = ex.execSelect();               //Make the query to the endpoint
            }
            catch(QueryExceptionHTTP eqe) { //E.g. the query requires too much to solve (HttpException: 500)
                System.out.println("Error making the SPARQL :" + eqe.getResponseMessage());
                eqe.printStackTrace();
                throw(eqe);
            }
            cache.put(sparqlQuery,                        //And store the information in the cache
                    ResultSetFactory.copyResults(res)); //It is CRITICAL to make a copy in-memory or it will be destroyed
            //The cache stores ResultSetRewindable objects
            System.out.println("New element stored in CacheSelect (in memory). Now it has " + cache.size() + " elements.");
            //Save the cache to disk
            //saveCacheToDisk(); Now it is a method
        }

        return res;
    }
    public void interactiveExplorer(){
        PrintStream ps = System.out;

        ps.println("Welcome to the cache explorer");
        ps.println("=============================");
        ps.println("SPARQL queries in the cache:");
        String[] queries    = new String[cache.size()];
        ResultSetRewindable[] resultsets = new ResultSetRewindable[cache.size()];
        int index = 0;
        for (Map.Entry<String, ResultSetRewindable> mapEntry : cache.entrySet()) {
            queries[index] = mapEntry.getKey();
            resultsets[index] = mapEntry.getValue();
            index++;
        }

        Scanner scanner = new Scanner(System.in);
        boolean wannaPlayAgain = true;
        while (wannaPlayAgain == true) {
            //Select the query
            int queryNum = 0;
            while (queryNum == 0) {
                index = 0;
                for (String q : queries) {
                    ps.println((index + 1) + ": " + q); //First is 1, not 0.
                    index++;
                }
                ps.println("Choose a query (type its number):");
                if (scanner.hasNextInt()) {
                    queryNum = scanner.nextInt(); //If you press 1+Enter, the Enter (a \n character) keeps in the scanner
                    scanner.skip(Pattern.compile(".*\\s")); //Skips all that finishes with a \s (whitespace character): [ \t\n\x0B\f\r]
                    if (queryNum < 1 || queryNum > cache.size()) {
                        ps.println("Please, select a valid number");
                        queryNum = 0;
                        //throw (new NumberFormatException());
                    } else { //All right
                        ps.println("Thanks!. You selected query " + queryNum + ": " + queries[queryNum - 1]);
                    }
                } else {
                    ps.println("Please, select a valid number");
                }
            }
            //Select the variable in the resultset
            ResultSetRewindable res = resultsets[queryNum - 1];
            res.reset();
            ps.println("These are the variables:");
            index = 0;
            for (String var : res.getResultVars()) {
                ps.println((index + 1) + ": " + var);
                index++;
            }
            int varNum = 0;
            while (varNum == 0) {
                ps.println("Choose the variable (type a number):");
                if (scanner.hasNextInt()) {
                    varNum = scanner.nextInt(); //If you press 1+Enter, the Enter (a \n character) keeps in the scanner
                    scanner.skip(Pattern.compile(".*\\s")); //Skips all that finishes with a \s (whitespace character): [ \t\n\x0B\f\r]
                    if (varNum < 1 || varNum > res.getResultVars().size()) {
                        ps.println("Please, select a valid number");
                        varNum = 0;
                        //throw (new NumberFormatException());
                    } else { //All right
                        ps.println("Thanks!. You selected variable " + res.getResultVars().get(varNum - 1)  + ".");
                    }
                } else {
                    ps.println("Please, select a valid number");
                }
            }
            //Select the value
            String var = res.getResultVars().get(varNum - 1);
            ps.println("Values for var " + var + ":");
            index = 0;
            for (; res.hasNext(); ) {
                QuerySolution qs = res.next();
                RDFNode node = qs.get(var);
                String shownValue = node == null? "empty" : node.toString();
                ps.println((index + 1) + ": " + shownValue);
                index++;
            }
            if (index == 0){
              ps.println("There are NO values!! :-(");
            }
            res.reset(); //Or the ResultSet will look empty

            //Play again?
            int valid = 0;
            while (valid == 0) {
                ps.println("Do you want to play again? (y/n)");
                if (scanner.hasNextLine()) {
                    String comm = scanner.nextLine();
                    if (comm.equals("y") || comm.equals("Y") ||
                            comm.equals("n") || comm.equals("N")) {
                    }
                    if (comm.equals("y") || comm.equals("Y")) {
                        valid = 1;
                        wannaPlayAgain = true;
                    }
                    if (comm.equals("n") || comm.equals("N")) {
                        valid = 1;
                        wannaPlayAgain = false;
                    }
                } else {
                    ps.println("Invalid command.");
                }
            }
        } //while wannaPlayAgain

    }

    private static String getCacheFileName(String endpoint){
        String epPart = endpoint.substring("http://".length(),
                endpoint.length() - "/sparql".length()); //Error prone (https or /sparql/)
        return (epPart + "." + fileNameTail);
    }

    private void readCacheFromDisk(String endpoint) {
        readCacheFromDisk(endpoint, getCacheFileName(endpoint));
    }

    private void readCacheFromDisk(String endpoint, String otherFileName) {
        try {
            FileInputStream fis = new FileInputStream(otherFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Map<String, String> mapSer = (Map<String, String>) ois.readObject();
            //In memory we have a cache with ResultSets. For a while
            // (if we know how to clear the serialized version) we have both
            // (the serialized and the ResultSet) in memory.
            cache = new HashMap<>();
            for (Map.Entry<String, String> entrySer : mapSer.entrySet()){
                String sparqlQuery = entrySer.getKey();
                String entryResSer = entrySer.getValue();
                InputStream stream = new ByteArrayInputStream(entryResSer.getBytes(StandardCharsets.UTF_8)); //How heavy is it?
                ResultSet res = ResultSetFactory.load(stream, format);
                cache.put(sparqlQuery, ResultSetFactory.copyResults(res)); //Stores a ResultSetRewindable
            }
            mapSer = null; //This should remove the object from memory
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe){ //i
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe){
            cnfe.printStackTrace();
        } finally {
            //ois.close();
        }
    }
    /**
     * Saves only if there is some data
     */
    public void saveCacheToDisk(String endpoint) {
        //Save the cache to disk
        if (cache == null) {
            return;
        }
        if (cache.size() > 0) {
            try {
                FileOutputStream fos = new FileOutputStream(getCacheFileName(endpoint));
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                //Writes a serialized version of the ResultSet
                Map<String, String> mapSer = new HashMap<>();
                for (Map.Entry<String, ResultSetRewindable> entry : cache.entrySet()){
                    String    sparqlQuery   = entry.getKey();
                    ResultSetRewindable res = entry.getValue();
                    OutputStream os = null;
                    try {
                        os = new ByteArrayOutputStream();
                        ResultSetFormatter.output(os, res, format); //Writes a serialization of ResultSet in os
                        String serialization = os.toString();
                        mapSer.put(sparqlQuery, serialization);
                    }finally {
                        os.close();
                    }
                }
                oos.writeObject(mapSer); //Writes the serialized version
                oos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e) { //java.io.NotSerializableException: org.apache.jena.sparql.engine.ResultSetCheckCondition
                e.printStackTrace();
            }
        }
    }
    public String cacheUsageReport(){
        return (cache == null? "SelectCache not initialized": cache.size() + " Select queries used.");
    }
    public String dump(){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        dump(ps);
        return(ps.toString());
    }
    public void dump(PrintStream ps){
        ps.println("   CacheSelect dump:");
        ps.println("   -----------------");
        ps.print  ("   Number of elements: ");
        if (cache == null){
            ps.println("0");
        }else {
            int size = cache.size();
            ps.println(size);
            int counter = 0;
            int limit = 10;
            for (Map.Entry<String, ResultSetRewindable> entry :cache.entrySet()) {
                ps.println("   query (" + ++counter + "/" + size + ")= " + entry.getKey());
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ResultSetRewindable res =  entry.getValue();
                ResultSetFormatter.outputAsTSV(os, res); //See TSV at http://www.iana.org/assignments/media-types/text/tab-separated-values
                ps.println("    \\--> ResultSet (" + myGetNumResults(res) + ") = " + upToNthLines(os.toString(), limit));
                res.reset(); //The resultSet has been iterated in the ResultSetFormatter.outputAsTSV method
            }
        }
    }
    static int myGetNumResults(ResultSetRewindable r){
        return r.size();
    }
    /**
     * Reads the DEFAULT cache fileName and dumps information in System.out
     */
    static public void dumpCacheinDisk(String endpoint) {
        dumpCacheinDisk(endpoint, getCacheFileName(endpoint));
    }

    /**
     * Reads the specified cache fileName and dumps information in System.out
     * @param otherFileName The name of the file to analyze
     */
    static public void dumpCacheinDisk(String endpoint, String otherFileName) {
        JenaExecutorCacheSelect jecs = new JenaExecutorCacheSelect();
        File f = new File(otherFileName);
        if (f.isFile() && f.canRead()) { // If there is a cache file... load it.
            long start = System.currentTimeMillis();
            jecs.readCacheFromDisk(endpoint, otherFileName);
            long lapse = System.currentTimeMillis() - start;
            //jecs.dump(System.out);
            System.out.println("Usage report for cache file "+ otherFileName + ": ");
            System.out.println("  Time (milisecs) required to load the cache file: "+ lapse);
            long sizebytes  = f.length();
            System.out.println("  File size: " + sizebytes + " bytes (~" + sizebytes/1024/1024 + "MB.)");
            System.out.println("  " + jecs.cacheUsageReport());
            jecs.dump(System.out);
        } else {
            System.out.println(otherFileName + " is not available.");
        }
    }

    /**
     * If s has new lines (\n or \r or...) will join up to uptoNlines nlines lines
     * @param s
     * @param uptoNlines
     * @return
     */
    private static String upToNthLines(String s, int uptoNlines){
        StringBuffer whole = new StringBuffer();
        new BufferedReader(new StringReader(s))
                .lines().limit(uptoNlines).forEach( //lines can be \n or \r or...
                (line) -> whole.append(line).append(", ")
        );
        return whole.append("...").toString();
    }



    static public void main8 (String[] args) {
        JenaExecutorCacheSelect cacheSelect = new JenaExecutorCacheSelect();
        String ep =    "http://es.dbpedia.org/sparql";
        ResultSet res1 = cacheSelect.executeWithCache(ep,
                "SELECT DISTINCT ?x{  ?subject <http://lod.springer.com/data/ontology/property/confCountry> ?x . }"
        );
        ResultSet res2 = cacheSelect.executeWithCache(ep,
                "SELECT DISTINCT ?x{  ?subject <http://lod.springer.com/data/ontology/property/confCountry> ?x . }"
        );
        cacheSelect.dump(System.out);
        cacheSelect.saveCacheToDisk(ep);
    }
    static public void main1 (String[] args) {
        String ep =    "http://es.dbpedia.org/sparql";
        dumpCacheinDisk(ep, "cacheSelect.20160804.v2.ser");
    }
    static public void main2 (String[] args) {
        String ep =    "http://es.dbpedia.org/sparql";
        dumpCacheinDisk(ep);
    }
    static public void main (String[] args) {
        JenaExecutorCacheSelect cacheSelect = new JenaExecutorCacheSelect();
        String ep =    "http://es.dbpedia.org/sparql";
        ResultSet res1 = cacheSelect.executeWithCache(ep,
                "SELECT DISTINCT * WHERE { ?x a <http://lod.springer.com/data/ontology/class/Conference> ; <http://lod.springer.com/data/ontology/property/confCity> ?I OPTIONAL { { ?I <http://www.w3.org/2000/01/rdf-schema#label> ?l } UNION { ?I <http://lod.springer.com/data/ontology/property/confName> ?l } UNION { ?I <http://lod.springer.com/data/ontology/property/confAcronym> ?l }} }"
        );
        cacheSelect.interactiveExplorer();
    }


}
