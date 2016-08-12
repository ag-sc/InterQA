package interQA.lexicon;

import interQA.elements.Element;
import interQA.Config.Language;
import interQA.Config.USECASE;
import interQA.main.JenaExecutorCacheSelect;
import interQA.main.JenaExecutorCacheAsk;
import interQA.patterns.query.IncrementalQuery;
import interQA.patterns.query.QueryBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.graph.Node;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementUnion;

/**
 *
 * @author cunger
 */

public class DatasetConnector {

    JenaExecutorCacheAsk    cacheAsk = new JenaExecutorCacheAsk();
    JenaExecutorCacheSelect cacheSel = new JenaExecutorCacheSelect();

    String endpoint;
    Vocabulary vocab;
    List<String> labelProperties;
    Language lang;


    public DatasetConnector(String url, Language language, USECASE usecase) {

        endpoint = url;
        vocab = new Vocabulary();
        lang = language;
        labelProperties = new ArrayList<>();
        
        switch (usecase) {
                
            default: {
                labelProperties.add("http://www.w3.org/2000/01/rdf-schema#label");
            }
        }
    }

    public void cacheUsageReport(PrintStream ps){
           ps.println(cacheAsk.cacheUsageReport() + " " + cacheSel.cacheUsageReport());
    }

    public void cacheDump(PrintStream ps){
        cacheAsk.dump(ps);
        cacheSel.dump(ps);
    }
    public void saveCacheToDisk(){
        cacheAsk.saveCacheToDisk(endpoint);
        cacheSel.saveCacheToDisk(endpoint);
    }
    public void interactiveExplorer(){
        cacheSel.interactiveExplorer();
    }

    public String getEndpoint(){
        return endpoint;
    }
    
    
    public void filter(Element element, QueryBuilder builder, String var) {
        
        for (LexicalEntry entry : element.getActiveEntries()) {
            
            boolean keep = false;
            
            for (IncrementalQuery query : builder.getQueries()) {
                 IncrementalQuery instantiated = builder.instantiate(var,entry,query);
                 Query q = instantiated.assembleAsAsk(vocab,false);
                 if (cacheAsk.executeWithCache(endpoint,query.prettyPrint(q))) {
                     keep = true;
                     break;
                 }
            }
            
            if (!keep) element.removeFromIndex(entry);
        }
    }
    
    public void fillInstances(Element element, QueryBuilder builder, String i_var) {
                        
        String label_var = "l";
        
        for (IncrementalQuery iq : builder.getQueries()) {
              
              IncrementalQuery copy = iq.clone();
              copy.getBody().addElement(label(i_var,label_var));
              Query query = copy.assemble(vocab,false);
              query.setQueryResultStar(true);
              String querystring = copy.prettyPrint(query);
                                          
              ResultSet results = cacheSel.executeWithCache(endpoint,querystring);
            
              while (results.hasNext()) {
                                  
                QuerySolution result   = results.nextSolution();
                RDFNode       instance = result.get(i_var);
                RDFNode       label    = result.get(label_var);
                                                
                if (instance != null) {
                    
                    LexicalEntry entry = new LexicalEntry();

                    if (instance.isURIResource()) {
                                                
                        entry.setReference(instance.asResource().getURI());
                        
                        if (label != null && label.isLiteral() &&
                           (label.asLiteral().getLanguage() == null ||
                            label.asLiteral().getLanguage().equals(lang.toString().toLowerCase()))) {
                                                        
                            entry.setCanonicalForm(label.asLiteral().getString());
                            element.addToIndex(label.asLiteral().getString(),entry);
                        }
                    }
                    else if (instance.isLiteral()) {
                                                                        
                        entry.setLiteralNode(instance);
                        entry.setAsLiteral();
                        
                        String form;
                        if (instance.asLiteral().getDatatypeURI().equals(vocab.xsd_gYear)) {
                            form = instance.asLiteral().getLexicalForm().substring(0,4);
                        } else {
                            form = instance.asLiteral().getLexicalForm();
                        }
                        entry.setCanonicalForm(form);
                        element.addToIndex(form,entry);
                    }
                    
                    if (!element.isStringElement()) {
                         element.getContext().put(entry,iq.getTriples());
                    }
                }
            }
        }  
        
        // TODO if SPRINGER, then additonally get confName/Acronym + confYear

    }
    
    private ElementOptional label(String i_var, String label_var) {
        
        if (labelProperties.size() == 1) {
            ElementGroup u = new ElementGroup();
            u.addTriplePattern(new Triple(toVar(i_var),toResource(labelProperties.get(0)),toVar(label_var)));
            return new ElementOptional(u);
        }
        else {
            ElementUnion union = new ElementUnion();
            for (String p : labelProperties) { 
                 ElementGroup u = new ElementGroup(); 
                 u.addTriplePattern(new Triple(toVar(i_var),toResource(p),toVar(label_var)));
                 union.addElement(u);
            }
            return new ElementOptional(union);
        }
    }

    
    // AUX for nodes
    
    public Var toVar(String v) {
        return Var.alloc(v);
    }
    
    public Node toResource(String uri) {
        return ResourceFactory.createResource(uri).asNode();
    }
}
