package interQA.lexicon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author cunger
 */
public class InstanceSource {
    
    String endpoint;
    
    Vocabulary vocab = new Vocabulary();
        
    
    public InstanceSource(String url) {
        
        endpoint = url;
    }
    
    public Map<String,List<LexicalEntry>> getInstanceIndex(String query, String var_uri, String var_label) {
       
        Map<String,List<LexicalEntry>> instances = new HashMap<>();
        
        // TODO Debug!

//        QueryExecution ex = QueryExecutionFactory.sparqlService(endpoint,query);
//        ResultSet results = ex.execSelect();
//        
//        while (results.hasNext()) {
//            
//               // TODO debug, no results are returned
//            
//               QuerySolution result = results.nextSolution();
//               
//               RDFNode uri   = result.get(var_uri);
//               RDFNode label = result.get(var_label); 
//               
//               if (uri != null && label != null) {
//                   
//                   String form = label.asLiteral().getValue().toString();
//                                      
//                   LexicalEntry entry = new LexicalEntry();
//                   entry.setCanonicalForm(form);
//                   entry.setReference(uri.toString());
//                   
//                   if (!instances.containsKey(form)) {
//                        instances.put(form,new ArrayList<>());
//                   }
//                   instances.get(form).add(entry);
//               }
//        }
        
        // For testing:
        
        LexicalEntry entry = new LexicalEntry();
        entry.setCanonicalForm("fnord");
        entry.setReference("http://fnord.org/Fnord");
        List<LexicalEntry> entries = new ArrayList<>();
        entries.add(entry);
        instances.put("fnord",entries);
        
        return instances;
    }
    
    private String domainQuery(String domain, String property) {
        
        // TODO This is most probably too strict.
        return "ASK WHERE { { <"+property+"> <" + vocab.rdfs + "domain> <" + domain + "> . }"
                  + " UNION { <"+property+"> <" + vocab.rdfs + "domain> ?domain . "
                          + " <"+ domain +"> <" + vocab.rdfs + "subClassOf> ?domain . } }";  
    }
    
    private String rangeQuery(String range, String property) {
        
        // TODO This is most probably too strict.
        return "ASK WHERE { { <"+property+"> <" + vocab.rdfs + "range> <" + range + "> . }"
                  + " UNION { <"+property+"> <" + vocab.rdfs + "range> ?range . "
                          + " <"+ range +"> <" + vocab.rdfs + "subClassOf> ?range . } }";  
    }
    
    public Map<String,List<LexicalEntry>> filterBy(Map<String,List<LexicalEntry>> index, LexicalEntry.SynArg syn, String uri) {
        
        Map<String,List<LexicalEntry>> filtered_index = new HashMap<>();
                
        String query;
        
        Iterator iter = index.entrySet().iterator();
        while (iter.hasNext()) {
               Map.Entry pair = (Map.Entry) iter.next();
               String form = (String) pair.getKey();
               List<LexicalEntry> entries = (List<LexicalEntry>) pair.getValue();
               List<LexicalEntry> filtered_entries = new ArrayList<>();
               
               for (LexicalEntry entry : entries) {
                    
                    switch (entry.getSemArg(syn)) {
                        case SUBJOFPROP: 
                             query = domainQuery(uri,entry.getReference());
                             break;
                        case OBJOFPROP:
                             query = rangeQuery(uri,entry.getReference());
                             break;
                        default: 
                             continue;
                    }                  
                   
                    QueryExecution ex = QueryExecutionFactory.sparqlService(endpoint,query);
                    boolean satisfiesCondition = ex.execAsk();
        
                    if (satisfiesCondition) filtered_entries.add(entry);
               }
               
               if (!filtered_entries.isEmpty()) {
                    filtered_index.put(form,filtered_entries);
               }
        }
        
        return filtered_index;
    }
}
