package interQA.main;

import org.apache.jena.query.*;
import org.apache.jena.riot.ResultSetMgr;
import org.apache.jena.sparql.resultset.ResultsFormat;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mariano on 21/07/2016.
 */
public class JenaExecutorCacheSelect{
    private Map<String, String> cache = null;
    private Boolean isFirstTime = true;
    static private final String fileName = "cacheSelect.ser";

    public ResultSet executeWithCache(String endpoint, String sparqlQuery) {
        String resSer = null;
        ResultSet res = null;

        if (isFirstTime){
            //Checks if there is a cache serialization in the file system
            File f = new File(fileName);
            if (f.isFile() && f.canRead()) { // If there is a cache file... load it.
                readCacheFromDisk();
            }else{  //There is no cache file
                //We use the static object cache
                cache = new HashMap<>();
            }
            isFirstTime = false;
        }

        //We use the cache
        if (cache.containsKey(sparqlQuery)) { //the sparqlQuery is in the cache
            resSer = cache.get(sparqlQuery);         //get the results from the cache
            InputStream stream = new ByteArrayInputStream(resSer.getBytes(StandardCharsets.UTF_8)); //How heavy is it?
            res = ResultSetFactory.load(stream, ResultsFormat.FMT_RS_XML);
        } else {                               //the sparqlQuery is NOT in the cache
            QueryExecution ex =
                  QueryExecutionFactory.sparqlService(endpoint, sparqlQuery);

            res =  ex.execSelect();               //Make the query to the endpoint
            resSer = ResultSetFormatter.asXMLString(res);
            cache.put(sparqlQuery, resSer);         //And store the information in the cache
            System.out.println("New element stored in CacheSelect (in memory). Now it has " + cache.size() + " elements.");
            //Save the cache to disk
            //saveCacheToDisk(); Now it is a method
        }

        return res;
    }
    private void readCacheFromDisk() {
        readCacheFromDisk(fileName);
    }
    private void readCacheFromDisk(String otherFileName) {

        try {
            FileInputStream fis = new FileInputStream(otherFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            cache = (Map<String, String>) ois.readObject();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe){ //i
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe){
            cnfe.printStackTrace();
        }

    }
    /**
     * Saves only if there is some data
     */
    public void saveCacheToDisk() {
        //Save the cache to disk
        if (cache == null) {
            return;
        }
        if (cache.size() > 0) {
            try {
                FileOutputStream fos = new FileOutputStream(fileName);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(cache);
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
            boolean sizeIslowerThanLimit = true;
            for (Map.Entry<String, String> entry : cache.entrySet()){
                ps.println("   query (" + counter++ + "/" + size + ")= " + entry.getKey());
                ps.println("    \\--> ResultSet XML serialization = " + entry.getValue());
                if (counter > limit){
                    sizeIslowerThanLimit = false;
                    break;
                }
            }
            if (sizeIslowerThanLimit == false){
                ps.println("... and many more.");
            }
//            cache.forEach((k, v) -> {  //lambda expression --> requires java 1.8
//                        //Traditional way: Arrays.toString(cache.entrySet().toArray())
//                        ps.println("   query = " + k);
//                        ps.println("    \\--> ResultSet XML serialization = " + v);
//                    }
//            );
        }
    }
    /**
     * Reads the DEFAULT cache fileName and dumps information in System.out
     */
    static public void dumpCacheinDisk() {
        dumpCacheinDisk(fileName);
    }

    /**
     * Reads the specified cache fileName and dumps information in System.out
     * @param otherFileName The name of the file to analyze
     */
    static public void dumpCacheinDisk(String otherFileName) {
        JenaExecutorCacheSelect jecs = new JenaExecutorCacheSelect();
        File f = new File(otherFileName);
        if (f.isFile() && f.canRead()) { // If there is a cache file... load it.
            long start = System.currentTimeMillis();
            jecs.readCacheFromDisk(otherFileName);
            long lapse = System.currentTimeMillis() - start;
            //jecs.dump(System.out);
            System.out.println("Usage report for cache file "+ otherFileName + ": ");
            System.out.println("  Time (milisecs) required to load the cache file: "+ lapse);
            long sizebytes  = f.length();
            System.out.println("  File size: " + sizebytes + " bytes (~" + sizebytes/1024/1024 + "MB.)");
            System.out.println("  " + jecs.cacheUsageReport());
            jecs.dump(System.out);
        } else {
            System.out.println(otherFileName + "is not available.");
        }
    }

    static public void main1 (String[] args) {
        JenaExecutorCacheSelect cacheSelect = new JenaExecutorCacheSelect();
        ResultSet res1 = cacheSelect.executeWithCache("http://es.dbpedia.org/sparql",
                                                      "SELECT DISTINCT ?x{  ?subject <http://lod.springer.com/data/ontology/property/confCountry> ?x . }"
                                                           );
        ResultSet res2 = cacheSelect.executeWithCache("http://es.dbpedia.org/sparql",
                                                      "SELECT DISTINCT ?x{  ?subject <http://lod.springer.com/data/ontology/property/confCountry> ?x . }"
        );
        cacheSelect.dump(System.out);
        cacheSelect.saveCacheToDisk();
    }
    static public void main (String[] args) {
        dumpCacheinDisk("cacheSelect.20160804.v2.ser");
    }


}
