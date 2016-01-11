package interQA.lexicon;

import java.util.ArrayList;
import java.util.Arrays;
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
// domainqueryPTI limit and offset set
public class InstanceSource {
    
    String endpoint;
    String lang ;
    Vocabulary vocab = new Vocabulary();
    List<String> labelProperties;

    public InstanceSource(String url,String language) {
        
       this(url,language,Arrays.asList("http://www.w3.org/2000/01/rdf-schema#label"));
    }
        
    public InstanceSource(String url,String language,List<String> props) {
        
        endpoint = url;
        lang = language;
        labelProperties = props;
    }
    
    private String label(String var1, String var2) {
        
        String out; 
        
        if (labelProperties.size() == 1) {
            out = var1 + " " + labelProperties.get(0) + " " + var2 + " .";
        }
        else if (labelProperties.size() > 1) {
            out = "{ " + var1 + " " + labelProperties.get(0) + " " + var2 + " . }";
            for (String prop : labelProperties.subList(1,labelProperties.size()-1)) {
                 out += " UNION { " + var1 + " " + prop + " " + var2 + " . }";
            }
        }
        else out = "";
        
        return out;
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
            	  
            	  
            		  String form = label.asLiteral().getValue().toString().replaceAll(" \\(.*?\\)", "");
                	  
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
//        
//        LexicalEntry entry = new LexicalEntry();
//        entry.setCanonicalForm("fnord");
//        entry.setReference("http://fnord.org/Fnord");
//        List<LexicalEntry> entries = new ArrayList<>();
//        entries.add(entry);
//        instances.put("fnord",entries);
        
        
        return instances;
    }
    //to confirm property of class at domain position (CTP = Class To Property)
    private String domainQueryForCTP(String domain, String property) {
        
        // TODO This is most probably too strict.
        return "ASK WHERE { { <"+property+"> <" + vocab.rdfs + "domain> <" + domain + "> . }"
                  + " UNION { <"+property+"> <" + vocab.rdfs + "domain> ?domain . "
                          + " <"+ domain +"> <" + vocab.rdfs + "subClassOf> ?domain . } }";  
    }
    //to confirm property of class at range position (CTP = Class To Property)
    private String rangeQueryForCTP(String range, String property) {
        
        // TODO This is most probably too strict.
        return "ASK WHERE { { <"+property+"> <" + vocab.rdfs + "range> <" + range + "> . }"
                  + " UNION { <"+property+"> <" + vocab.rdfs + "range> ?range . "
                          + " <"+ range +"> <" + vocab.rdfs + "subClassOf> ?range . } }";  
    }
    //to query instances(domain pos) that are suitable with property(Property to Instance)
    private String domainQueryForPTI(String property_uri){
    	
    	return "SELECT DISTINCT ?x ?l WHERE { "
                + " ?x <" + property_uri + "> ?object . "
                + label("?x","?l")
                + "filter langMatches( lang(?l), \""+lang+"\") }";
    }
    //to query instances(domain pos) that are suitable with property(Property to Instance)
    private String rangeQueryForPTI(String property_uri){
    	
        return "SELECT DISTINCT ?x ?l WHERE { "
               + " ?subject <" + property_uri + "> ?x . "
               + label("?x","?l")
               + "filter langMatches( lang(?l), \""+lang+"\") }";
    }
    //to query instances for the case; both property share same domain
    private String domainQueryFor2PTI(String property_uri1,String property_uri2){
    	   	
    	return "SELECT DISTINCT ?x ?l WHERE { "
		+ " ?x <" + property_uri1 + "> ?object1 . "
		+ " ?x <" + property_uri2 +"> ?object2"
		+ label("?x","?l")
		+ "filter langMatches( lang(?l), \""+lang+"\") }";
    }
    //to query instances for the case; both property share same range
    private String rangeQueryFor2PTI(String property_uri1,String property_uri2){
    	   	
    	return "SELECT DISTINCT ?x ?l WHERE { "
    		+ " ?subject1 <"+ property_uri1 +"> ?x ."
                + " ?subject2 <" + property_uri2 + "> ?x . "
		+ label("?x","?l")
		+ "filter langMatches( lang(?l), \""+lang+"\")}";
    }
    //to confirm the latter property (w.r.t the former property) of class at domain position (May be the former method can be implemented for
    //for this method too) 
    private String domainQueryForPTP(String latterproperty, String formerproperty){
    	return "ASK WHERE { {  ?subject  <"+latterproperty+">  ?object1 . "
    						+ " ?subject  <"+formerproperty+">  ?object2 . }"
    								+ " UNION { ?subject  <"+latterproperty+">  ?object1 . "
    											+ "?object2  <"+formerproperty+">  ?subject .} "
    								+ " UNION { ?object1  <"+latterproperty+">  ?subject . "
    											+ "?subject  <"+formerproperty+">  ?object2. } }";
    	// ?domaın p1 ?obj 
    	// ?obj p2 ... 
    }
    //to confirm the latter property (w.r.t the former property) of class at range position  
    private String rangeQueryForPTP(String latterproperty, String formerproperty){
    	return "ASK WHERE {  ?subject1  <"+latterproperty+">  ?object . "
    					 + " ?subject2  <"+formerproperty+">  ?object . }"
    					 		+ " UNION { ?subject1  <"+latterproperty+">  ?object . "
    					 				 + "?object  <"+formerproperty+">  ?subject2 . } "
    					 		+ " UNION { ?object  <"+latterproperty+">  ?subject1 . "
    					 				+ "?subject2  <"+formerproperty+">  ?object. } }";
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
    					break;
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
    
    public Map<String,List<LexicalEntry>> filterByPropertyForInstances(List<LexicalEntry> entries, LexicalEntry.SynArg syn){
    	
    	Map<String,List<LexicalEntry>> filtered_instances_index = new HashMap<>();
    		
    		for(LexicalEntry entry : entries){
    			String query;
    			switch(entry.getSemArg(syn)){
    				
    				case SUBJOFPROP:
    				    query = domainQueryForPTI(entry.getReference());
    				    
    				    filtered_instances_index.putAll(getInstanceIndex(query, "?x", "?l"));
    					break;
    				case OBJOFPROP:
    					query= rangeQueryForPTI(entry.getReference());
    					filtered_instances_index.putAll(getInstanceIndex(query, "?x", "?l"));
    					break;
    				default:
    					break;		
    			}
    			
    			
    		}
    	
    	
    	return filtered_instances_index;
    }

    public Map<String,List<LexicalEntry>> filterBy2PropertiesForInstances(List<LexicalEntry> entriesForPre1, List<LexicalEntry> entriesForPre2, LexicalEntry.SynArg syn ){
    	Map<String,List<LexicalEntry>> filtered_instances_index = new HashMap<>();
		
		for(LexicalEntry entry1 : entriesForPre1){
		for(LexicalEntry entry2: entriesForPre2){
			String query;
			switch(entry1.getSemArg(syn)){
			//TODO mince: The position of instance may vary depending on property (think on all cases) !! 
			// instance for first property may locate at subject and for second property at domain 
				case SUBJOFPROP:
				    query = domainQueryFor2PTI(entry1.getReference(),entry2.getReference());
				    
				    filtered_instances_index.putAll(getInstanceIndex(query, "?x", "?l"));
					break;
				case OBJOFPROP:
					query= rangeQueryFor2PTI(entry1.getReference(),entry2.getReference());
					filtered_instances_index.putAll(getInstanceIndex(query, "?x", "?l"));
					break;
				default:
					break;		
			}
		}
			
		}
	
	
	return filtered_instances_index;
    	
    }
 	
    

}
