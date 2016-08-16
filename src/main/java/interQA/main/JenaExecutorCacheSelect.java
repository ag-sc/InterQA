package interQA.main;


import interQA.Config;
import org.apache.jena.query.*;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.apache.jena.sparql.resultset.ResultSetMem;
import org.apache.jena.sparql.resultset.ResultsFormat;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import static interQA.Config.ExtractionMode.NaiveExtraction;
import static interQA.Config.ExtractionMode.intensiveExtraction;

/**
 * Created by Mariano on 21/07/2016.
 */
public class JenaExecutorCacheSelect{

    private TreeMap<String, ResultSetRewindable> cache = null;
    private Boolean isFirstTime = true;
    private Config.ExtractionMode extractionMode = NaiveExtraction;
    private boolean useHistoricalCache = false; // The historical cache is the cache file result of previous executions

    static private final String fileNameTail = "cacheSelect.ser";
    static private ResultsFormat format = ResultsFormat.FMT_RS_TSV;//FMT_RS_XML;//
    static final int BLOQSIZE = 10000; //The maximum number of rows in a resultset. For Virtuoso it is 10.000
    static final int WAIT_MILLIS = 100; //Milliseconds to wait if the server is too busy. After this period tries it again.

    public JenaExecutorCacheSelect (){
        //By default, the extraction mode is NaiveExtraction
        //By default, useCache is false
    }

    public JenaExecutorCacheSelect (Config.ExtractionMode em, boolean useHistoricalCache){
        extractionMode = em;
        this.useHistoricalCache = useHistoricalCache;
    }
    public void setCacheMode (Config.ExtractionMode em, boolean useHistoricalCache)
    {
        this.extractionMode = em;
        this.useHistoricalCache = useHistoricalCache;
    }
    public boolean isUsingHistoricalCache(){return useHistoricalCache;}
    public Config.ExtractionMode getCacheExtractionMode(){ return extractionMode; }

