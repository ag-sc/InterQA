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

public class LiteralSource {

	String endpoint;
	String lang;
	Vocabulary vocab = new Vocabulary();
	
	
	public LiteralSource(String endpoint,String lang){
		this.endpoint = endpoint;
		this.lang = lang;
	}

	private Map<String,List<LexicalEntry>> getLiteralIndex(String query, String var_literal){
		
		Map<String,List<LexicalEntry>> literals = new HashMap<>();
		
		QueryExecution ex =  QueryExecutionFactory.sparqlService(endpoint,query);
		ResultSet results = ex.execSelect();
		
		while(results.hasNext()){
				
			QuerySolution result = results.nextSolution();
			
			RDFNode literal = result.get(var_literal);
			
			
			if(literal !=null ){
				
				String form = literal.toString();
				String form_IRI ="";
				if(form.matches(".*http://www.w3.org/2001/XMLSchema#gYear")){
					form =	form.substring(form.indexOf('"') + 1, form.indexOf("-"));
					form_IRI = "\""+form+"\"^^<http://www.w3.org/2001/XMLSchema#gYear>";
				}
				else if(form.matches("\\d{4}-\\d{2}-\\d{2}.*")){
					form =	form.substring(form.indexOf('"') + 1, form.indexOf("+"));
					form_IRI = "\""+form+"\"^^<http://www.w3.org/2001/XMLSchema#date>";
				}
				 
				else{
					form = form.substring(0,form.indexOf("@"));
					form_IRI="\""+form+"\"@en";	
				}
				
				 
				
				LexicalEntry entry = new LexicalEntry();
				entry.setCanonicalForm(form);
				entry.setReference(form_IRI);
				
				if(!literals.containsKey(form)){
					literals.put(form,new ArrayList<>());
				}
				
				literals.get(form).add(entry);
				
			}
			
		}
		
		
		return literals;
		
	}
	
	// query to get literals for specific property
	private String LiteralQueryForProperty(String property){
		
		return "SELECT DISTINCT ?lit  { ?x <"+property+"> ?lit.}";
		
	}
	// return literals depends on properties
	public Map<String,List<LexicalEntry>> getLiteralByProperty(List<LexicalEntry> indexes){
		
		Map<String,List<LexicalEntry>> literals = new HashMap<>();
		
		String query;
		
		for(LexicalEntry index : indexes){
		    query = LiteralQueryForProperty(index.getReference());
			literals.putAll(getLiteralIndex(query,"?lit"));
							
		}
				
		return literals;
		
	}
}


