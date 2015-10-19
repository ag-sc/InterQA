package interQA.lexicon;

import java.util.ArrayList;
import java.util.HashMap;
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
        
    
    public InstanceSource(String url) {
        
        endpoint = url;
    }
    
    public Map<String,List<LexicalEntry>> getInstanceIndex(String query, String var_uri, String var_label) {
       
        Map<String,List<LexicalEntry>> instances = new HashMap<>();
        
        QueryExecution ex = QueryExecutionFactory.sparqlService(endpoint,query);
        ResultSet results = ex.execSelect();
        
        while (results.hasNext()) {
            
               QuerySolution result = results.nextSolution();
               
               RDFNode uri   = result.get(var_uri);
               RDFNode label = result.get(var_label); 
               
               if (uri != null && label != null) {
                   
                   String form = label.asLiteral().getValue().toString();
                                      
                   LexicalEntry entry = new LexicalEntry();
                   entry.setCanonicalForm(form);
                   entry.setReference(uri.toString());
                   
                   if (!instances.containsKey(form)) {
                        instances.put(form,new ArrayList<>());
                   }
                   instances.get(form).add(entry);
               }
        }
        
        // For testing:
        
//        LexicalEntry entry = new LexicalEntry();
//        entry.setCanonicalForm("fnord");
//        entry.setReference("http://fnord.org/Fnord");
//        List<LexicalEntry> entries = new ArrayList<>();
//        entries.add(entry);
//        instances.put("fnord",entries);
        
        return instances;
    }
    
}
