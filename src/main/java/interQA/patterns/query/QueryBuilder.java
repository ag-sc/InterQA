package interQA.patterns.query;

import interQA.elements.Element;
import interQA.lexicon.LexicalEntry;
import interQA.lexicon.Vocabulary;
import java.util.HashSet;
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
    
    Vocabulary vocab;    
    Set<String> placeholders;
    Set<IncrementalQuery> queries;
    
    
    public QueryBuilder() {
        
        vocab = new Vocabulary();
        placeholders = new HashSet<>();
        queries = new HashSet<>();
    }
    
    public String placeholder(String var) {
        
        placeholders.add(var);
        return var;
    }
    
    public boolean isorwillbeURI(Node node) {
        
        return (node.isURI() || (node.isVariable() && placeholders.contains(node.getName())));
    }
    
    public Set<IncrementalQuery> getQueries() {
        
        return queries;
    }
    
    public void setQueries(Set<IncrementalQuery> qs) {
        
        queries = qs;
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
    
    public void instantiate(String var, Element element) {
        
        instantiate(var,element,false);
    }

    public void instantiate(String var, Element element, boolean ignoreContext) {
    
        Set<IncrementalQuery> copies = new HashSet<>();
        
        for (LexicalEntry entry : element.getActiveEntries()) {
        for (IncrementalQuery q : queries) {     
            if (!ignoreContext && 
                !element.isStringElement() && 
                 element.getContext().containsKey(entry) &&
                !element.getContext().get(entry).equals(q.getTriples())) {
                 continue;
            }
            
            IncrementalQuery copy = instantiate(var,entry,q);
                        
            copies.add(copy);            
        }}
         
        queries = copies;
    }
    
    public IncrementalQuery instantiate(String var, LexicalEntry entry, IncrementalQuery query) {
        
            IncrementalQuery copy = query.clone();
            
            Set<Triple> del = new HashSet<>();
            Set<Triple> add = new HashSet<>();
            
            for (Triple t : query.triples) {
                
                 if (t.getSubject().matches(Var.alloc(var))) {
                     del.add(t);
                     add.add(new Triple(toResource(entry.getReference()),t.getPredicate(),t.getObject()));
                 }
                 if (t.getPredicate().matches(Var.alloc(var))) {
                     del.add(t);
                     if (orderAlright(t,entry)) {
                         add.add(new Triple(t.getSubject(),toResource(entry.getReference()),t.getObject()));
                     } else if (isorwillbeURI(t.getObject())) {
                         add.add(new Triple(t.getObject(),toResource(entry.getReference()),t.getSubject()));
                     } else {
                         del.remove(t);
                     }
                 }
                 if (t.getObject().matches(Var.alloc(var))) {
                     del.add(t);
                     if (entry.isLiteral()) {
                         add.add(new Triple(t.getSubject(),t.getPredicate(),entry.getLiteralNode().asNode()));
                     } else {
                         add.add(new Triple(t.getSubject(),t.getPredicate(),toResource(entry.getReference())));
                     }
                 }
            }
            
            for (Triple t : del) copy.removeTriple(t); 
            for (Triple t : add) copy.addTriple(t); 

            return copy;
    }
    
    private boolean orderAlright(Triple t, LexicalEntry entry) {
        
        // If the object is or will be a resource or literal, and the entry has a linear argument mapping, then the order is alright.
        if ((!t.getObject().isVariable() || placeholders.contains(t.getObject().getName())) 
            & entry.getArgumentMapping() == LexicalEntry.ArgumentMapping.LINEAR) 
            return true;
        
        // Analogously, 
        // If the subject is or will be a resource or literal, and the entry has a reverse argument mapping, then the order is alright.
        if ((!t.getSubject().isVariable() || placeholders.contains(t.getSubject().getName())) 
            & entry.getArgumentMapping() == LexicalEntry.ArgumentMapping.REVERSE) 
            return true;
        
        // In all other cases, the order is not alright (and should be reversed).
        return false;
    }
        
        
    public Set<String> returnQueries(boolean onlyInstantiatedTriples) {
        
        Set<String> out = new HashSet<>();
                
        for (IncrementalQuery query : queries) { 
            
             Query q = query.assemble(vocab,onlyInstantiatedTriples);
             
             if (q != null) { 
                 out.add(query.prettyPrint(q));
             }
        }
                
        return out;
    }
    
    
    // Cloning 
    
    @Override
    public QueryBuilder clone() {
        
        QueryBuilder clone = new QueryBuilder();
        
        clone.placeholders = placeholders;
        
        for (IncrementalQuery iq : queries) {
             clone.queries.add(iq.clone());
        }
        
        return clone;
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
