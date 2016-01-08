package interQA.lexicon;

import java.util.ArrayList;
import java.util.List;

import interQA.elements.ClassElement;
import interQA.elements.IndividualElement;
import interQA.elements.LiteralElement;
import interQA.elements.PropertyElement;

public class SparqlQueryBuilder {

	Vocabulary vocab = new Vocabulary();
	
	
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
		//SELECT DISTINCT ?uri { ?uri rdf:type Conference . ?uri confYear "2015" . }
		return "SELECT DISTINCT ?uri "
				+ "{ ?uri  <"+vocab.rdfType+">  <"+class_entry.getReference()+">. "
				+ "  ?uri  <"+prop_entry.getReference()+">  "+lit_entry.getReference()+". }";
	}
	
	
	public List<String> BuildQueryForClassInstances(List<LexicalEntry> class_entries){
		
		List<String> queries = new ArrayList<>();
		for(LexicalEntry class_entry: class_entries)
			queries.add(queryForClassInstances(class_entry));
		return queries;
	}
	
	//without class IRI
	public List<String> BuildQueryForIndividualAndPropery(IndividualElement instance_elements,PropertyElement property_elements,LexicalEntry.SynArg syn){
		
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
	public List<String> BuildQueryForIndividualAndProperty(ClassElement class_elements,IndividualElement instance_elements,PropertyElement property_elements,LexicalEntry.SynArg syn){
		
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

	public List<String> BuildQueryForClassAndProperty(ClassElement class_elements,PropertyElement property_elements,LexicalEntry.SynArg syn){
		
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

	public List<String> BuildQueryForClassAnd2Properties(ClassElement class_elements,PropertyElement property_element1,PropertyElement property_element2,LexicalEntry.SynArg syn1,LexicalEntry.SynArg syn2){
		
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

	public List<String> BuildQueryForClassAndPropertyAndLiteral(ClassElement class_elements,PropertyElement property_elements,LiteralElement literal_elements){
		
		List<String> queries = new ArrayList<>();
		
		for(LexicalEntry noun_entry : class_elements.getActiveEntries()){
			for(LexicalEntry verb_entry : property_elements.getActiveEntries()){
				for(LexicalEntry lit_entry: literal_elements.getActiveEntries()){
					queries.add(queryForincasePropertyAndLiteral(noun_entry,verb_entry,lit_entry));
				}
			}
		}
		
		return queries;
	}
}