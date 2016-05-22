package interQA.lexicon;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;

import interQA.elements.ClassElement;
import interQA.elements.InstanceElement;
import interQA.elements.PropertyElement;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


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
         
         //check the type of instance, if it is literal, FILTER property is added to query
         public String typeChecker(LexicalEntry instance){
             
            char var_name = randomVariable();
            if(instance.getReference().matches("http://.*")){
                    return " instance.getReference()";
                    }
            else{
                    return " ?"+var_name+" . FILTER regex(?"+var_name+",\""+instance.getCanonicalForm()+"\") ";	
            }
          
         }
         
         //random letter creator for multi-line sparql queries
         private char randomVariable(){
            
            Random r = new Random();
            char c = (char) (r.nextInt(26) + 'a');
            
            return c; 
         }
         
        //depends on the value of flag puts ?x or COUNT(?x) for How many questions 
        private String countFlag(boolean flag,String var){
            
            if(flag==true){
                return "COUNT(DISTINCT ?"+var+")";
            }
            else return  "DISTINCT ?"+var;
         
        } 
	 
	 // query to reach Instances of a Class
	private String queryForClassInstances(LexicalEntry classvar,boolean flag){
            
            return "SELECT "+countFlag(flag,"x")+" WHERE { "
            + " ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + classvar.getReference() + "> . }";
	}
	// query for SUBJOFPROP pos -- Individual has its class info and Property
	private String queryForSUBJOFPROPinCaseClassAndIndividualAndProperty(LexicalEntry noun_entry,LexicalEntry nounprop_entry,LexicalEntry inst_entry,
                            boolean flag){
                           
		return "SELECT "+countFlag(flag,"x")+" WHERE { "
				+ "?x <"+vocab.rdfType+"> <"+noun_entry.getReference()+"> ."
    			+ "?x <"+nounprop_entry.getReference()+"> "+inst_entry.getReference()+" . }";
	}
	// query for OBJOFPROP pos -- Individual has its class info and Property
	private String queryForOBJOFPROPinCaseClassAndIndividualAndProperty(LexicalEntry noun_entry,LexicalEntry nounprop_entry,LexicalEntry inst_entry,
                    boolean flag){
            
		return "SELECT  "+countFlag(flag,"x")+" WHERE { "
				+ "?x <"+vocab.rdfType+"> <"+noun_entry.getReference()+"> ."
				+ " "+inst_entry.getReference()+" <"+nounprop_entry.getReference()+"> ?x . }";
	}
	//query to confirm the validity of query for SUBJOFPROP
        private String askQueryForSUBJOFPROPinCaseClassAndIndividualAndProperty(LexicalEntry noun_entry,LexicalEntry nounprop_entry,LexicalEntry inst_entry,
                            boolean flag){
                           
		return "ASK WHERE { "
				+ "?x <"+vocab.rdfType+"> <"+noun_entry.getReference()+"> ."
    			+ "?x <"+nounprop_entry.getReference()+"> "+inst_entry.getReference()+" . }";
	}
        //query to confirm the validity of query for OBJOFPROP
        private String askQueryOBJOFPROPinCaseClassAndIndividualAndProperty(LexicalEntry noun_entry,LexicalEntry nounprop_entry,LexicalEntry inst_entry,
                    boolean flag){
            
		return "ASK WHERE { "
				+ "?x <"+vocab.rdfType+"> <"+noun_entry.getReference()+"> ."
				+ " "+inst_entry.getReference()+" <"+nounprop_entry.getReference()+"> ?x . }";
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
	private String queryForSUBJOFPROPinCaseClassAndProperty(LexicalEntry class_entry,LexicalEntry prop_entry,boolean flag){
		
            
		return "SELECT "+countFlag(flag,"x")+" {"
				+ " ?a  <" + vocab.rdfType + ">  <" + class_entry.getReference() + "> ."
				+ " ?a  <" + prop_entry.getReference() + "> ?x . }";
	}
	//query for OBJOFPROP pos -- There is a Class and a Property
	private String queryForOBJOFPROPinCaseClassAndProperty(LexicalEntry class_entry,LexicalEntry prop_entry,boolean flag){
		
		return "SELECT  "+countFlag(flag,"x")+" {"
				+ " ?a  <" + vocab.rdfType + ">  <" + class_entry.getReference() + "> ."
				+ " ?x  <" + prop_entry.getReference() + "> ?a . }"; 
	}
        
 
	// query for SUBOFPROP pos for first property -- two property case
	private String queryForSUBJOFPROPinCaseClassAndPropertyBeginning(LexicalEntry class_entry,LexicalEntry prop_entry1){
		return "SELECT DISTINCT ?x ?y {"
				+ " ?a  <" + vocab.rdfType + ">  <" + class_entry.getReference() + "> ."
				+ " ?a  <" + prop_entry1.getReference() + "> ?x . ";
	}
	// query for OBOFPROP pos for first property -- two property case
	private String queryForOBJOFPROPinCaseClassAndPropertyBeginning(LexicalEntry class_entry,LexicalEntry prop_entry1){
		return "SELECT DISTINCT ?x ?y {"
				+ " ?a  <" + vocab.rdfType + ">  <" + class_entry.getReference() + "> ."
				+ " ?x  <" + prop_entry1.getReference() + "> ?a . ";
	}
        
        // query for SUBOFPROP pos for first property -- two property case
	private String queryForSUBJOFPROPinCaseInstanceAndPropertyBeginning(LexicalEntry instance_entry,LexicalEntry prop_entry1){
		return "SELECT DISTINCT ?x ?y {"
				+ instance_entry+"  <" + prop_entry1.getReference() + "> ?x . ";
	}
        
        // query for OBOFPROP pos for first property -- two property case
	private String queryForOBJOFPROPinCaseInstanceAndPropertyBeginning(LexicalEntry instance_entry,LexicalEntry prop_entry1){
		return "SELECT DISTINCT ?x ?y {"
				+ " ?x  <" + prop_entry1.getReference() + "> "+instance_entry+" .";
	}
        
        
	// query for SUBOFPROP pos for second property -- two property case
	private String queryForSUBJOFPROPinCaseClassAndPropertyEnding(LexicalEntry prop_entry2){
			return " ?a  <" + prop_entry2.getReference() + "> ?y . }";
		}
	// query for OBJOFPROP pos for second property -- two property case
	private String queryForOBJOFPROPinCaseClassAndPropertyEnding(LexicalEntry prop_entry2){
				return " ?y  <" + prop_entry2.getReference() + "> ?a . }";
			}
	
	// query for SUBOFPROP pos for second property -- two property case
	private String queryForSUBJOFPROPinCaseInstanceAndPropertyEnding(LexicalEntry instance_entry,LexicalEntry prop_entry2){
			return instance_entry+"  <" + prop_entry2.getReference() + "> ?y . }";
        }
        
        // query for OBJOFPROP pos for second property -- two property case
	private String queryForOBJOFPROPinCaseInstanceAndPropertyEnding(LexicalEntry instance_entry,LexicalEntry prop_entry2){
				return " ?y  <" + prop_entry2.getReference() + "> "+instance_entry+" . }";
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
	private String DomainqueryForincasegYearNameandProperty(LexicalEntry gYear_entry,LexicalEntry name_entry,LexicalEntry prop_entry){
                        
		return "SELECT DISTINCT ?lit WHERE "
				+ "{?x <"+prop_entry.getReference()+"> ?y."
				+label("?x","?l1",LabelProperties)+" FILTER regex(?l1,\""+name_entry.getCanonicalForm()+"\"). "
				+label("?x","?l2",DateProperties)+" FILTER regex(?l2,\""+gYear_entry.getCanonicalForm()+"\"). "
				+" }";
	}
    private String RangequeryForincasegYearNameandProperty(LexicalEntry gYear_entry,LexicalEntry name_entry,LexicalEntry prop_entry){
                        
		return "SELECT DISTINCT ?x WHERE "
				+ "{?x <"+prop_entry.getReference()+"> ?y."
				+label("?y","?l1",LabelProperties)+" FILTER regex(?l1,\""+name_entry.getCanonicalForm()+"\"). "
				+label("?y","?l2",DateProperties)+" FILTER regex(?l2,\""+gYear_entry.getCanonicalForm()+"\"). "
				+" }";
	}


// create query to ensure whether it works or not (property (w.r.t class) and gYear and Name Literal)
	private String DomainAskQueryForincasePropertyAndgYearAndNameLiteral(LexicalEntry gYear_entry,LexicalEntry name_entry,LexicalEntry prop_entry){
		return "ASK WHERE "
				+ "{?x <"+prop_entry.getReference()+"> ?y."
				+label("?x","?l1",LabelProperties)+" FILTER regex(?l1,\""+name_entry.getCanonicalForm()+"\"). "
				+label("?x","?l2",DateProperties)+" FILTER regex(?l2,\""+gYear_entry.getCanonicalForm()+"\"). "
				+" }";
                
                
		
	}
	
    private String RangeAskQueryForincasePropertyAndgYearAndNameLiteral(LexicalEntry gYear_entry,LexicalEntry name_entry,LexicalEntry prop_entry){
		return "ASK WHERE "
				+ "{?x <"+prop_entry.getReference()+"> ?y."
				+label("?y","?l1",LabelProperties)+" FILTER regex(?l1,\""+name_entry.getCanonicalForm()+"\"). "
				+label("?y","?l2",DateProperties)+" FILTER regex(?l2,\""+gYear_entry.getCanonicalForm()+"\"). "
				+" }";
                
                
		
	}
        
	private String queryForSUBJOFPROPinCasePropertyBeginning(LexicalEntry prop_entry){
		return "SELECT DISTINCT ?lit1 ?lit2  { ?x  <"+prop_entry.getReference()+"> ?lit1.";
	}
	
	private String queryForOBJOFPROPinCasePropertyBeginning(LexicalEntry prop_entry){
		return "SELECT DISTINCT ?lit1 ?lit2  { ?lit1  <"+prop_entry.getReference()+"> ?x.";
	}
	
	private String queryForSUBJOFPROPinCaseClassPropertyAndInstanceBeginning(LexicalEntry class_entry,LexicalEntry prop_entry,LexicalEntry inst_entry,boolean flag){
		
            return "SELECT DISTINCT "+countFlag(flag,"lit1")+"  { ?lit1 <"+vocab.rdfType+"> <"+class_entry.getReference()+"> ."
				+ ""+inst_entry.getReference()+"  <"+prop_entry.getReference()+"> ?lit1.";
	}
	
	private String queryForOBJOFPROPinCaseClassPropertyAndInstanceBeginning(LexicalEntry class_entry,LexicalEntry prop_entry,LexicalEntry inst_entry,boolean flag){
		return "SELECT DISTINCT "+countFlag(flag,"lit1")+" { ?lit1 <"+vocab.rdfType+"> <"+class_entry.getReference()+"> ."
				+ "?lit1  <"+prop_entry.getReference()+"> "+typeChecker(inst_entry)+".";
	}
	
	private String AskQueryForSUBJOFPROPinCasePropertyBeginning(LexicalEntry prop_entry){
		return "ASK WHERE { ?x  <"+prop_entry.getReference()+"> ?lit1.";
	}
	
	private String AskQueryForOBJOFPROPinCasePropertyBeginning(LexicalEntry prop_entry){
		return "ASK WHERE { ?lit1  <"+prop_entry.getReference()+"> ?x.";
	}

	private String AskQueryForSUBJOFPROPinCaseClassPropertyAndInstanceBeginning(LexicalEntry class_entry,LexicalEntry prop_entry,LexicalEntry inst_entry){
		return "ASK WHERE { ?lit1 <"+vocab.rdfType+"> <"+class_entry.getReference()+"> ."
				+ ""+inst_entry.getReference()+"  <"+prop_entry.getReference()+"> ?lit1.";
	}
	
	private String AskQueryForOBJOFPROPinCaseClassPropertyAndInstanceBeginning(LexicalEntry class_entry,LexicalEntry prop_entry,LexicalEntry inst_entry){
		return "ASK WHERE { ?lit1 <"+vocab.rdfType+"> <"+class_entry.getReference()+"> ."
				+ "?lit1  <"+prop_entry.getReference()+"> "+typeChecker(inst_entry)+".";
	}
	
	private String queryForSUBJOFPROPinCasePropertyInMiddle(LexicalEntry prop_entry){
		return "?x <"+prop_entry.getReference()+"> ?lit1.";
	}
	
	private String queryForOBJOFPROPinCasePropertyInMiddle(LexicalEntry prop_entry){
		return "?lit1 <"+prop_entry.getReference()+"> ?x. ";
	}
	
	private String queryForSUBJOFPROPinCasePropertyAndInstanceInMiddle(LexicalEntry prop_entry,LexicalEntry inst_entry){
		return " "+inst_entry.getReference()+" <"+prop_entry.getReference()+"> ?lit1.";
	}
	
	private String queryForOBJOFPROPinCasePropertyAndInstanceInMiddle(LexicalEntry prop_entry,LexicalEntry inst_entry){
		return " ?lit1  <"+prop_entry.getReference()+"> "+typeChecker(inst_entry)+". ";
	}
	
	private String queryForSUBJOFPROPinCasePropertyAndInstance(LexicalEntry property_entry,LexicalEntry inst_entry,
                boolean flag){
		
            
            return "SELECT "+countFlag(flag,"x")+" WHERE {"
                                   + " " + inst_entry.getReference() + " <" + property_entry.getReference() + "> ?x . }";
	}

	private String queryForOBJOFPROPinCasePropertyAndInstance(LexicalEntry property_entry,LexicalEntry inst_entry,
                        boolean flag){
		return "SELECT  "+countFlag(flag,"x")+" WHERE {"
                + " ?x <" + property_entry.getReference() + "> " + inst_entry.getReference() + " . }";
	}
	
	private String queryForSUBJOFPROPinCaseProperty(LexicalEntry property_entry){

		return "SELECT DISTINCT ?x WHERE {"
                                   + " ?y <" + property_entry.getReference() + "> ?x . }";
	}
	
	private String queryForOBJOFPROPinCasePropertyAndInstance(LexicalEntry property_entry){
		return "SELECT DISTINCT ?x WHERE {"
                + " ?x <" + property_entry.getReference() + "> ?y . }";
	}
	
	public Set<String> BuildQueryForProperty(PropertyElement property_element){
		Set<String> queries = new HashSet<>();
		for (LexicalEntry property_entry : property_element.getActiveEntries()) {

            switch (property_entry.getSemArg(LexicalEntry.SynArg.OBJECT)) {

               case SUBJOFPROP: 
                         queries.add(queryForSUBJOFPROPinCaseProperty(property_entry));
                    break;
               case OBJOFPROP: 
                         queries.add(queryForOBJOFPROPinCasePropertyAndInstance(property_entry));
                    break;
            }
       }
		
		
		return queries;
	}
	
	public Set<String> BuildQueryForPropertyAndInstance(PropertyElement property_element,InstanceElement instance_element,
                boolean flag){
		Set<String> queries = new HashSet<>();
		for (LexicalEntry property_entry : property_element.getActiveEntries()) {
                    
            switch (property_entry.getSemArg(LexicalEntry.SynArg.OBJECT)) {

               case SUBJOFPROP: 
                    for (LexicalEntry inst_entry : instance_element.getActiveEntries()) {
                         queries.add(queryForSUBJOFPROPinCasePropertyAndInstance(property_entry,inst_entry,flag));
                    }
                    break;
               case OBJOFPROP: 
                    for (LexicalEntry inst_entry : instance_element.getActiveEntries()) {
                         queries.add(queryForOBJOFPROPinCasePropertyAndInstance(property_entry,inst_entry,flag));
                    }
                    break;
            }
       }
		
		return queries;
	}
	
	public Set<String> BuildQueryForClassInstances(List<LexicalEntry> class_entries,boolean flag){
		
		Set<String> queries = new HashSet<>();
		for(LexicalEntry class_entry: class_entries){
		    queries.add(queryForClassInstances(class_entry,flag));
                }
                
		return queries;
	}
	//without class IRI
	public Set<String> BuildQueryForIndividualAndPropery(InstanceElement instance_elements,PropertyElement property_elements,
LexicalEntry.SynArg syn){
		
		Set<String> queries = new HashSet<>();
		
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
	public Set<String> BuildQueryForClassAndIndividualAndProperty(ClassElement class_elements,InstanceElement instance_elements,
PropertyElement property_elements,LexicalEntry.SynArg syn,boolean flag){
		
		Set<String> queries = new HashSet<>();
		
		for(LexicalEntry noun_entry :class_elements.getActiveEntries()){
			for(LexicalEntry nounprop_entry: property_elements.getActiveEntries()){
				String query = "";
                                String check_query = "";
				switch(nounprop_entry.getSemArg(syn)){
					
				case SUBJOFPROP:
					for(LexicalEntry inst_entry : instance_elements.getActiveEntries()){
                                            
                                            query = queryForSUBJOFPROPinCaseClassAndIndividualAndProperty(noun_entry,nounprop_entry,inst_entry,flag);
                                            check_query = askQueryForSUBJOFPROPinCaseClassAndIndividualAndProperty(noun_entry,nounprop_entry,inst_entry,flag);
					if(SPARQLQueryValidator(check_query)) queries.add(query);
					}
					break;
				case OBJOFPROP:
					for(LexicalEntry inst_entry : instance_elements.getActiveEntries()){
						query = queryForSUBJOFPROPinCaseClassAndIndividualAndProperty(noun_entry,nounprop_entry,inst_entry,flag);
                                            check_query = askQueryForSUBJOFPROPinCaseClassAndIndividualAndProperty(noun_entry,nounprop_entry,inst_entry,flag);
					if(SPARQLQueryValidator(check_query)) queries.add(query);
					}
					
					break;
				default:
					break;
				
				}
					
					
			}
			
		}
		
		
		
		return queries;
	}

	public Set<String> BuildQueryForClassAndProperty(ClassElement class_elements,PropertyElement property_elements,
LexicalEntry.SynArg syn,boolean flag){
		
		Set<String> queries = new HashSet<>();
		
		for(LexicalEntry class_entry : class_elements.getActiveEntries()){
			for(LexicalEntry property_entry : property_elements.getActiveEntries()){
				
				switch(property_entry.getSemArg(syn)){
				
				case SUBJOFPROP:
				
                                    queries.add(queryForSUBJOFPROPinCaseClassAndProperty(class_entry,property_entry,flag));
					break;
					
				case OBJOFPROP:
					queries.add(queryForOBJOFPROPinCaseClassAndProperty(class_entry,property_entry,flag));
					break;
				
				default:
						break;
				
				}
				
			}
			
		}
		
		
		
		return queries;
		
	}

	public Set<String> BuildQueryForClassAnd2Properties(ClassElement class_elements,PropertyElement property_element1,
PropertyElement property_element2,LexicalEntry.SynArg syn1,LexicalEntry.SynArg syn2){
		
		Set<String> queries = new HashSet<>();
		
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

        	public Set<String> BuildQueryForInstanceAnd2Properties(InstanceElement instance_elements,PropertyElement property_element1,
PropertyElement property_element2,LexicalEntry.SynArg syn1,LexicalEntry.SynArg syn2){
		
		Set<String> queries = new HashSet<>();
		
		for(LexicalEntry instance_entry : instance_elements.getActiveEntries()){
		for(LexicalEntry property_entry1 : property_element1.getActiveEntries()){
			
			String query ="" ;
			
			switch(property_entry1.getSemArg(syn1)){
				
				case SUBJOFPROP:
					
					query = queryForSUBJOFPROPinCaseInstanceAndPropertyBeginning(instance_entry,property_entry1);
					
					for(LexicalEntry property_entry2 : property_element2.getActiveEntries()){
						
						switch(property_entry2.getSemArg(syn2)){
						
						case SUBJOFPROP:
							query += queryForSUBJOFPROPinCaseInstanceAndPropertyEnding(instance_entry,property_entry2);
							queries.add(query);
							break;
						case OBJOFPROP:
							query += queryForOBJOFPROPinCaseInstanceAndPropertyEnding(instance_entry,property_entry2);
							queries.add(query);
							break;
						default:
							break;
						}
						
					}
					
					
					break;
					
				case OBJOFPROP:
					for(LexicalEntry property_entry2 : property_element2.getActiveEntries()){
						
						query = queryForOBJOFPROPinCaseInstanceAndPropertyBeginning(instance_entry,property_entry1);
						
						switch(property_entry2.getSemArg(syn2)){
						
						case SUBJOFPROP:
							query += queryForSUBJOFPROPinCaseInstanceAndPropertyEnding(instance_entry,property_entry2);
							queries.add(query);
							break;
						case OBJOFPROP:
							query += queryForOBJOFPROPinCaseInstanceAndPropertyEnding(instance_entry,property_entry2);
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
        
	public Set<String> BuildQueryForClassPropertyAndInstance(ClassElement class_elements, PropertyElement property_element,InstanceElement instance_element,
			LexicalEntry.SynArg syn){
		
		Set<String> queries = new HashSet<>();
		
		
		return queries;
	}
	
	
	public Set<String> BuildQueryForClassAnd2PropertyAndIndividual(ClassElement class_elements,PropertyElement property1_elements,
			InstanceElement indv1_elements,LexicalEntry.SynArg syn1,PropertyElement property2_elements,LexicalEntry.SynArg syn2
                        ,boolean flag ){
		Set<String> queries = new HashSet<>();
		String query ="";
		String check_query = "";
		
		for(LexicalEntry class_element: class_elements.getActiveEntries()){
			for(LexicalEntry property1_element: property1_elements.getActiveEntries()){
				
				switch(property1_element.getSemArg(syn1)){
					
					case SUBJOFPROP:
						
						for(LexicalEntry indv1_element:indv1_elements.getActiveEntries()){
							
							query = queryForSUBJOFPROPinCaseClassPropertyAndInstanceBeginning(class_element,property1_element,indv1_element,flag);
							check_query = AskQueryForSUBJOFPROPinCaseClassPropertyAndInstanceBeginning(class_element,property1_element,indv1_element);
							
							for(LexicalEntry property2_element:property2_elements.getActiveEntries()){
							
                                                            String query_mid = "";
								switch(property2_element.getSemArg(syn2)){
									
									case SUBJOFPROP:
                                                                            
                                                                                query_mid =queryForSUBJOFPROPinCasePropertyInMiddle(property2_element)+"}" ;
										query += query_mid;
										check_query+= query_mid;
									case OBJOFPROP:
                                                                                query_mid = queryForOBJOFPROPinCasePropertyInMiddle(property2_element)+"}" ;
										query += query_mid;
										check_query+= query_mid;
							
                                                                }
                                                        if(!query.isEmpty() && SPARQLQueryValidator(check_query)) queries.add(query);
                                                        query = query.replace(query_mid,"");
                                                        check_query = check_query.replace(query_mid,"");
                                                        }
                                                }
						
					case OBJOFPROP:
						
						for(LexicalEntry indv1_element:indv1_elements.getActiveEntries()){

							query = queryForOBJOFPROPinCaseClassPropertyAndInstanceBeginning(class_element,property1_element,indv1_element,flag);
							check_query = AskQueryForOBJOFPROPinCaseClassPropertyAndInstanceBeginning(class_element,property1_element,indv1_element);
							
							for(LexicalEntry property2_element:property2_elements.getActiveEntries()){
								
                                                            String query_mid = "";
                                                            switch(property2_element.getSemArg(syn2)){
								
										case SUBJOFPROP:
                                                                                        query_mid = queryForSUBJOFPROPinCasePropertyInMiddle(property2_element)+"}";
											query += query_mid;
											check_query+= query_mid;
										case OBJOFPROP:
                                                                                        query_mid = queryForOBJOFPROPinCasePropertyInMiddle(property2_element)+"}";
											query += query_mid;
											check_query+= query_mid;
							}
                                                            if(!query.isEmpty() && SPARQLQueryValidator(check_query)) queries.add(query);
                                                        query = query.replace(query_mid,"");
                                                        check_query = check_query.replace(query_mid,"");
                                                                }
                                                        
						}
                                                                
							
				}
						
					}
				}
		
		
		
		
		return queries;
		
	}
	
	
	public Set<String> BuildQueryForClassAnd2PropertyAnd2Individual(ClassElement class_elements,PropertyElement property1_elements,
			InstanceElement indv1_elements,LexicalEntry.SynArg syn1,PropertyElement property2_elements,InstanceElement indv2_elements
			,LexicalEntry.SynArg syn2,boolean flag){
		Set<String> queries = new HashSet<>();
		String query ="";
		String check_query = "";
		
		for(LexicalEntry class_element: class_elements.getActiveEntries()){
			for(LexicalEntry property1_element: property1_elements.getActiveEntries()){
				
				switch(property1_element.getSemArg(syn1)){
					
					case SUBJOFPROP:
						
						for(LexicalEntry indv1_element:indv1_elements.getActiveEntries()){
							
							query = queryForSUBJOFPROPinCaseClassPropertyAndInstanceBeginning(class_element,property1_element,indv1_element,flag);
							check_query = AskQueryForSUBJOFPROPinCaseClassPropertyAndInstanceBeginning(class_element,property1_element,indv1_element);
							
							for(LexicalEntry property2_element:property2_elements.getActiveEntries()){
								
                                                                String query_mid = "";
								switch(property2_element.getSemArg(syn2)){
									
									case SUBJOFPROP:
										for(LexicalEntry indv2_element:indv2_elements.getActiveEntries()){
                                                                                    query_mid = queryForSUBJOFPROPinCasePropertyAndInstanceInMiddle(property2_element,indv2_element)+"}" ;
                                                                                    query += query_mid;
                                                                                    check_query+= query_mid;
										}break;
										
									case OBJOFPROP:
										for(LexicalEntry indv2_element:indv2_elements.getActiveEntries()){
                                                                                    query_mid = queryForOBJOFPROPinCasePropertyAndInstanceInMiddle(property2_element,indv2_element)+"}";
                                                                                    query += query_mid;
										    check_query+= query_mid;
										}break;
								}
                                                                
                                                           if(!query.isEmpty() && SPARQLQueryValidator(check_query)) queries.add(query);
                                                           query = query.replace(query_mid, "");
                                                           check_query = check_query.replace(query_mid, "");
							}
						}
						
					case OBJOFPROP:
						
						for(LexicalEntry indv1_element:indv1_elements.getActiveEntries()){

							query = queryForOBJOFPROPinCaseClassPropertyAndInstanceBeginning(class_element,property1_element,indv1_element,flag);
							check_query = AskQueryForOBJOFPROPinCaseClassPropertyAndInstanceBeginning(class_element,property1_element,indv1_element);
							
							for(LexicalEntry property2_element:property2_elements.getActiveEntries()){
                                                            String query_mid = "";	
                                                            switch(property2_element.getSemArg(syn2)){
								
										case SUBJOFPROP:
											for(LexicalEntry indv2_element:indv2_elements.getActiveEntries()){
                                                                                            query_mid = queryForSUBJOFPROPinCasePropertyAndInstanceInMiddle(property2_element,indv2_element)+"}" ;
                                                                                            query += query_mid;
                                                                                            check_query+= query_mid;
                                                                                        }
										case OBJOFPROP:
											for(LexicalEntry indv2_element:indv2_elements.getActiveEntries()){
                                                                                            query_mid = queryForOBJOFPROPinCasePropertyAndInstanceInMiddle(property2_element,indv2_element)+"}";
                                                                                            query += query_mid;
                                                                                            check_query+= query_mid;
                                                                                        }
							}
		
                                                
                                                if(!query.isEmpty() && SPARQLQueryValidator(check_query)) queries.add(query);
                                                         query= query.replace(query_mid, "");
                                                         check_query = check_query.replace(query_mid, "");     
							}
						}
				
				}
						
					}
				}
		
		
		
		
		return queries;
		
	}
	
	
	public Set<String> BuildQueryForPropertyAndgYearAndNameLiteral(PropertyElement property_elements,InstanceElement gYear_elements,
InstanceElement name_elements, LexicalEntry.SynArg syn){
		
		Set<String> queries = new HashSet<>();
		String query = "";
		String check_query="";
		for(LexicalEntry property_element: property_elements.getActiveEntries()){
			for(LexicalEntry name_element : name_elements.getActiveEntries()){
				for(LexicalEntry gYear_element : gYear_elements.getActiveEntries()){
                                    
                                    if (property_element.getSemArg(syn)== null) continue;
                                    switch(property_element.getSemArg(syn)){
 			
                                        case SUBJOFPROP:
                                            query = DomainqueryForincasegYearNameandProperty(gYear_element,name_element,property_element);
                                            check_query = DomainAskQueryForincasePropertyAndgYearAndNameLiteral(gYear_element,name_element,property_element);
                                            
                                            break;
                                        case OBJOFPROP:
                                            query = RangequeryForincasegYearNameandProperty(gYear_element,name_element,property_element);
                                            check_query = RangeAskQueryForincasePropertyAndgYearAndNameLiteral(gYear_element,name_element,property_element);
                                            
                                            break;
 			}
					
				}
				if(SPARQLQueryValidator(check_query)&& !queries.contains(query)) queries.add(query);
			}
		}
		
		
		return queries;
	}

	public Set<String> BuildQueryFor2PropertyAndNameLiteralAndGYearLiteral(PropertyElement property_elements1,LexicalEntry.SynArg syn1,
			PropertyElement property_elements2,LexicalEntry.SynArg syn2,InstanceElement name_literals,InstanceElement gYear_literals){
		
		Set<String> queries = new HashSet<>();
		
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
									query += label("?x","?l1",DateProperties)+" FILTER regex(?l1,\""+gYear_entry.getCanonicalForm()+"\"). " 
                                                                              + label("?x","?l2",LabelProperties)+" FILTER regex(?l2,\""+name_entry.getCanonicalForm()+"\"). ";
                                                                        check_query += label("?x","?l1",DateProperties)+" FILTER regex(?l1,\""+gYear_entry.getCanonicalForm()+"\"). " 
                                                                              + label("?x","?l2",LabelProperties)+" FILTER regex(?l2,\""+name_entry.getCanonicalForm()+"\"). ";}
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
									query += label("?x","?l1",DateProperties)+" FILTER regex(?l1,\""+gYear_entry.getCanonicalForm()+"\"). " 
                                                                              + label("?x","?l2",LabelProperties)+" FILTER regex(?l2,\""+name_entry.getCanonicalForm()+"\"). ";
                                                                        check_query += label("?x","?l1",DateProperties)+" FILTER regex(?l1,\""+gYear_entry.getCanonicalForm()+"\"). " 
                                                                              + label("?x","?l2",LabelProperties)+" FILTER regex(?l2,\""+name_entry.getCanonicalForm()+"\"). ";}
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
						
						query = queryForSUBJOFPROPinCasePropertyBeginning(property_entry1);
						check_query = AskQueryForSUBJOFPROPinCasePropertyBeginning(property_entry1);
						
						switch(property_entry2.getSemArg(syn2)){
						

						case SUBJOFPROP:
							query += queryForSUBJOFPROPinCasePropertyInMiddle(property_entry2);
							check_query += queryForSUBJOFPROPinCasePropertyInMiddle(property_entry2) ;
							for(LexicalEntry gYear_entry : gYear_literals.getActiveEntries()){
								for(LexicalEntry name_entry : name_literals.getActiveEntries()){
									query += label("?x","?l1",DateProperties)+" FILTER regex(?l1,\""+gYear_entry.getCanonicalForm()+"\"). " 
                                                                              + label("?x","?l2",LabelProperties)+" FILTER regex(?l2,\""+name_entry.getCanonicalForm()+"\"). ";
                                                                        check_query += label("?x","?l1",DateProperties)+" FILTER regex(?l1,\""+gYear_entry.getCanonicalForm()+"\"). " 
                                                                              + label("?x","?l2",LabelProperties)+" FILTER regex(?l2,\""+name_entry.getCanonicalForm()+"\"). ";
								}
							}
							query += "}";
							check_query += "}";
							System.out.println("SQB 3: "+query);
							if(SPARQLQueryValidator(check_query)) queries.add(query);
							break;
						case OBJOFPROP:
							query += queryForSUBJOFPROPinCasePropertyInMiddle(property_entry2);
							for(LexicalEntry gYear_entry : gYear_literals.getActiveEntries()){
								for(LexicalEntry name_entry : name_literals.getActiveEntries()){
									query += label("?x","?l1",DateProperties)+" FILTER regex(?l1,\""+gYear_entry.getCanonicalForm()+"\"). " 
                                                                              + label("?x","?l2",LabelProperties)+" FILTER regex(?l2,\""+name_entry.getCanonicalForm()+"\"). ";
									check_query += label("?x","?l1",DateProperties)+" FILTER regex(?l1,\""+gYear_entry.getCanonicalForm()+"\"). " 
                                                                              + label("?x","?l2",LabelProperties)+" FILTER regex(?l2,\""+name_entry.getCanonicalForm()+"\"). ";
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