    public ResultSet executeWithCache(String endpoint, String sparqlQuery) {
        ResultSet res = null;

        if (isFirstTime){
            //Checks if there is a cache serialization in the file system
            File f = new File(getCacheFileName(endpoint));
            if (f.isFile() && f.canRead() &&  useHistoricalCache) { // If there is a cache file... load it.
                readCacheFromDisk(endpoint);
            }else{  //There is no cache file
                //We use the static object cache
                cache = new TreeMap<>();
            }
            isFirstTime = false;
        }

        //We use the cache
        if (cache.containsKey(sparqlQuery)) { //the sparqlQuery is in the cache
            res = cache.get(sparqlQuery);         //get the results from the cache
        } else {                               //the sparqlQuery is NOT in the cache
            res = extractiveExecSelect(endpoint, sparqlQuery, BLOQSIZE, WAIT_MILLIS, extractionMode);

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
        if (cache == null){
            ps.println("No one. Cache not initialized yet.");
            return;
        }

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
                    ps.println((index + 1) + "(with "+ resultsets[index].size() + " elements): " + q); //First is 1, not 0.
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
    static public void interactiveExplorerForCacheinDiskSpecificFile(String otherFileName) {
        JenaExecutorCacheSelect jecs = new JenaExecutorCacheSelect();
        File f = new File(otherFileName);
        if (f.isFile() && f.canRead()) { // If there is a cache file... load it.
            long start = System.currentTimeMillis();
            jecs.readCacheFromDiskSpecificFile(otherFileName);
            long lapse = System.currentTimeMillis() - start;

            System.out.println("Usage report for cache file "+ otherFileName + ": ");
            System.out.println("  Time (milisecs) required to load the cache file: "+ lapse);
            long sizebytes  = f.length();
            System.out.println("  File size: " + sizebytes + " bytes (~" + sizebytes/1024/1024 + "MB.)");
            System.out.println("  " + jecs.cacheUsageReport());
            jecs.interactiveExplorer();
        } else {
            System.out.println(otherFileName + " is not available.");
        }
    }



    private static String getCacheFileName(String endpoint){
        String epPart = endpoint.substring("http://".length(),
                endpoint.length() - "/sparql".length()); //Error prone (https or /sparql/)
        return (epPart + "." + fileNameTail);
    }

    private void readCacheFromDisk(String endpoint) {
        readCacheFromDiskSpecificFile(getCacheFileName(endpoint));
    }

    private void readCacheFromDiskSpecificFile(String fileName) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        System.out.print("Loading Select cache from disk...");
        try {
            fis = new FileInputStream(fileName);
            ois = new ObjectInputStream(fis);
            Map<String, String> mapSer = (Map<String, String>) ois.readObject();
            //In memory we have a cache with ResultSets. For a while
            // (if we know how to clear the serialized version) we have both
            // (the serialized and the ResultSet) in memory.
            cache = new TreeMap<>();
            for (Map.Entry<String, String> entrySer : mapSer.entrySet()){
                String sparqlQuery = entrySer.getKey();
                String entryResSer = entrySer.getValue();
                InputStream stream = new ByteArrayInputStream(entryResSer.getBytes(StandardCharsets.UTF_8)); //How heavy is it?
                ResultSet res = ResultSetFactory.load(stream, format);
                cache.put(sparqlQuery, ResultSetFactory.copyResults(res)); //Stores a ResultSetRewindable
            }
            mapSer = null; //This should remove the object from memory (at least available for the GC)
            System.out.println("done!.");
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe){ //i
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe){
            cnfe.printStackTrace();
        } catch (OutOfMemoryError oome){
            System.out.println("Sorry, not enough memory to read the cache in disk :-(. Stack trace:");
            oome.printStackTrace();
        } finally {
            try {
                ois.close();
                fis.close();
            }catch (IOException ioe){
                System.out.println("Sorry, I can not close the cache file. Stack trace:");
                ioe.printStackTrace();
            }

        }
    }
    /**
     * Saves only if there is some data
     */
    public void saveCacheToDisk(String endpoint) {
        if (useHistoricalCache == false){ //If we do not load the historical cache, it is better not allow to save it.
            return;
        }
        //Save the cache to disk
        if (cache == null) {
            return;
        }
        if (cache.size() > 0) {
            try {
                FileOutputStream fos = new FileOutputStream(getCacheFileName(endpoint));
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                //Writes a serialized version of the ResultSet
                boolean hasOOMError = false;
                Map<String, String> mapSer = new HashMap<>();
                for (Map.Entry<String, ResultSetRewindable> entry : cache.entrySet()){
                    String    sparqlQuery   = entry.getKey();
                    ResultSetRewindable res = entry.getValue();
                    res.reset(); //If the iterator is finished will not be written
                    ByteArrayOutputStream bos = null;
                    try {
                        bos = new ByteArrayOutputStream(5 * //Estimation: 5 chars per string
                                res.size() * res.getResultVars().size());
                        ResultSetFormatter.output(bos, res, format); //Writes a serialization of ResultSet in os
                        String serialization = bos.toString();  //Here we have had out of memory :-S
                        mapSer.put(sparqlQuery, serialization);
                    } catch (OutOfMemoryError oome) {
                        //We have not written the conflictive data to the cache, but we can write the cache
                        hasOOMError = true;
                    }
                    finally {
                        bos.close();  //I think it is not mandatory because it implements Autocloseable
                    }
                }
                oos.writeObject(mapSer); //Writes the serialized version in any case
                oos.close();
                if (hasOOMError == true) {   //We have caught a out of memory Error.
                    //Do not write the file
                    throw new OutOfMemoryError(); //Continue the OOM process
                }
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
        dumpCacheinDiskSpecificFile(getCacheFileName(endpoint));
    }

    /**
     * Reads the specified cache fileName and dumps information in System.out
     * @param otherFileName The name of the file to analyze
     */
    static public void dumpCacheinDiskSpecificFile(String otherFileName) {
        JenaExecutorCacheSelect jecs = new JenaExecutorCacheSelect();
        File f = new File(otherFileName);
        if (f.isFile() && f.canRead()) { // If there is a cache file... load it.
            long start = System.currentTimeMillis();
            jecs.readCacheFromDiskSpecificFile(otherFileName);
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
        dumpCacheinDiskSpecificFile("cacheSelect.20160804.v2.ser");
    }
    static public void main2 (String[] args) {
        String ep =    "http://es.dbpedia.org/sparql";
        dumpCacheinDisk(ep);
    }
    static public void main3 (String[] args) {
        JenaExecutorCacheSelect cacheSelect = new JenaExecutorCacheSelect();
        String ep =    "http://es.dbpedia.org/sparql";
        ResultSet res1 = cacheSelect.executeWithCache(ep,
                "SELECT DISTINCT * WHERE { ?x ?P2 ?I2 ; a <http://lod.springer.com/data/ontology/class/Conference> ; <http://lod.springer.com/data/ontology/property/confCity> ?I1 OPTIONAL { { ?I1 <http://www.w3.org/2000/01/rdf-schema#label> ?l } UNION { ?I1 <http://lod.springer.com/data/ontology/property/confName> ?l } UNION { ?I1 <http://lod.springer.com/data/ontology/property/confAcronym> ?l }} }"
        );

        cacheSelect.interactiveExplorer();
        cacheSelect.saveCacheToDisk(ep);
        String fileName =    "es.dbpedia.org.cacheSelect.ser";
        interactiveExplorerForCacheinDiskSpecificFile(fileName);

    }

    static ResultSetRewindable extractiveExecSelect(String ep, String sparqlQuery, int limit, int milis, Config.ExtractionMode em){
        //PAY ATTENTION. We need ORDER BY in order to get a predictable result by OFFSET blocks
        //WARNING!!Additionally, there is a limit or 40.000 results for a ORDER BY query. You can cross this limit with
        //something that Virtuoso people name "subquering". See http://virtuoso.openlinksw.com/dataspace/doc/dav/wiki/Main/VirtTipsAndTricksHowToHandleBandwidthLimitExceed
        //Perhaps this is a virtuoso SPECIFIC solution and perhaps it is NOT generic for other EPs.

        Query qjena = QueryFactory.create(sparqlQuery);
        String var1 = qjena.getResultVars().get(0); //We get the first result var. We could think about which would be the better
        String qOrderBy = sparqlQuery + " ORDER BY ?" + var1;

        QueryExecution qe = QueryExecutionFactory.sparqlService(ep, qOrderBy);
        ResultSet           res   = qe.execSelect();
        ResultSetRewindable resok = ResultSetFactory.copyResults(res); //res is unusable from now on, use resok

        if (em == intensiveExtraction && resok.size() == limit){ //We assume that this implies that this is an extractive query
            ResultSet           resExtra   = null;
            ResultSetRewindable resExtraok = null;
            ResultSetMem        resSum     = null;

            System.out.print("Doing an extractive query. This is the query:\n"+ sparqlQuery +"\n.");
            StringBuilder varsPrefixPart = new StringBuilder();
            for (String v : qjena.getResultVars()){
                varsPrefixPart.append("?" + v + " ");
            }
            String extractivePrefix = "SELECT " + varsPrefixPart.toString() + "WHERE {" + qOrderBy + "}";
            int index = 1;
            boolean gotMaxResults = true;
            while (gotMaxResults == true){
                String qExtractive = extractivePrefix + " OFFSET "+ index * limit +" LIMIT " + index * limit;
                try {
                    resExtra = QueryExecutionFactory.sparqlService(ep, qExtractive).execSelect();
                }catch(QueryExceptionHTTP eqe) { //E.g. the query is very frequent OR requires too much to solve (HttpException: 500)
                    System.out.println("Size = " + resok.size() + ". We are stressing the EP...");
                    try {
                        System.out.println("Sleep for " + milis + " millis...");
                        Thread.sleep(milis);
                        continue;
                    }catch (InterruptedException ie){
                        System.out.println("Can not sleep. This is the end, sorry :-(.");
                        System.out.flush();
                        return null;
                    }
                }
                resExtraok = ResultSetFactory.copyResults(resExtra); //resExtra is unusable from now on, use resExtraok
                resSum = new ResultSetMem(resok, resExtraok);
                resok = resSum;
                if (resok.size() == (index + 1) * limit){ //If we get again the maximum number of results
                    //gotMaxResults keeps true
                    System.out.print(".");
                }else{
                    gotMaxResults = false;
                    System.out.println("");
                    break; //leaves the while
                }
                index++;
            }
        }
        return resok;
    }


    static public void main4 (String[] args) {
        String qBase = "SELECT DISTINCT * WHERE { ?I <http://dbpedia.org/ontology/creator> ?x OPTIONAL { ?I <http://www.w3.org/2000/01/rdf-schema#label> ?l } }";
        String ep = "http://dbpedia.org/sparql";


        ResultSetRewindable rs = extractiveExecSelect(ep, qBase, 10000, 100, intensiveExtraction);
        System.out.println("Size = " + rs.size());

    }

    static public void main (String[] args) {
        String fileName =    "dbpedia.org.cacheSelect.ser";
        interactiveExplorerForCacheinDiskSpecificFile(fileName);
    }

    static public void main5 (String[] args){
        String qBase = "SELECT DISTINCT * WHERE { ?I <http://dbpedia.org/ontology/creator> ?x OPTIONAL { ?I <http://www.w3.org/2000/01/rdf-schema#label> ?l } }";
        String ep = "http://dbpedia.org/sparql";
        //We need ORDER BY in order to get a predictable result by OFFSET blocks
        String qOrderBy = qBase + " ORDER BY ?I";
        Query query1 = QueryFactory.create(qOrderBy);
        QueryExecution qexec1 = QueryExecutionFactory.sparqlService(ep, query1);
        ResultSet res1 = qexec1.execSelect();

        String qNext = qOrderBy + " OFFSET 10000 LIMIT 10000";
        Query query2 = QueryFactory.create(qNext);
        QueryExecution qexec2 = QueryExecutionFactory.sparqlService(ep, query2);
        ResultSet res2 = qexec2.execSelect();

        ResultSetMem rs = new ResultSetMem(res1, res2);
    }

    static public void main6 (String[] args){
        String qBase = "SELECT DISTINCT * WHERE { ?I <http://dbpedia.org/ontology/creator> ?x OPTIONAL { ?I <http://www.w3.org/2000/01/rdf-schema#label> ?l } }";
        String ep = "http://dbpedia.org/sparql";
        //We need ORDER BY in order to get a predictable result by OFFSET blocks
        String qOrderBy = qBase + " ORDER BY ?I";
        Query query1 = QueryFactory.create(qOrderBy);
        QueryExecution qexec1 = QueryExecutionFactory.sparqlService(ep, query1);
        ResultSet res1 = qexec1.execSelect();
        ResultSetRewindable res1ok = ResultSetFactory.copyResults(res1);

        String qNext = qOrderBy + " OFFSET 10000 LIMIT 10000";
        Query query2 = QueryFactory.create(qNext);
        QueryExecution qexec2 = QueryExecutionFactory.sparqlService(ep, query2);
        ResultSet res2 = qexec2.execSelect();
        ResultSetRewindable res2ok = ResultSetFactory.copyResults(res2);

        ResultSetMem rs = new ResultSetMem(res1, res2);
        ResultSetMem rsok = new ResultSetMem(res1ok, res2ok);
    }
    static public void main7 (String[] args) throws Exception {
        FileOutputStream fos = new FileOutputStream("test");
        fos.write("test text".getBytes());
        fos.close();
    }

}
