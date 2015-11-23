package interQA.lexicon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;

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
    //to confirm property of class at domain position 
    private String domainQueryForCTP(String domain, String property) {
        
        // TODO This is most probably too strict.
        return "ASK WHERE { { <"+property+"> <" + vocab.rdfs + "domain> <" + domain + "> . }"
                  + " UNION { <"+property+"> <" + vocab.rdfs + "domain> ?domain . "
                          + " <"+ domain +"> <" + vocab.rdfs + "subClassOf> ?domain . } }";  
    }
    //to confirm property of class at range position
    private String rangeQueryForCTP(String range, String property) {
        
        // TODO This is most probably too strict.
        return "ASK WHERE { { <"+property+"> <" + vocab.rdfs + "range> <" + range + "> . }"
                  + " UNION { <"+property+"> <" + vocab.rdfs + "range> ?range . "
                          + " <"+ range +"> <" + vocab.rdfs + "subClassOf> ?range . } }";  
    }
    
    
    
    //to confirm the latter property (w.r.t the former property) of class at domain position (May be the former method can be implemented for
    //for this method too) 
    private String domainQueryForPTP(String latterproperty, String formerproperty){
    	return "ASK WHERE { {  ?subject <"+latterproperty+"> ?object1 . "
    						+ " ?subject <"+formerproperty+"> ?object2 . }"
    								+ " UNION { ?subject <"+latterproperty+"> ?object1 . "
    											+ "?object2 <"+formerproperty+"> ?subject .} "
    								+ " UNION { ?object1 <"+latterproperty+"> ?subject . "
    											+ "?subject <"+formerproperty+"> ?object2. } }";
    	// ?domaın p1 ?obj 
    	// ?obj p2 ... 
    }
    //to confirm the latter property (w.r.t the former property) of class at range position  
    private String rangeQueryForPTP(String latterproperty, String formerproperty){
    	return "ASK WHERE {  ?subject1 <"+latterproperty+"> ?object . "
    					 + " ?subject2 <"+formerproperty+"> ?object . }"
    					 		+ " UNION { ?subject1 <"+latterproperty+"> ?object . "
    					 				 + "?object <"+formerproperty+"> ?subject2 . } "
    					 		+ " UNION { ?object <"+latterproperty+"> ?subject1 . "
    					 				+ "?subject2 <"+formerproperty+" ?object. } }";
    }
    public Map<String,List<LexicalEntry>> filterByClassForProperty(Map<String,List<LexicalEntry>> index, LexicalEntry.SynArg syn, String uri) {
        
        Map<String,List<LexicalEntry>> filtered_index = new HashMap<>();
                
        String query;
        
        Iterator iter = index.entrySet().iterator();
        while (iter.hasNext()) {
               Map.Entry pair = (Map.Entry) iter.next();
               String form = (String) pair.getKey();
               List<LexicalEntry> entries = (List<LexicalEntry>) pair.getValue();
               List<LexicalEntry> filtered_entries = new ArrayList<>();
               
               for (LexicalEntry entry : entries) {
                    
            	   if (entry.getSemArg(syn)== null) continue; // TODO cunger: Look at why this happens!
                    switch (entry.getSemArg(syn)) {
                        case SUBJOFPROP: 
                             query = domainQueryForCTP(uri,entry.getReference());
                             break;
                        case OBJOFPROP:
                             query = rangeQueryForCTP(uri,entry.getReference());
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
    
    public Map<String,List<LexicalEntry>> filterByPropertyForProperty(Map<String,List<LexicalEntry>> index, LexicalEntry.SynArg syn, String uri){
    	
    	Map<String,List<LexicalEntry>> filtered_index = new HashMap<>();
    	
    	String query ="";
    	
    	Iterator iter = index.entrySet().iterator();
    	while(iter.hasNext()){
    		Map.Entry pair = (Map.Entry) iter.next();
    		String form = (String) pair.getKey();
    		List<LexicalEntry> entries = (List<LexicalEntry>) pair.getValue();
    		List<LexicalEntry> filtered_entries = new ArrayList<>();
    		
    		for (LexicalEntry entry : entries){
    			
    			switch(entry.getSemArg(syn)){
    				case SUBJOFPROP:
    					query = domainQueryForPTP(uri,entry.getReference());
    				case OBJOFPROP:
    					query = rangeQueryForPTP(uri,entry.getReference());
    					break;
    				default:
    					break;
    			
    			}
    			
    				QueryExecution ex = QueryExecutionFactory.sparqlService(endpoint, query);
    				boolean satisfiesCondition = ex.execAsk();
    				
    				if(satisfiesCondition) filtered_entries.add(entry);    				
    		}
    			if(!filtered_entries.isEmpty()){
    				filtered_index.put(form,filtered_entries);
    			}
    	}
    	
    	return filtered_index;
    }
    
}
