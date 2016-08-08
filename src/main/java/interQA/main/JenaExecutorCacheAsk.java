package interQA.main;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mariano on 21/07/2016.
 */
public class JenaExecutorCacheAsk {

    private Map<String, CacheAskQueryInfo> cache = null;

    private Boolean isFirstTime = true;
    static private final String fileNameTail = "cacheAsk.ser";

    public Boolean executeWithCache(String endpoint, String sparqlQuery) {
        CacheAskQueryInfo qi = null;
        Boolean satisfiesCondition;

        if (isFirstTime) {
            //Checks if there is a cache serialization in the file system
            File f = new File(getCacheFileName(endpoint));
            if (f.isFile() && f.canRead()) { // If there is a cache file... load it.
                readCacheFromDisk(endpoint);
            } else {  //There is no cache file
                //We use the static object cache
                cache = new HashMap<>();
            }
            isFirstTime = false;
        }

        //We use the cache
        if (cache.containsKey(sparqlQuery)) { //the sparqlQuery is in the cache
            qi = cache.get(sparqlQuery);         //get the results from the cache
            qi.increaseTimesUsed(); //Only increases when recovered from cache
            qi.updateTimeStamp();
            satisfiesCondition = qi.getRes();
        } else {                               //the sparqlQuery is NOT in the cache
            QueryExecution ex =                      //Make the query to the endpoint
                    QueryExecutionFactory.sparqlService(endpoint, sparqlQuery);
            satisfiesCondition = ex.execAsk();
            qi = new CacheAskQueryInfo(satisfiesCondition);
            cache.put(sparqlQuery, qi);         //And store the information in the cache
            //saveCacheToDisk(); Now its saved by a method invocation
        }

        return satisfiesCondition;
    }

    private void readCacheFromDisk(String endpoint) {

        try {
            String fileName = getCacheFileName(endpoint);
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            cache = (Map<String, CacheAskQueryInfo>) ois.readObject();
            System.out.println("Loaded cache file " + fileName + " with " + cache.size() + " elements.");
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) { //i
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }

    }
    private static String getCacheFileName(String endpoint){
        String epPart = endpoint.substring("http://".length(),
                                           endpoint.length() - "/sparql".length()); //Error prone (https or /sparql/)
        return (epPart + "." + fileNameTail);
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
                oos.writeObject(cache);
                oos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String cacheUsageReport() {
        return (cache == null ? "AskCache not initialized" : cache.size() + " ask queries used");
    }

    public void dump(PrintStream ps) {
        ps.println("   CacheAsk dump:");
        ps.println("   --------------");
        ps.print("   Number of elements: ");
        if (cache == null) {
            ps.println("0");
        } else {
            ps.println(cache.size());
            cache.forEach((k, v) -> {  //lambda expression --> requires java 1.8
                        //Traditional way: Arrays.toString(cache.entrySet().toArray())
                        ps.println("   query = " + k);
                        ps.println("    \\--> res = " + v.getRes());
                        ps.println("    |--> #used = " + v.getTimesUsed());
                        ps.println("    |--> timeStamp: Oldest = " + new Date(v.getOldestTimeStamp()) + ". Newest = " + new Date(v.getNewestTimeStamp()));
                    }
            );

        }
    }

    static public void dumpCacheinDisk(String endpoint) {
        Map<String, CacheAskQueryInfo> tempCache = null;
        JenaExecutorCacheAsk jeca = new JenaExecutorCacheAsk();
        String fileName = getCacheFileName(endpoint);
        File f = new File(fileName);
        if (f.isFile() && f.canRead()) { // If there is a cache file... load it.
            jeca.readCacheFromDisk(endpoint);
            jeca.dump(System.out);
        } else {
            System.out.println(fileName + "is not available.");
        }
    }

    static public void main(String[] args) {
        JenaExecutorCacheAsk cacheAsk = new JenaExecutorCacheAsk();
        String ep = "http://es.dbpedia.org/sparql";
        boolean satisfiesCondition1 = cacheAsk.executeWithCache(ep,
                "ASK WHERE { { <http://lod.springer.com/data/ontology/property/confStartDate> <http://www.w3.org/2000/01/rdf-schema#range> <http://lod.springer.com/data/ontology/class/Conference> . } UNION { <http://lod.springer.com/data/ontology/property/confStartDate> <http://www.w3.org/2000/01/rdf-schema#range> ?range .  <http://lod.springer.com/data/ontology/class/Conference> <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?range . } }"
        );
        cacheAsk.dump(System.out);
        boolean satisfiesCondition2 = cacheAsk.executeWithCache(ep,
                "ASK WHERE { { <http://lod.springer.com/data/ontology/property/confStartDate> <http://www.w3.org/2000/01/rdf-schema#range> <http://lod.springer.com/data/ontology/class/Conference> . } UNION { <http://lod.springer.com/data/ontology/property/confStartDate> <http://www.w3.org/2000/01/rdf-schema#range> ?range .  <http://lod.springer.com/data/ontology/class/Conference> <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?range . } }"
        );
        cacheAsk.dump(System.out);
        cacheAsk.saveCacheToDisk(ep);
        cacheAsk.dumpCacheinDisk(ep);


    }
}

    /**
     * This class can not be inner: the serialization of inner classes does not work :-S
     */
    class CacheAskQueryInfo implements Serializable {
        private Boolean res;
        private int timesUsed;
        private long oldestTimeStamp;
        private long newestTimeStamp;

        public CacheAskQueryInfo(Boolean res) {
            this.res = res;
            timesUsed = 0;
            oldestTimeStamp = System.currentTimeMillis();
            newestTimeStamp = oldestTimeStamp;
        }

        public Boolean getRes() {
            return res;
        }

        public int getTimesUsed() {
            return timesUsed;
        }

        public long getOldestTimeStamp() {
            return oldestTimeStamp;
        }

        public long getNewestTimeStamp() {
            return newestTimeStamp;
        }

        public void increaseTimesUsed() {
            timesUsed++;
        }

        public void updateTimeStamp() {
            newestTimeStamp = System.currentTimeMillis();
        }

    }

