package interQA.patterns.query;

import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Vocabulary;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.core.Var;

/**
 *
 * @author cunger
 */
public class QueryBuilder {
    
      
    Set<IncrementalQuery> queries;
    
    Vocabulary vocab;    
    
    
    public QueryBuilder() {
        
        queries = new HashSet<>();
        vocab   = new Vocabulary();
    }
    
    public Set<IncrementalQuery> getQueries() {
        
        return queries;
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
    
    public void addTriple(Triple t) {     
                
        for (IncrementalQuery q : queries) {
             q.addTriple(t);
        }
    }
    
    public void addUninstantiatedTriple(String s_var,String p_var,String o_var) {
        
        addTriple(new Triple(Var.alloc(s_var),Var.alloc(p_var),Var.alloc(o_var)));
    }
    
    public void addUninstantiatedTypeTriple(String s_var,String o_var) {
        
        addTriple(new Triple(Var.alloc(s_var),toResource(vocab.sortal_predicate),Var.alloc(o_var)));
    }
    
    public Set<IncrementalQuery> instantiate(String var, LexicalEntry entry) {
    
        Set<IncrementalQuery> copies = new HashSet<>();

        Set<Triple> del;
        Set<Triple> add;
        
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
                     LexicalEntry.SemArg sem = entry.getSemArg(LexicalEntry.SynArg.SUBJECT);
                     if (sem != null && sem == LexicalEntry.SemArg.OBJOFPROP) {
                         add.add(new Triple(t.getObject(),toResource(entry.getReference()),t.getSubject()));
                     } else {
                         add.add(new Triple(t.getSubject(),toResource(entry.getReference()),t.getObject()));
                     }
                 }
                 if (t.getObject().matches(Var.alloc(var))) {
                     del.add(t);
                     add.add(new Triple(t.getSubject(),t.getPredicate(),toResource(entry.getReference())));
                 }
            }
            
            for (Triple t : del) copy.removeTriple(t);
            for (Triple t : add) copy.addTriple(t);
            
            copies.add(copy);
        }
         
        return copies;
    }
    
    public void instantiate(String var, List<LexicalEntry> entries) {

        Set<IncrementalQuery> copies = new HashSet<>();
        
        for (LexicalEntry entry : entries) {
            copies.addAll(instantiate(var,entry));
        }
        
        queries = copies;
    }
        
        
    public Set<String> returnQueries(boolean asFinal) {
        
        Set<String> out = new HashSet<>();
        
        for (IncrementalQuery query : queries) { 
            
             Query q  = query.assemble(asFinal); 
             if  ( q != null) { 
                 out.add(q.toString().replaceAll("\\n"," ").replaceAll("\\s+"," ").trim());
             }
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
