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
public class JenaExecutorCacheSelect{
    static private Map<String, ResultSet> cache = null;
    static private Boolean isFirstTime = true;
    static private String fileName = "cacheSelect.ser";

    public ResultSet executeWithCache(String endpoint, String sparqlQuery) {
        ResultSet res = null;

        if (isFirstTime){
            //Checks if there is a cache serialization in the file system
            File f = new File(fileName);
            if (f.isFile() && f.canRead()) { // If there is a cache file... load it.
                try {
                    FileInputStream fis = new FileInputStream(fileName);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    cache = (Map<String, ResultSet>) ois.readObject();
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
            res = cache.get(sparqlQuery);         //get the results from the cache
        } else {                               //the sparqlQuery is NOT in the cache
            QueryExecution ex =
                  QueryExecutionFactory.sparqlService(endpoint, sparqlQuery);

            res =  ex.execSelect();               //Make the query to the endpoint
            cache.put(sparqlQuery, res);         //And store the information in the cache
            System.out.println("New element stored in CacheSelect (in memory). Now it has " + cache.size() + " elements.");
            //Save the cache to disk
//            try {
//                FileOutputStream fos = new FileOutputStream(fileName);
//                ObjectOutputStream oos = new ObjectOutputStream(fos);
//                oos.writeObject(cache);
//                oos.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }catch (IOException e) { //java.io.NotSerializableException: org.apache.jena.sparql.engine.ResultSetCheckCondition
//                e.printStackTrace();
//            }
        }

        return res;
    }

    static public void main (String[] args) {
        JenaExecutorCacheSelect cacheSelect = new JenaExecutorCacheSelect();
        ResultSet res1 = cacheSelect.executeWithCache("http://es.dbpedia.org/sparql",
                                                      "SELECT DISTINCT ?x{  ?subject <http://lod.springer.com/data/ontology/property/confCountry> ?x . }"
                                                           );
        ResultSet res2 = cacheSelect.executeWithCache("http://es.dbpedia.org/sparql",
                                                      "SELECT DISTINCT ?x{  ?subject <http://lod.springer.com/data/ontology/property/confCountry> ?x . }"
        );
    }
}
