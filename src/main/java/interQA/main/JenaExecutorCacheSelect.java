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
public class JenaExecutorCacheSelect implements Serializable{
    static private Map<String, QueryExecution> cache = null;
    static HashMap<String, ArrayList<String>> map = new HashMap<>();
    static private Boolean isFirstTime = true;
    static private String fileName = "cacheAsk.ser";

    public QueryExecution executeWithCache(String endpoint, String sparqlQuery) {
        QueryExecution ex = null;
        ArrayList<String> list = null;

        if (isFirstTime){
            //Checks if there is a cache serialization in the file system
            File f = new File(fileName);
            if (f.isFile() && f.canRead()) { // If there is a cache file... load it.
                try {
                    FileInputStream fis = new FileInputStream(fileName);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    cache = (Map<String, QueryExecution>) ois.readObject();
                    isFirstTime = true;
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
        }

        //We use the cache
        if (cache.containsKey(sparqlQuery)) { //the sparqlQuery is in the cache
            ex = cache.get(sparqlQuery);         //get the results from the cache
        } else {                               //the sparqlQuery is NOT in the cache
            ex =                                 //Make the query to the endpoint
                    QueryExecutionFactory.sparqlService(endpoint, sparqlQuery);

            //Check the initial word in the SPARQL query
            if (sparqlQuery.startsWith("ASK")){
                boolean satisfiesCondition = ex.execAsk();
                String boolText = String.valueOf(satisfiesCondition);
                if (map.containsKey(sparqlQuery)){
                    list = map.get(sparqlQuery);
                    if (!list.contains(boolText)){
                        list.add(boolText);
                    }
                }else {
                    list = new ArrayList<String>();
                    list.add(boolText);
                    map.put(sparqlQuery, list);
                }
            }
            /*if (sparqlQuery.startsWith("SELECT")){
                ResultSet results = ex.execSelect();
                while(results.hasNext()) {
                    QuerySolution result = results.nextSolution();
                    result.
                    if (map.containsKey(sparqlQuery)) {
                        list = map.get(sparqlQuery);
                        if (!list.contains(boolText)) {
                            list.add(boolText);
                        }
                    } else {

                        list = new ArrayList<String>();
                        list.add(boolText);
                        map.put(sparqlQuery, list);
                    }
                }
            }*/


            cache.put(sparqlQuery, ex);         //And store the information in the cache
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

        return ex;
    }

    static public void main (String[] args) {
//        JenaExecutorCacheSelect cacheAsk = new JenaExecutorCacheSelect();
//        boolean satisfiesCondition = cacheAsk.executeWithCache("http://es.dbpedia.org/",
//                                                            "ASK WHERE { { <http://lod.springer.com/data/ontology/property/confStartDate> <http://www.w3.org/2000/01/rdf-schema#range> <http://lod.springer.com/data/ontology/class/Conference> . } UNION { <http://lod.springer.com/data/ontology/property/confStartDate> <http://www.w3.org/2000/01/rdf-schema#range> ?range .  <http://lod.springer.com/data/ontology/class/Conference> <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?range . } }"
//                                                           );
    }
}
