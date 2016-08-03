package interQA.patterns.query;

import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Vocabulary;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.core.Var;

/**
 *
 * @author cunger
 */
public class QueryBuilder {
    
      
    Set<IncrementalQuery> queries;
    
    Vocabulary vocab;
    
    String sortal_predicate = sortal_predicate = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    
    
    
    public QueryBuilder() {
        
        queries = new HashSet<>();
        vocab   = new Vocabulary();
    }
    
    public void addProjVar(String var) {
        
        for (IncrementalQuery q : queries) {
             q.projvars.add(var);
        }
    }
    
    public void addCountVar(String var) {
        
        for (IncrementalQuery q : queries) {
             q.countvars.add(var);
        }
    }
        
    public void addTypeTriple(String var, List<LexicalEntry> entries) {
        
        Set<IncrementalQuery> copies = new HashSet<>();
        
        for (LexicalEntry entry : entries) {
        for (IncrementalQuery q : queries) { 
             IncrementalQuery copy = q.clone();
             copy.addTriple(new Triple(toVar(var),toResource(sortal_predicate),toResource(entry.getReference())));
             copies.add(copy);
        }}

        queries = copies;
    }
    
    public void addTriple(String v1, List<LexicalEntry> entries, String v2) {
        
        Set<IncrementalQuery> copies = new HashSet<>();
        
        for (LexicalEntry entry : entries) {
            
            Node s = Var.alloc(v1);
            Node o = Var.alloc(v2);
            Node p = toResource(entry.getReference());
            
            Triple t = null;
        
            switch (entry.getSemArg(LexicalEntry.SynArg.OBJECT)) {
                case OBJOFPROP: {
                     t = new Triple(s,p,o);
                     break;
                }
                case SUBJOFPROP: {
                     t = new Triple(o,p,s);
                     break;
                }
                default: break;
            }
            
            if (t != null) {
                for (IncrementalQuery q : queries) { 
                     IncrementalQuery copy = q.clone();
                     copy.addTriple(t);
                     copies.add(copy);
                }
            }
        }

        queries = copies;
    }
    
    public void addTriple(Triple t) {     
                
        for (IncrementalQuery q : queries) {
             q.addTriple(t);
        }
    }
    
    public void instantiate(String var, List<LexicalEntry> entries) {

        Set<IncrementalQuery> copies = new HashSet<>();

        Set<Triple> del;
        Set<Triple> add;
        
        for (LexicalEntry entry : entries) {
        for (IncrementalQuery q : queries) {
            
            IncrementalQuery copy = q.clone();
            
            del = new HashSet<>();
            add = new HashSet<>();
            
            for (Triple t : q.triples) {
                
                 if (t.getSubject().matches(Var.alloc(var))) {
                     del.add(t);
                     add.add(new Triple(toResource(entry.getReference()),t.getPredicate(),t.getObject()));
                 }
                 if (t.getPredicate().matches(Var.alloc(var))) {
                     del.add(t);
                     add.add(new Triple(t.getSubject(),toResource(entry.getReference()),t.getObject()));
                 }
                 if (t.getObject().matches(Var.alloc(var))) {
                     del.add(t);
                     add.add(new Triple(t.getSubject(),t.getPredicate(),toResource(entry.getReference())));
                 }
            }
            
            for (Triple t : del) copy.removeTriple(t);
            for (Triple t : add) copy.addTriple(t);
        }}
        
        queries = copies;
    }
        
        
    public Set<String> returnQueries() {
        
        Set<String> out = new HashSet<>();
        
        String q;
        for (IncrementalQuery query : queries) { 
             q = query.assemble().toString();
             q = q.replaceAll("\\n"," ").replaceAll("\\s+"," ");
             out.add(q);
        }
        
        return out;
    }
    
    
    // AUX for nodes
    
    public Var toVar(String v) {
        return Var.alloc(v);
    }
    
    public Node toResource(String uri) {
        return ResourceFactory.createResource(uri).asNode();
    }
    
    
    // RESET
    
    public void reset() {
        
        queries = new HashSet<>();
        queries.add(new IncrementalQuery());
        
        vocab.reset();
    }
    
}
