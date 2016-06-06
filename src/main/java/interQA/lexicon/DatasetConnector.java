package interQA.lexicon;

import interQA.lexicon.LexicalEntry.Language;
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

public class DatasetConnector {
    
    
    String endpoint;
    Vocabulary vocab = new Vocabulary();
    List<String> labelProperties;
    Language lang ;


    public DatasetConnector(String url,Language language) {
        
       this(url,language,Arrays.asList("http://www.w3.org/2000/01/rdf-schema#label"));
    }
        
    public DatasetConnector(String url, Language language, List<String> props) {
        
        endpoint = url;
        lang = language;
        labelProperties = props;
    }
    
    private String label(String var1, String var2) {
        
        String out;
        
        if (labelProperties.size() == 1) {
            out = var1 + " <" + labelProperties.get(0) + "> " + var2 + " .";
        }
        else if (labelProperties.size() > 1) {
            out = "{ " + var1 + " <" + labelProperties.get(0) + "> " + var2 + " . }";
            for (String prop : labelProperties.subList(1,labelProperties.size())) {
                 out += " UNION { " + var1 + " <" + prop + "> " + var2 + " . }";
            }
        }
        else out = "" ;
        
        return out;
    }
    

    
    public Map<String,List<LexicalEntry>> getInstanceIndex(String query, String var_uri) {
       
        Map<String,List<LexicalEntry>> instances = new HashMap<>();
        List<String> collected_IRI = new ArrayList<>();
        String query_first ="SELECT DISTINCT "+var_uri+"{ "+query+ "}";
        QueryExecution ex = QueryExecutionFactory.sparqlService(endpoint,query_first);
        ResultSet results = ex.execSelect();
        while (results.hasNext()) {
        	
        	  List<String> labels= new ArrayList<>(); 	
              QuerySolution result = results.nextSolution();  
              RDFNode uri   = result.get(var_uri);
              String form_IRI ="";
              if (uri != null ) {
		            	String form = uri.toString();
		  				
	  						
	  						
				  				if(form.matches(".*http://www.w3.org/2001/XMLSchema#gYear")){
				  					form =form.substring(form.indexOf('"') + 1, form.indexOf("-"));
				  					labels.add(form);
				  					form_IRI = "\""+form+"\"^^<http://www.w3.org/2001/XMLSchema#gYear>";
				  				}
				  				else if(form.matches("\\d{4}-\\d{2}-\\d{2}.*")){
				  					form = form.substring(form.indexOf('"') + 1, form.indexOf("+"));
				  					labels.add(form);
				  					form_IRI = "\""+form+"\"^^<http://www.w3.org/2001/XMLSchema#date>";
				  				}
				  				else if(form.matches("http://.*")){
				  					collected_IRI.add(form);
				  					}
                                                                else if(form.matches("https://.*")){
				  					collected_IRI.add(form);
				  					}
				  				else{
                                                                    
				  					form = form.substring(0,form.indexOf("@"));
				  					labels.add(form);
				  					form_IRI="\""+form+"\"@"+lang;	
				  					
				  				}
	  				
            	  }
	  			
              
              
              
                  
              for(String form :labels){
            	  
            	  LexicalEntry entry = new LexicalEntry();
                  entry.setCanonicalForm(form);
                  entry.setReference(form_IRI);
                 
                  if (!instances.containsKey(form)) {
                       instances.put(form,new ArrayList<>());
                 }
                  
                  instances.get(form).add(entry);
            	  
              }
              
              
            	  
        }
        
        if(!collected_IRI.isEmpty()){
        	
        	query = "SELECT DISTINCT "+var_uri+" ?l { "+query
        			+label(var_uri,"?l")
        			+"filter langMatches( lang(?l), \""+lang+"\") }";
      	    instances.putAll(getInstanceLabel(query,var_uri,"?l"));;
      	    
        }
        return instances;
    }
   
