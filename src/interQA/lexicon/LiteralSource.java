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

public class LiteralSource {

	String endpoint;
	String lang;
	Vocabulary vocab = new Vocabulary();
	List<String> Labelprops;
	
	public LiteralSource(String endpoint,String lang){
		this.endpoint = endpoint;
		this.lang = lang;
	}

	public LiteralSource(String endpoint,String lang,List<String> props){
		this.endpoint = endpoint;
		this.lang = lang;
		Labelprops = props;
	}
	
    private String label(String var1, String var2) {
        
        String out; 
        
        if (Labelprops.size() == 1) {
            out = var1 + " <" + Labelprops.get(0) + "> " + var2 + " .";
        }
        else if (Labelprops.size() > 1) {
            out = "{ " + var1 + "  <" + Labelprops.get(0) + ">  " + var2 + " . }";
            for (String prop : Labelprops.subList(1,Labelprops.size()-1)) {
                 out += " UNION { " + var1 + " <" + prop + "> " + var2 + " . }";
            }
        }
        else out = "";
        
        return out;
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
	
	// domain query to get label literals for specific property
	private String domainLabelLiteralQueryForProperty(String property){
		
		return "SELECT DISTINCT ?lit WHERE { "
                                + " ?lit <"+property+"> ?y . " 
                        //      + " ?x <"+property+"> ?y ."
			//	+ label("?x","?lit")
				+ "filter langMatches( lang(?lit),\""+lang+"\") }";
		
	}
	//range query to get label literals for specific property
	private String rangeLabelLiteralQueryForProperty(String property){
		
		return "SELECT DISTINCT ?lit WHERE { "
                                + " ?x <"+property+"> ?lit . "
                        //      + " ?x <"+property+"> ?y."
			//	+ label("?y","?lit")
				+"filter langMatches( lang(?lit), \""+lang+"\")}";
		
	}
	
	// domain query to get literals for specific property
	private String domainLiteralQueryForProperty(String property){
		
		return "SELECT DISTINCT ?y WHERE { ?x <"+property+"> ?y. }";
		
	}
	//range query to get literals for specific property
	private String rangeLiteralQueryForProperty(String property){
		
		return "SELECT DISTINCT ?y WHERE { ?y <"+property+"> ?x. }";
		
	}
	
	
	
	//domain query to get literals for specific property that returns IRI 
	private String domainLiteralQueryForPropertyAndInstance(String property){
		
		return "SELECT DISTINCT ?lit WHERE { ?x <"+property+"> ?y."
				+label("?y","?lit") 
				+ " }";
	}
	//range query to get literals for specific property that returns IRI 
	private String rangeLiteralQueryForPropertyAndInstance(String property){
		
		return "SELECT DISTINCT ?lit WHERE { ?y <"+property+"> ?x."
				+label("?y","?lit") 
				+ " }";
	} 
	//query to get gYearLiterals by the chosen Literal
	private String gYearLiteralQueryForChosenLiteral(String literal){
		return "SELECT DISTINCT ?lit WHERE {"
				+ "?x <http://lod.springer.com/data/ontology/property/hasConference> ?conference."
				+ "?conference <http://lod.springer.com/data/ontology/property/confYear> ?lit."
				+label("?conference",literal) +"}";
	}

	private String LiteralQueryForChosenLiteralAndProperty(String prop_name,String propoflit_name,String literal){
		return	"SELECT DISTINCT ?lit WHERE { ?uri <"+prop_name+">  ?lit."
				+ "?uri <"+propoflit_name+">  "+literal+". }";
	}
	
	
	
	public Map<String,List<LexicalEntry>> getLabelLiteralByProperty(List<LexicalEntry> indexes,LexicalEntry.SynArg syn){
		            
		Map<String,List<LexicalEntry>> literals = new HashMap<>();
		
		String query;
		
		for(LexicalEntry index : indexes){

			if (index.getSemArg(syn)== null) continue;
			switch(index.getSemArg(syn)){
			
				case SUBJOFPROP:
					query = domainLabelLiteralQueryForProperty(index.getReference());
					literals.putAll(getLiteralIndex(query,"?lit"));
					break;
				case OBJOFPROP:
					query = rangeLabelLiteralQueryForProperty(index.getReference());
					literals.putAll(getLiteralIndex(query,"?lit"));
					break;
			}
		    
							
		}
				
		return literals;
		
	}
	
	public Map<String,List<LexicalEntry>> getLiteralByProperty(List<LexicalEntry> indexes,LexicalEntry.SynArg syn){
        
		Map<String,List<LexicalEntry>> literals = new HashMap<>();
		
		String query;
		
		for(LexicalEntry index : indexes){
			
			if (index.getSemArg(syn)== null) continue;
			switch(index.getSemArg(syn)){
			
				case SUBJOFPROP:
					query = rangeLiteralQueryForProperty(index.getReference());
					literals.putAll(getLiteralIndex(query,"?y"));
					break;
				case OBJOFPROP:
					query = domainLiteralQueryForProperty(index.getReference());
					literals.putAll(getLiteralIndex(query,"?y"));
					break;
			}
		    
							
		}
				
		return literals;
		
	}

	public Map<String,List<LexicalEntry>> getLiteralByPropertyAndLiteral(List<LexicalEntry> prop_indexes,List<LexicalEntry> propoflit_indexes,List<LexicalEntry> lit_indexes){
		
		
		Map<String,List<LexicalEntry>> literals = new HashMap<>();
		Map<String,List<LexicalEntry>> filtered_literals = new HashMap<>();
		
		String query;
		
		for(LexicalEntry prop_index : prop_indexes){	
		for(LexicalEntry propoflit_index : propoflit_indexes){
		for(LexicalEntry lit_index : lit_indexes){
			
			
					query = LiteralQueryForChosenLiteralAndProperty(prop_index.getReference(),propoflit_index.getReference(),lit_index.getReference()) ; 
					literals.putAll(getLiteralIndex(query,"?lit"));
					
					
					if(!literals.containsValue(lit_index.getReference())) filtered_literals.putAll(literals);
		    
		}
						
		}
		
		}
		
		return filtered_literals;
	}
	
	public Map<String,List<LexicalEntry>> getLiteralByPropertyAndInstance(List<LexicalEntry> indexes,LexicalEntry.SynArg syn){
        
		Map<String,List<LexicalEntry>> literals = new HashMap<>();
		
		String query;
		
		for(LexicalEntry index : indexes){

			if (index.getSemArg(syn)== null) continue;
			switch(index.getSemArg(syn)){
			
				case SUBJOFPROP:
					query = rangeLiteralQueryForPropertyAndInstance(index.getReference());
					literals.putAll(getLiteralIndex(query,"?lit"));
					break;
				case OBJOFPROP:
					query = domainLiteralQueryForPropertyAndInstance(index.getReference());
					literals.putAll(getLiteralIndex(query,"?lit"));
					break;
			}
		    
							
		}
				
		return literals;
		
	}
	
	public Map<String,List<LexicalEntry>> getLiteralByLiteral(List<LexicalEntry> indexes){
		
		Map<String,List<LexicalEntry>> literals = new HashMap();
		String query;
		
		for(LexicalEntry index: indexes){
			
			query =gYearLiteralQueryForChosenLiteral(index.getReference()); 
			literals.putAll(getLiteralIndex(query,"?lit"));		
		}
		
		
		
		return literals;
		
	}
}


