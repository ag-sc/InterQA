package interQA.lexicon;

import interQA.elements.Element;
import interQA.elements.InstanceElement;
import interQA.lexicon.LexicalEntry.Language;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import interQA.main.JenaExecutorCacheSelect;
import interQA.main.JenaExecutorCacheAsk;
import interQA.patterns.query.IncrementalQuery;
import interQA.patterns.query.QueryBuilder;
import org.apache.jena.query.Query;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author cunger
 */

public class DatasetConnector {

    JenaExecutorCacheAsk    cacheAsk = new JenaExecutorCacheAsk();
    JenaExecutorCacheSelect cacheSel = new JenaExecutorCacheSelect();

    String endpoint;
    Vocabulary vocab = new Vocabulary();
    List<String> labelProperties;
    Language lang;


    public DatasetConnector(String url, Language language) {

       this(url,language,Arrays.asList("http://www.w3.org/2000/01/rdf-schema#label"));
    }

    public DatasetConnector(String url, Language language, List<String> props) {

        endpoint = url;
        lang = language;
        labelProperties = props;
    }

    public void cacheUsageReport(PrintStream ps){
           ps.println(cacheAsk.cacheUsageReport() + " " + cacheSel.cacheUsageReport());
    }

    public void cacheDump(PrintStream ps){
        cacheAsk.dump(ps);
        cacheSel.dump(ps);
    }
    public void saveCacheToDisk(){
        cacheAsk.saveCacheToDisk();
        cacheSel.saveCacheToDisk();
    }

    public String getEndpoint(){
        return endpoint;
    }
    
    
    public void filter(Element element, QueryBuilder builder, String var) {
        
        for (LexicalEntry entry : element.getActiveEntries()) {
            
            boolean keep = false;
            
            for (IncrementalQuery query : builder.getQueries()) {
                 IncrementalQuery instantiated = builder.instantiate(var,entry,query);
                 Query q = instantiated.assembleAsAsk(false);
                 if (cacheAsk.executeWithCache(endpoint,query.prettyPrint(q))) {
                     keep = true;
                     break;
                 }
            }
            
            if (!keep) element.removeFromIndex(entry);
        }
    }
    
    public void fillInstances(Element element, QueryBuilder builder, String var) {
                
        for (IncrementalQuery iq : builder.getQueries()) {
               
              Query query = iq.assemble(false);
              query.setQueryResultStar(true);
                           
              ResultSet results = cacheSel.executeWithCache(endpoint,iq.prettyPrint(query));
            
              while (results.hasNext()) {
                
                QuerySolution result = results.nextSolution();
                RDFNode       node   = result.get(var);
                                                
                if (node != null) {
                    
                    LexicalEntry entry = new LexicalEntry();

                    if (node.isURIResource()) {
                        
                        String uri = node.asResource().getURI();
                        
                        entry.setReference(uri);
                        
                        List<String> labels = getLabels(uri);
                        if (!labels.isEmpty()) {
                            entry.setCanonicalForm(labels.get(0));
                        }
                        for (String label : labels) {
                            element.addToIndex(label,entry);
                        }
                    }
                    else if (node.isLiteral()) {
                        
                        entry.setLiteralNode(node);
                        entry.setAsLiteral();
                        entry.setCanonicalForm(node.asLiteral().getString());
                        element.addToIndex(node.asLiteral().getLexicalForm(),entry);
                    }
                    
                    if (!element.isStringElement()) {
                         element.getContext().put(entry,iq.getTriples());
                    }
                }
            }
        }        
    }

    private List<String> getLabels(String uri) {
        
        List<String> labels = new ArrayList<>();
        
        String query = "SELECT DISTINCT ?l { " 
                     +  label(uri,"?l")
                     + "}";
        
        ResultSet results = cacheSel.executeWithCache(endpoint,query);
        
        while (results.hasNext()) {

            QuerySolution result = results.nextSolution();
            RDFNode       node   = result.get("l");
            
            if (node.isLiteral() &&
               (node.asLiteral().getLanguage() == null ||
                node.asLiteral().getLanguage().equals(lang.toString().toLowerCase()))) {
            
                labels.add(node.asLiteral().getString());
            }
        }
                
        return labels;
    }
    
    private String label(String uri, String var) {

        String out;

        if (labelProperties.size() == 1) {
            out = "<"+uri+">" + " <" + labelProperties.get(0) + "> " + var + " .";
        }
        else if (labelProperties.size() > 1) {
            out = "{ <"+uri+"> <" + labelProperties.get(0) + "> " + var + " . }";
            for (String prop : labelProperties.subList(1,labelProperties.size())) {
                 out += " UNION { <"+uri+"> <" + prop + "> " + var + " . }";
            }
        }
        else out = "" ;

        return out;
    }

}
