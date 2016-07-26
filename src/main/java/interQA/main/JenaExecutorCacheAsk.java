package interQA.main;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mariano on 21/07/2016.
 */
public class JenaExecutorCacheAsk{
    static private Map<String, Boolean> cache = null;

    static private Boolean isFirstTime = true;
    static private String fileName = "cacheAsk.ser";

    public Boolean executeWithCache(String endpoint, String sparqlQuery) {
        Boolean satisfiesCondition = null;
        ArrayList<String> list = null;

        if (isFirstTime){
            //Checks if there is a cache serialization in the file system
            File f = new File(fileName);
            if (f.isFile() && f.canRead()) { // If there is a cache file... load it.
                try {
                    FileInputStream fis = new FileInputStream(fileName);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    cache = (Map<String, Boolean>) ois.readObject();
                    System.out.println("Loaded cache file " + fileName + " with " + cache.size() + " elements.");
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
            satisfiesCondition = cache.get(sparqlQuery);         //get the results from the cache
        } else {                               //the sparqlQuery is NOT in the cache
            QueryExecution ex =                      //Make the query to the endpoint
                    QueryExecutionFactory.sparqlService(endpoint, sparqlQuery);
            satisfiesCondition = ex.execAsk();
            cache.put(sparqlQuery, satisfiesCondition);         //And store the information in the cache
            //Save the cache to disk
            try {
                FileOutputStream fos = new FileOutputStream(fileName);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(cache);
                oos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        return satisfiesCondition;
    }

    static public void main (String[] args) {
        JenaExecutorCacheAsk cacheAsk = new JenaExecutorCacheAsk();
        boolean satisfiesCondition1 = cacheAsk.executeWithCache("http://es.dbpedia.org/sparql",
                                                            "ASK WHERE { { <http://lod.springer.com/data/ontology/property/confStartDate> <http://www.w3.org/2000/01/rdf-schema#range> <http://lod.springer.com/data/ontology/class/Conference> . } UNION { <http://lod.springer.com/data/ontology/property/confStartDate> <http://www.w3.org/2000/01/rdf-schema#range> ?range .  <http://lod.springer.com/data/ontology/class/Conference> <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?range . } }"
                                                           );
        boolean satisfiesCondition2 = cacheAsk.executeWithCache("http://es.dbpedia.org/sparql",
                "ASK WHERE { { <http://lod.springer.com/data/ontology/property/confStartDate> <http://www.w3.org/2000/01/rdf-schema#range> <http://lod.springer.com/data/ontology/class/Conference> . } UNION { <http://lod.springer.com/data/ontology/property/confStartDate> <http://www.w3.org/2000/01/rdf-schema#range> ?range .  <http://lod.springer.com/data/ontology/class/Conference> <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?range . } }"
        );

    }
}
