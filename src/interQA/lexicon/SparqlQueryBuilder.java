package interQA.lexicon;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;

import interQA.elements.ClassElement;
import interQA.elements.IndividualElement;
import interQA.elements.LiteralElement;
import interQA.elements.PropertyElement;


public class SparqlQueryBuilder {

	Vocabulary vocab = new Vocabulary();
	String endpoint = "http://es.dbpedia.org/sparql";
	
	List<String> LabelProperties; 
	List<String> DateProperties;
	
        public SparqlQueryBuilder() {
               
            LabelProperties = new ArrayList<>();
            LabelProperties.add("http://lod.springer.com/data/ontology/property/confAcronym");
            LabelProperties.add("http://lod.springer.com/data/ontology/property/confName");
        
            DateProperties = new ArrayList<>();
            DateProperties.add("http://lod.springer.com/data/ontology/property/confYear");
        }
        
	
	 private String label(String var1, String var2,List<String> Labelprops) {
	        
	        String out; 
	        
	        if (Labelprops.size() == 1) {
	            out = var1 + " <" + Labelprops.get(0) + "> " + var2 + " .";
	        }
	        else if (Labelprops.size() > 1) {
	            out = "{ " + var1 + "  <" + Labelprops.get(0) + ">  " + var2 + " . }";
	            for (int i = 1; i < Labelprops.size(); i++) {
	                 out += " UNION { " + var1 + " <" + Labelprops.get(i) + "> " + var2 + " . }";
	            }
	        }
	        else out = "";
	        
	        return out;
	    }
	