    public Map<String,List<LexicalEntry>> getInstanceLabel(String query,String var_uri,String var_label){
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
                      entry.setReference("<"+uri.toString()+">");
                     
                      if (!instances.containsKey(form)) {
                           instances.put(form,new ArrayList<>());
                     }
                      
                      instances.get(form).add(entry);
                  
                  
            	  }
                      
            	  
        }
        
        return instances;

    	
    	
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
            	  
            	  	String form = label.toString();	
            	    String form_IRI = "";  
            	  	if(form.matches(".*http://www.w3.org/2001/XMLSchema#gYear")){
    					form =	form.substring(form.indexOf('"') + 1, form.indexOf("-"));
    					form_IRI = "\""+form+"\"^^<http://www.w3.org/2001/XMLSchema#gYear>";
    				}
    				else if(form.matches("\\d{4}-\\d{2}-\\d{2}.*")){
    					form =	form.substring(form.indexOf('"') + 1, form.indexOf("+"));
    					form_IRI = "\""+form+"\"^^<http://www.w3.org/2001/XMLSchema#date>";
    				}
    				 
    				else if(form.matches(".*@"+lang+"")){
    					form = form.substring(0,form.indexOf("@"));
    					form_IRI="\""+form+"\"@en";	
    				}
    				else{
    					form = label.asLiteral().getValue().toString().replaceAll(" \\(.*?\\)", "");
    					form_IRI = uri.toString();
    				}
            		   
                	  
                      LexicalEntry entry = new LexicalEntry();
                      entry.setCanonicalForm(form);
                      entry.setReference(uri.toString());
                     
                      if (!instances.containsKey(form)) {
                           instances.put(form,new ArrayList<>());
                     }
                      
                      instances.get(form).add(entry);
                  
                  
            	  }
                      
            	  
        }
        
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
    	
    	return " ?x <" + property_uri + "> ?object .";
    }
    //to query instances(domain pos) that are suitable with property(Property to Instance)
    private String rangeQueryForPTI(String property_uri){
    	
        return " ?subject <" + property_uri + "> ?x . ";
    }
 
    
    //to query instance for property position SUBOFPROP beginning (2Property to get instance)
    private String SUBJOFPROPQueryForInstanceBeginning(String property_uri){
        
        return  "SELECT DISTINCT ?x ?l WHERE {"
                +"?x <"+property_uri+"> ?object1. ";
    }
    
    //to query instance for property position OBOFPROP beginning (2Property to get instance)
    private String OBJOFPROPQueryForInstaceBeginning(String property_uri){
        
        return  "SELECT DISTINCT ?x ?l WHERE {"
                +"?object1 <"+property_uri+"> ?x. ";
    }
    
        //to query instance for property position SUBOFPROP beginning (2Property to get instance)
    private String SUBJOFPROPQueryForClassBeginning(String property_uri){
        
        return  "SELECT DISTINCT ?y ?l WHERE {"
                +"?x <"+vocab.rdfType+"> ?y."
                +"?x <"+property_uri+"> ?object1. ";
    }
    
    //to query instance for property position OBOFPROP beginning (2Property to get instance)
    private String OBJOFPROPQueryForClassBeginning(String property_uri){
        
        return  "SELECT DISTINCT ?y ?l WHERE {"
                +"?x <"+vocab.rdfType+"> ?y."
                +"?object1 <"+property_uri+"> ?x. ";
    }
    
    
    //to query instance for property position SUBOFPROP ending (2Property to get instance)
    private String SUBJOFPROPQueryForInstaceEnding(String property_uri){
        
        return  "?x <"+property_uri+"> ?object2."
                +label("?x","?l")
                +"filter langMatches( lang(?l), \""+lang+"\") }";
    }
    
      //to query instance for property position SUBOFPROP ending (2Property to get instance)
    private String OBJOFPROPQueryForInstanceEnding(String property_uri){
        
        return  "?object2 <"+property_uri+"> ?x."
                +label("?x","?l")
                +"filter langMatches( lang(?l), \""+lang+"\") }";
    }
    
        //to query class for property position SUBOFPROP ending (2Property to get instance)
    private String SUBJOFPROPQueryForClassEnding(String property_uri){
        
        return  "?x <"+property_uri+"> ?object2."
                +label("?y","?l")
                +"filter langMatches( lang(?l), \""+lang+"\") }";
    }
    
      //to query class for property position SUBOFPROP ending (2Property to get instance)
    private String OBJOFPROPQueryForClassEnding(String property_uri){
        
        return  "?object2 <"+property_uri+"> ?x."
                +label("?y","?l")
                +"filter langMatches( lang(?l), \""+lang+"\") }";
    }
    
    //to query instances for the case; both property share same range
    private String rangeQueryFor2PTI(String property_uri1,String property_uri2){
    	   	
    	return "SELECT DISTINCT ?x ?l WHERE { "
    		+ " ?subject1 <"+ property_uri1 +"> ?x ."
                + " ?subject2 <" + property_uri2 + "> ?x . "
		+ label("?x","?l")
		+ "filter langMatches( lang(?l), \""+lang+"\")}";
    }
    //to query property's instance for property and its instance and property share same domain
    private String domainQueryFor2PAIFI(String property_uri1,String property_uri2,String instanceofprop1){
    	
    	return "SELECT DISTINCT ?y  WHERE { "
		+" "+instanceofprop1+ "  <"+ property_uri1 +"> ?x ."
            + " ?y <" + property_uri2 + "> ?x . }";
    }
    
  //to query property's instance for property and its instance and property share same range
    private String rangeQueryFor2PAIFI(String property_uri1,String property_uri2,String instanceofprop1){
    	return "SELECT DISTINCT ?y  WHERE { "
    			+ " ?x <" + property_uri1 + "> "+instanceofprop1+" . "
    			+ " ?x <" + property_uri2 +"> ?y}";
    }
	//query to get gYearLiterals by the chosen Literal
	private String gYearLiteralQueryForChosenLiteral(String literal){
		return "SELECT DISTINCT ?lit WHERE {"
				+ "?y <http://lod.springer.com/data/ontology/property/hasConference> ?conference."
				+ "?conference <http://lod.springer.com/data/ontology/property/confYear> ?lit."
				+label("?conference",literal) +"}";
	}
	private String gYearInstancesQueryForChosenLiteral(String instances){
		return  "?y <http://lod.springer.com/data/ontology/property/hasConference> "+instances+". "
				+instances+ " <http://lod.springer.com/data/ontology/property/confYear> ?lit.";
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
    	return "ASK WHERE { { ?subject1  <"+latterproperty+">  ?object . "
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
                        
    				QueryExecution ex = QueryExecutionFactory.sparqlService(endpoint, query.replace("\u00a0"," "));
    				boolean satisfiesCondition = ex.execAsk();
    				
    				if(satisfiesCondition&&entry.getReference()!=uri) filtered_entries.add(entry);    				
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
    			if (entry.getSemArg(syn)== null) continue;
    			switch(entry.getSemArg(syn)){
    				
    				case SUBJOFPROP:
    				    query = domainQueryForPTI(entry.getReference());
    				    filtered_instances_index.putAll(getInstanceIndex(query, "?x"));
    					break;
    				case OBJOFPROP:
    					query= rangeQueryForPTI(entry.getReference());
    					filtered_instances_index.putAll(getInstanceIndex(query, "?x"));
    					
    					break;
    				default:
    					break;		
    			}
    			
    			
    		}
    	
    	return filtered_instances_index;
    }


      public Map<String,List<LexicalEntry>> filterBy2PropertiesForInstances(List<LexicalEntry> entriesForPre1, List<LexicalEntry> entriesForPre2, LexicalEntry.SynArg syn1,LexicalEntry.SynArg syn2 ){
    	Map<String,List<LexicalEntry>> filtered_instances_index = new HashMap<>();
		
		for(LexicalEntry entry1 : entriesForPre1){
                    String query;
                    switch(entry1.getSemArg(syn1)){
                    
                        case SUBJOFPROP:
                        
                            query = SUBJOFPROPQueryForInstanceBeginning(entry1.getReference());
                            
                        for(LexicalEntry entry2: entriesForPre2){
			
                            switch(entry2.getSemArg(syn2)){
                                case SUBJOFPROP:
				
                                query += SUBJOFPROPQueryForInstaceEnding(entry2.getReference());
				filtered_instances_index.putAll(getInstanceIndex(query, "?x", "?l"));
				break;
				
                                case OBJOFPROP:
					query += OBJOFPROPQueryForInstanceEnding(entry2.getReference());
					filtered_instances_index.putAll(getInstanceIndex(query, "?x", "?l"));
					break;
                                        
				default:
					break;	
                            }
                            	
			
                        
                        } 
                        case OBJOFPROP:
                            
                            query = SUBJOFPROPQueryForInstanceBeginning(entry1.getReference());
                            
                            for(LexicalEntry entry2: entriesForPre2){
			
                            switch(entry2.getSemArg(syn2)){
                                case SUBJOFPROP:
				
                                    query += SUBJOFPROPQueryForInstaceEnding(entry2.getReference());
                                    filtered_instances_index.putAll(getInstanceIndex(query, "?x", "?l"));
                                    break;

                                case OBJOFPROP:
					query += OBJOFPROPQueryForInstanceEnding(entry2.getReference());
					filtered_instances_index.putAll(getInstanceIndex(query, "?x", "?l"));
					break;
                                        
                                        
				default:
					break;	
                            }
                            }
                            
                            
		}
			
		}
	
	
	return filtered_instances_index;
    	
    }
    
      
    public Map<String,List<LexicalEntry>> filterBy2PropertiesForClasses(List<LexicalEntry> entriesForPre1, List<LexicalEntry> entriesForPre2, LexicalEntry.SynArg syn1,LexicalEntry.SynArg syn2 ){
    	Map<String,List<LexicalEntry>> filtered_instances_index = new HashMap<>();
		
		for(LexicalEntry entry1 : entriesForPre1){
                    String query;
                    switch(entry1.getSemArg(syn1)){
                    
                        case SUBJOFPROP:
                        
                            query = SUBJOFPROPQueryForClassBeginning(entry1.getReference());
                            
                        for(LexicalEntry entry2: entriesForPre2){
			
                            switch(entry2.getSemArg(syn2)){
                                case SUBJOFPROP:
				
                                query += SUBJOFPROPQueryForClassEnding(entry2.getReference());
				filtered_instances_index.putAll(getInstanceIndex(query, "?x", "?l"));
				break;
				
                                case OBJOFPROP:
					query += OBJOFPROPQueryForClassEnding(entry2.getReference());
					filtered_instances_index.putAll(getInstanceIndex(query, "?x", "?l"));
					break;
                                        
				default:
					break;	
                            }
                            	
			
                        
                        } 
                        case OBJOFPROP:
                            
                            query = SUBJOFPROPQueryForInstanceBeginning(entry1.getReference());
                            
                            for(LexicalEntry entry2: entriesForPre2){
			
                            switch(entry2.getSemArg(syn2)){
                                case SUBJOFPROP:
				
                                    query += SUBJOFPROPQueryForClassEnding(entry2.getReference());
                                    filtered_instances_index.putAll(getInstanceIndex(query, "?x", "?l"));
                                    break;

                                case OBJOFPROP:
					query += OBJOFPROPQueryForClassEnding(entry2.getReference());
					filtered_instances_index.putAll(getInstanceIndex(query, "?x", "?l"));
					break;
                                        
                                        
				default:
					break;	
                            }
                            }
                            
                            
		}
			
		}
	
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	
	return filtered_instances_index;
    	
    }  
    
    
    
    
    public Map<String,List<LexicalEntry>> filterBy2PropertiesAndInstanceForInstances(List<LexicalEntry> entriesForPre1,List<LexicalEntry> entriesForIns, List<LexicalEntry> entriesForPre2, LexicalEntry.SynArg syn ){
    	Map<String,List<LexicalEntry>> filtered_instances_index = new HashMap<>();
		
		for(LexicalEntry entry1 : entriesForPre1){
		for(LexicalEntry entry2: entriesForPre2){
			String query;
			switch(entry1.getSemArg(syn)){
			//TODO mince: The position of instance may vary depending on property (think on all cases) !! 
			// instance for first property may locate at subject and for second property at domain 
				case SUBJOFPROP:
					
					for(LexicalEntry InstEntry: entriesForIns){
					
				    query = domainQueryFor2PAIFI(entry1.getReference(),entry2.getReference(),InstEntry.getReference()); 
				    filtered_instances_index.putAll(getInstanceIndex(query, "?y"));
					}
					break;
				case OBJOFPROP:
					for(LexicalEntry InstEntry: entriesForIns){
					query= rangeQueryFor2PAIFI(entry1.getReference(),entry2.getReference(),InstEntry.getReference());
					filtered_instances_index.putAll(getInstanceIndex(query, "?y"));
					}
					break;
				default:
					break;		
			}
		}
			
		}
	
	
	return filtered_instances_index;
    	
    }
    
    public Map<String,List<LexicalEntry>> filterByInstanceForInstance(List<LexicalEntry> indexes){
		
		Map<String,List<LexicalEntry>> literals = new HashMap();
		String query;
		for(LexicalEntry index: indexes){
			query =gYearInstancesQueryForChosenLiteral(index.getReference()); 
			literals.putAll(getInstanceIndex(query,"?lit"));		
		}
		
		return literals;
		
	}

}
