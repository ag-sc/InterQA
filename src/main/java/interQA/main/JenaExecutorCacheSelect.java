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
                try {
                    FileInputStream fis = new FileInputStream(fileName);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    cache = (Map<String, String>) ois.readObject();
                } catch (FileNotFoundException fnfe) {
                    fnfe.printStackTrace();
                } catch (IOException ioe){ //i
                    ioe.printStackTrace();
                } catch (ClassNotFoundException cnfe){
                    cnfe.printStackTrace();
                }
            }else{  //There is no cache file
                //We use the static object cache
                cache = new HashMap<>();
            }
            isFirstTime = false;
        }

        //We use the cache
        if (cache.containsKey(sparqlQuery)) { //the sparqlQuery is in the cache
            resSer = cache.get(sparqlQuery);         //get the results from the cache
            InputStream stream = new ByteArrayInputStream(resSer.getBytes(StandardCharsets.UTF_8)); //How heavy it it?
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
            ps.println(cache.size());
            cache.forEach((k, v) -> {  //lambda expression --> requires java 1.8
                        //Traditional way: Arrays.toString(cache.entrySet().toArray())
                        ps.println("   query = " + k);
                        ps.println("    \\--> ResultSet XML serialization = " + v);
                    }
            );
        }
    }
    static public void main (String[] args) {
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
}