	 public boolean SPARQLQueryValidator(String check_query){
                QueryExecution ex = QueryExecutionFactory.sparqlService(endpoint,check_query);
                return ex.execAsk();
		}

	 
	 // query to reach Instances of a Class
	private String queryForClassInstances(LexicalEntry classvar){
		return "SELECT DISTINCT ?x WHERE { "
                + " ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + classvar.getReference() + "> . }";
	}
	// query for SUBJOFPROP pos -- Individual has its class info and Property
	private String queryForSUBJOFPROPinCaseIndividualAndProperty(LexicalEntry noun_entry,LexicalEntry nounprop_entry,LexicalEntry inst_entry){
		return "SELECT DISTINCT ?x WHERE { "
				+ "?x <"+vocab.rdfType+"> <"+noun_entry.getReference()+"> ."
    			+ "?x <"+nounprop_entry.getReference()+"> <"+inst_entry.getReference()+"> . }";
	}
	// query for OBJOFPROP pos -- Individual has its class info and Property
	private String queryForOBJOFPROPinCaseIndividualAndProperty(LexicalEntry noun_entry,LexicalEntry nounprop_entry,LexicalEntry inst_entry){
		return "SELECT DISTINCT ?x WHERE { "
				+ "?x <"+vocab.rdfType+"> <"+noun_entry.getReference()+"> ."
				+ " <"+inst_entry.getReference()+"> <"+nounprop_entry.getReference()+"> ?x . }";
	}
	//query for SUBJOFPROP pos -- Individual has NOT its class info ,contrary has Property
	private String queryForSUBJOFPROPinCaseIndividualAndProperty(LexicalEntry prop_entry, LexicalEntry inst_entry ){

		return "SELECT DISTINCT ?x WHERE {"
                + " <" + inst_entry.getReference() + "> <" + prop_entry.getReference() + "> ?x . }";
	}
	//query for OBJOFPROP pos -- Individual has NOT its class info ,contrary has Property
	private String queryForOBJOFPROPinCaseIndividualAndProperty(LexicalEntry prop_entry,LexicalEntry inst_entry){
		return "SELECT DISTINCT ?x WHERE { "
				+ " <"+inst_entry.getReference()+"> <"+prop_entry.getReference()+"> ?x . }";
	}
	//query for SUBJOFPROP pos -- There is a Class and a Property
	private String queryForSUBJOFPROPinCaseClassAndProperty(LexicalEntry class_entry,LexicalEntry prop_entry){
		
		return "SELECT DISTINCT ?x {"
				+ " ?a  <" + vocab.rdfType + ">  <" + class_entry.getReference() + "> ."
				+ " ?a  <" + prop_entry.getReference() + "> ?x . }";
	}
	//query for OBJOFPROP pos -- There is a Class and a Property
	private String queryForOBJOFPROPinCaseClassAndProperty(LexicalEntry class_entry,LexicalEntry prop_entry){
		
		return "SELECT DISTINCT ?x {"
				+ " ?a  <" + vocab.rdfType + ">  <" + class_entry.getReference() + "> ."
				+ " ?x  <" + prop_entry.getReference() + "> ?a . }"; 
	}
	// query for SUBOFPROP pos for first property -- two property case
	private String queryForSUBJOFPROPinCaseClassAndPropertyBeginning(LexicalEntry class_entry,LexicalEntry prop_entry1){
		return "SELECT DISTINCT ?x ?y {"
				+ " ?a  <" + vocab.rdfType + ">  <" + class_entry.getReference() + "> ."
				+ " ?a  <" + prop_entry1.getReference() + "> ?x . ";
	}
	// query for SUBOFPROP pos for first property -- two property case
	private String queryForOBJOFPROPinCaseClassAndPropertyBeginning(LexicalEntry class_entry,LexicalEntry prop_entry1){
		return "SELECT DISTINCT ?x ?y {"
				+ " ?a  <" + vocab.rdfType + ">  <" + class_entry.getReference() + "> ."
				+ " ?x  <" + prop_entry1.getReference() + "> ?a . ";
	}
	// query for SUBOFPROP pos for second property -- two property case
	private String queryForSUBJOFPROPinCaseClassAndPropertyEnding(LexicalEntry prop_entry2){
			return " ?a  <" + prop_entry2.getReference() + "> ?y . }";
		}
	// query for SUBOFPROP pos for second property -- two property case
	private String queryForOBJOFPROPinCaseClassAndPropertyEnding(LexicalEntry prop_entry2){
				return " ?y  <" + prop_entry2.getReference() + "> ?a . }";
			}
	
	
	
	//SPRINGER
	
	//query for property (w.r.t class) and literal
	private String queryForincasePropertyAndLiteral(LexicalEntry class_entry,LexicalEntry prop_entry,LexicalEntry lit_entry){
		//SELECT DISTINCT ?uri WHERE { ?uri rdf:type Conference . ?uri confYear "2015" . }
		return "SELECT DISTINCT ?uri WHERE "
				+ "{ ?uri  <"+vocab.rdfType+">  <"+class_entry.getReference()+">. "
				+ "  ?uri  <"+prop_entry.getReference()+">  "+lit_entry.getReference()+". }";
	}
	
	private String queryForincase2PropertyAnd2Literal(LexicalEntry class_entry,LexicalEntry prop1_entry,LexicalEntry lit1_entry,
			LexicalEntry prop2_entry,LexicalEntry lit2_entry){
		return "SELECT DISTINCT ?uri WHERE "
				+ "{ ?uri  <"+vocab.rdfType+">  <"+class_entry.getReference()+">. "
				+ "  ?uri  <"+prop1_entry.getReference()+">  "+lit1_entry.getReference()+". "
				+ "  ?uri  <"+prop2_entry.getReference()+">  "+lit2_entry.getReference()+".}";
	}
	
	// create query to ensure whether it works or not (property (w.r.t class) and literal)
	private String AskQueryForincasePropertyAndLiteral(LexicalEntry class_entry,LexicalEntry prop_entry,LexicalEntry lit_entry){
		
		return "ASK WHERE"
				+ "{ ?uri  <"+vocab.rdfType+">  <"+class_entry.getReference()+">. "
				+ "  ?uri  <"+prop_entry.getReference()+">  "+lit_entry.getReference()+". }";
	}
	
	private String AskQueryincase2PropertyAnd2Literal(LexicalEntry class_entry,LexicalEntry prop1_entry,LexicalEntry lit1_entry,
			LexicalEntry prop2_entry,LexicalEntry lit2_entry){
		
		return "ASK WHERE"
				+ "{ ?uri  <"+vocab.rdfType+">  <"+class_entry.getReference()+">. "
				+ "  ?uri  <"+prop1_entry.getReference()+">  "+lit1_entry.getReference()+". "
				+ "  ?uri  <"+prop2_entry.getReference()+">  "+lit2_entry.getReference()+".}";
	}
	
	
	//query for gYear Literal and Name Literal of Conference and Property
	private String queryForincasegYearNameandProperty(LexicalEntry gYear_entry,LexicalEntry name_entry,LexicalEntry prop_entry){
                        
		return "SELECT DISTINCT ?x WHERE "
				+ "{?x <"+prop_entry.getReference()+"> ?lit."
				+label("?lit",name_entry.getReference(),LabelProperties)
				+label("?lit",gYear_entry.getReference(),DateProperties)
				+" }";
	}
	// create query to ensure whether it works or not (property (w.r.t class) and gYear and Name Literal)
	private String AskQueryForincasePropertyAndgYearAndNameLiteral(LexicalEntry gYear_entry,LexicalEntry name_entry,LexicalEntry prop_entry){
		return "ASK WHERE "
				+ "{?x <"+prop_entry.getReference()+"> ?lit."
				+label("?lit",name_entry.getReference(),LabelProperties)
				+label("?lit",gYear_entry.getReference(),DateProperties)
				+" }";
		
	}
	
	private String queryForSUBJOFPROPinCasePropertyBeginning(LexicalEntry prop_entry){
		return "SELECT DISTINCT ?lit1 ?lit2  { ?x  <"+prop_entry.getReference()+"> ?lit1.";
	}
	
	private String queryForOBJOFPROPinCasePropertyBeginning(LexicalEntry prop_entry){
		return "SELECT DISTINCT ?lit1 ?lit2  { ?lit1  <"+prop_entry.getReference()+"> ?x.";
	}
	
	private String AskQueryForSUBJOFPROPinCasePropertyBeginning(LexicalEntry prop_entry){
		return "ASK WHERE { ?x  <"+prop_entry.getReference()+"> ?lit1.";
	}
	
	private String AskQueryForOBJOFPROPinCasePropertyBeginning(LexicalEntry prop_entry){
		return "ASK WHERE { ?lit1  <"+prop_entry.getReference()+"> ?x.";
	}

	private String queryForSUBJOFPROPinCasePropertyInMiddle(LexicalEntry prop_entry){
		return "?x <"+prop_entry.getReference()+"> ?lit2.";
	}
	
	private String queryForOBJOFPROPinCasePropertyInMiddle(LexicalEntry prop_entry){
		return "?lit2 <"+prop_entry.getReference()+"> ?x. ";
	}
	

	
	
	public List<String> BuildQueryForClassInstances(List<LexicalEntry> class_entries){
		
		List<String> queries = new ArrayList<>();
		for(LexicalEntry class_entry: class_entries)
			queries.add(queryForClassInstances(class_entry));
		return queries;
	}
	
	//without class IRI
	public List<String> BuildQueryForIndividualAndPropery(IndividualElement instance_elements,PropertyElement property_elements,
LexicalEntry.SynArg syn){
		
		List<String> queries = new ArrayList<>();
		
		for(LexicalEntry prop_entry : property_elements.getActiveEntries()){
			//LexicalEntry.SynArg.DIRECTOBJECT
			switch(prop_entry.getSemArg(syn)){
				
			case SUBJOFPROP:
				for(LexicalEntry inst_entry : instance_elements.getActiveEntries()){
					queries.add(queryForSUBJOFPROPinCaseIndividualAndProperty(prop_entry,inst_entry));
				}
				break;
			case OBJOFPROP:
				for(LexicalEntry inst_entry : instance_elements.getActiveEntries()){
					queries.add(queryForOBJOFPROPinCaseIndividualAndProperty(prop_entry,inst_entry));
				}
				break;
			default:
				break;
			}
			
			
			
			
		}
		
		
		return queries;
		
	}
	//with class IRI of Instances
	public List<String> BuildQueryForIndividualAndProperty(ClassElement class_elements,IndividualElement instance_elements,
PropertyElement property_elements,LexicalEntry.SynArg syn){
		
		List<String> queries = new ArrayList<>();
		
		for(LexicalEntry noun_entry :class_elements.getActiveEntries()){
			for(LexicalEntry nounprop_entry: property_elements.getActiveEntries()){
				
				switch(nounprop_entry.getSemArg(syn)){
					
				case SUBJOFPROP:
					for(LexicalEntry inst_entry : instance_elements.getActiveEntries()){
					queries.add(queryForSUBJOFPROPinCaseIndividualAndProperty(noun_entry,nounprop_entry,inst_entry));
					}
					break;
				case OBJOFPROP:
					for(LexicalEntry inst_entry : instance_elements.getActiveEntries()){
						queries.add(queryForOBJOFPROPinCaseIndividualAndProperty(noun_entry,nounprop_entry,inst_entry));
					}
					
					break;
				default:
					break;
				
				}
					
					
			}
			
		}
		
		
		
		return queries;
	}

	public List<String> BuildQueryForClassAndProperty(ClassElement class_elements,PropertyElement property_elements,
LexicalEntry.SynArg syn){
		
		List<String> queries  = new ArrayList<>();
		
		for(LexicalEntry class_entry : class_elements.getActiveEntries()){
			for(LexicalEntry property_entry : property_elements.getActiveEntries()){
				
				switch(property_entry.getSemArg(syn)){
				
				case SUBJOFPROP:
					queries.add(queryForSUBJOFPROPinCaseClassAndProperty(class_entry,property_entry));
					break;
					
				case OBJOFPROP:
					queries.add(queryForOBJOFPROPinCaseClassAndProperty(class_entry,property_entry));
					break;
				
				default:
						break;
				
				}
				
			}
			
		}
		
		
		
		return queries;
		
	}

	public List<String> BuildQueryForClassAnd2Properties(ClassElement class_elements,PropertyElement property_element1,
PropertyElement property_element2,LexicalEntry.SynArg syn1,LexicalEntry.SynArg syn2){
		
		List<String> queries = new ArrayList<>();
		
		for(LexicalEntry class_entry : class_elements.getActiveEntries()){
		for(LexicalEntry property_entry1 : property_element1.getActiveEntries()){
			
			String query ="" ;
			
			switch(property_entry1.getSemArg(syn1)){
				
				case SUBJOFPROP:
					
					query = queryForSUBJOFPROPinCaseClassAndPropertyBeginning(class_entry,property_entry1);
					
					for(LexicalEntry property_entry2 : property_element2.getActiveEntries()){
						
						switch(property_entry2.getSemArg(syn2)){
						
						case SUBJOFPROP:
							query += queryForSUBJOFPROPinCaseClassAndPropertyEnding(property_entry2);
							queries.add(query);
							break;
						case OBJOFPROP:
							query += queryForOBJOFPROPinCaseClassAndPropertyEnding(property_entry2);
							queries.add(query);
							break;
						default:
							break;
						}
						
					}
					
					
					break;
					
				case OBJOFPROP:
					for(LexicalEntry property_entry2 : property_element2.getActiveEntries()){
						
						query = queryForOBJOFPROPinCaseClassAndPropertyBeginning(class_entry,property_entry1);
						
						switch(property_entry2.getSemArg(syn2)){
						
						case SUBJOFPROP:
							query += queryForSUBJOFPROPinCaseClassAndPropertyEnding(property_entry2);
							queries.add(query);
							break;
						case OBJOFPROP:
							query += queryForOBJOFPROPinCaseClassAndPropertyEnding(property_entry2);
							queries.add(query);
							break;
						default:
							break;
						}
						
					}
					
					
					
					
					break;
		}
		}
		
		}
		return queries;
	}

	
	//SPRINGER
	public List<String> BuildQueryForClassAndPropertyAndLiteral(ClassElement class_elements,PropertyElement property_elements,
LiteralElement literal_elements){
		
		List<String> queries = new ArrayList<>();
		String query="";
		String check_query="";
		for(LexicalEntry noun_entry : class_elements.getActiveEntries()){
			for(LexicalEntry verb_entry : property_elements.getActiveEntries()){
				for(LexicalEntry lit_entry: literal_elements.getActiveEntries()){
					query=queryForincasePropertyAndLiteral(noun_entry,verb_entry,lit_entry);
					check_query =AskQueryForincasePropertyAndLiteral(noun_entry,verb_entry,lit_entry); 
				}
				if(SPARQLQueryValidator(check_query)) queries.add(query);
			}
		}
		
		return queries;
	}
	
	public List<String> BuildQueryForClassAnd2PropertyAnd2Literal(ClassElement class_elements,PropertyElement property1_elements,
			LiteralElement literal1_elements,PropertyElement property2_elements,LiteralElement literal2_elements){
		List<String> queries = new ArrayList<>();
		String query ="";
		String check_query = "";
		
		for(LexicalEntry class_element: class_elements.getActiveEntries()){
			for(LexicalEntry property1_element: property1_elements.getActiveEntries()){
				for(LexicalEntry literal1_element:literal1_elements.getActiveEntries()){
					for(LexicalEntry property2_element:property2_elements.getActiveEntries()){
						for(LexicalEntry literal2_element : literal2_elements.getActiveEntries()){
							query= queryForincase2PropertyAnd2Literal(class_element,property1_element,literal1_element,
									property2_element,literal2_element);
							check_query=AskQueryincase2PropertyAnd2Literal(class_element,property1_element,literal1_element,
									property2_element,literal2_element);
						}
						if(!query.isEmpty() && SPARQLQueryValidator(check_query)) queries.add(query);
					}
				}
			}
		}
		
		
		
		return queries;
		
	}
	
	
	public List<String> BuildQueryForPropertyAndgYearAndNameLiteral(PropertyElement property_elements,LiteralElement gYear_elements,
LiteralElement name_elements){
		
		List<String> queries = new ArrayList<>();
		String query = "";
		String check_query="";
		for(LexicalEntry property_element: property_elements.getActiveEntries()){
			for(LexicalEntry name_element : name_elements.getActiveEntries()){
				for(LexicalEntry gYear_element : gYear_elements.getActiveEntries()){
					query = queryForincasegYearNameandProperty(gYear_element,name_element,property_element);
					check_query = AskQueryForincasePropertyAndgYearAndNameLiteral(gYear_element,name_element,property_element);
				}
				if(SPARQLQueryValidator(check_query)&& !queries.contains(query)) queries.add(query);
			}
		}
		
		
		return queries;
	}

	public List<String> BuildQueryFor2PropertyAndNameLiteralAndGYearLiteral(PropertyElement property_elements1,LexicalEntry.SynArg syn1,
			PropertyElement property_elements2,LexicalEntry.SynArg syn2,LiteralElement name_literals,LiteralElement gYear_literals){
		
		List<String> queries = new ArrayList<>();
		
		for(LexicalEntry property_entry1 : property_elements1.getActiveEntries()){
			
			String query = "";
			String check_query= "";
			
			switch(property_entry1.getSemArg(syn1)){
				case SUBJOFPROP:
					
					query = queryForSUBJOFPROPinCasePropertyBeginning(property_entry1);
					check_query = AskQueryForSUBJOFPROPinCasePropertyBeginning(property_entry1);
					
					for(LexicalEntry property_entry2 : property_elements2.getActiveEntries()){
						
						switch(property_entry2.getSemArg(syn2)){
						
						case SUBJOFPROP:
							query += queryForSUBJOFPROPinCasePropertyInMiddle(property_entry2);
							check_query += queryForSUBJOFPROPinCasePropertyInMiddle(property_entry2);
							for(LexicalEntry gYear_entry : gYear_literals.getActiveEntries()){
								for(LexicalEntry name_entry : name_literals.getActiveEntries()){
									query += label("?x",gYear_entry.getReference(),DateProperties) + label("?x",name_entry.getReference(),LabelProperties);
									check_query += label("?x",gYear_entry.getReference(),DateProperties) + label("?x",name_entry.getReference(),LabelProperties);
								}
							}
							query += "}";
							check_query += "}";
							
							if(SPARQLQueryValidator(check_query)) queries.add(query);
							
							break;
						case OBJOFPROP:
							query += queryForOBJOFPROPinCasePropertyInMiddle(property_entry2);
							check_query += queryForOBJOFPROPinCasePropertyInMiddle(property_entry2);
							for(LexicalEntry gYear_entry : gYear_literals.getActiveEntries()){
								for(LexicalEntry name_entry : name_literals.getActiveEntries()){
									query += label("?x",gYear_entry.getReference(),DateProperties) + label("?x",name_entry.getReference(),LabelProperties);
									check_query += label("?x",gYear_entry.getReference(),DateProperties) + label("?x",name_entry.getReference(),LabelProperties);
								}
							}
							query += "}";
							check_query += "}";
							
							if(SPARQLQueryValidator(check_query)) queries.add(query);
							break;
						default:
							break;
						}
					}
					
					break;
					
				case OBJOFPROP:
					for(LexicalEntry property_entry2 : property_elements2.getActiveEntries()){
						
						query = queryForOBJOFPROPinCasePropertyBeginning(property_entry1);
						check_query = AskQueryForOBJOFPROPinCasePropertyBeginning(property_entry1);
						
						switch(property_entry2.getSemArg(syn2)){
						

						case SUBJOFPROP:
							query += queryForSUBJOFPROPinCasePropertyInMiddle(property_entry2);
							check_query += queryForSUBJOFPROPinCasePropertyInMiddle(property_entry2) ;
							for(LexicalEntry gYear_entry : gYear_literals.getActiveEntries()){
								for(LexicalEntry name_entry : name_literals.getActiveEntries()){
									query += label("?x",gYear_entry.getReference(),DateProperties) + label("?x",name_entry.getReference(),LabelProperties);
									check_query += label("?x",gYear_entry.getReference(),DateProperties) + label("?x",name_entry.getReference(),LabelProperties);
								}
							}
							query += "}";
							check_query += "}";
							
							if(SPARQLQueryValidator(check_query)) queries.add(query);
							break;
						case OBJOFPROP:
							query += queryForOBJOFPROPinCasePropertyInMiddle(property_entry2);
							for(LexicalEntry gYear_entry : gYear_literals.getActiveEntries()){
								for(LexicalEntry name_entry : name_literals.getActiveEntries()){
									query += label("?x",gYear_entry.getReference(),DateProperties) + label("?x",name_entry.getReference(),LabelProperties);
									check_query += label("?x",gYear_entry.getReference(),DateProperties) + label("?x",name_entry.getReference(),LabelProperties);
								}
							}
							query += "}";
							check_query += "}";
							
							if(SPARQLQueryValidator(check_query)) queries.add(query);
							break;
						default:
							break;
				
					}		
					break;	
			
			}
			
		}
		
	}
		return queries;

}
	
	

}