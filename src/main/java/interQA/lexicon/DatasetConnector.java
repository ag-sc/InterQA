package interQA.lexicon;

import interQA.Config;
import interQA.elements.Element;
import interQA.Config.Language;
import interQA.Config.Usecase;
import interQA.main.JenaExecutorCacheSelect;
import interQA.main.JenaExecutorCacheAsk;
import interQA.main.URILabelCache;
import interQA.patterns.query.IncrementalQuery;
import interQA.patterns.query.QueryBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.graph.Node;

import org.apache.jena.query.Query;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.core.Var;

/**
 *
 * @author cunger
 */

public class DatasetConnector {

    JenaExecutorCacheAsk    cacheAsk = new JenaExecutorCacheAsk();
    JenaExecutorCacheSelect cacheSel = new JenaExecutorCacheSelect(); //Naive Extraction by default
    URILabelCache           cacheLabels = null;

    String endpoint;
    Vocabulary vocab;
    List<String> labelProperties ;
    Language lang;
    String gYearProperty=null;
    Usecase usecase ;
    
    public DatasetConnector(String url, Language language, Usecase usecase) {

        endpoint = url;
        vocab = new Vocabulary();
        lang = language;
        labelProperties = new ArrayList<>();
        this.usecase = usecase;
        cacheLabels = new URILabelCache(language.toString().toLowerCase()); //This load the cache from disk... takes time.

        switch (usecase) {
            
            case SPRINGER: {
                labelProperties.add("http://lod.springer.com/data/ontology/property/confName");
                labelProperties.add("http://lod.springer.com/data/ontology/property/confAcronym");
                gYearProperty = "http://lod.springer.com/data/ontology/property/confYear";
                break;
            }
            default: {
                labelProperties.add("http://www.w3.org/2000/01/rdf-schema#label");
                break;
            }
        }
    }

    public void setCacheMode(Config.ExtractionMode extractionMode, boolean useHistoricalCache){
        cacheSel.setCacheMode(extractionMode, useHistoricalCache);
        cacheAsk.setCacheMode(useHistoricalCache);
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

    /**
     * This method modifies element, adding the appropriated instances
     * Fills instances
     * @param element
     * @param builder
     * @param i_var
     */
    public void fillInstances(Element element, QueryBuilder builder, String i_var) {
                        
                for (IncrementalQuery iq : builder.getQueries()) {
              
                    IncrementalQuery copy = iq.clone();
                    Query query = copy.assemble(vocab,false);
                    query.setQueryResultStar(true);
                    String querystring = copy.prettyPrint(query);
                    
                    ResultSet results = cacheSel.executeWithCache(endpoint,querystring);

                    while (results.hasNext()) {

                      QuerySolution result   = results.nextSolution();
                      RDFNode       instance = result.get(i_var);

                      if (instance != null) {

                          LexicalEntry entry = new LexicalEntry();

                          if (instance.isURIResource()) {

                              String uri   = instance.asResource().getURI();
                              String label = cacheLabels.getLabel(uri);
                              entry.setReference(uri);
                              entry.setCanonicalForm(label);
                              element.addToIndex(label,entry);
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
    }

    
    // AUX for nodes
    
    public Var toVar(String v) {
        return Var.alloc(v);
    }
    
    public Node toResource(String uri) {
        return ResourceFactory.createResource(uri).asNode();
    }
}
