package interQA.patterns.query;

import java.util.HashSet;
import java.util.Set;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.aggregate.AggCountVar;
import org.apache.jena.sparql.syntax.ElementGroup;

/**
 *
 * @author cunger
 */
public class IncrementalQuery {
    
    
    Query query;
    
    Set<String> projvars;
    Set<String> countvars;
    
    Set<Triple> triples;
    
    
    public IncrementalQuery() {
                
        projvars  = new HashSet<>();
        countvars = new HashSet<>();
        triples   = new HashSet<>();
    }
    
    
    public void addTriple(Triple t) {
        
        triples.add(t);
    }
    
    public void removeTriple(Triple t) {
        
        triples.remove(t);
    }

    public Query assembleAsAsk(boolean asFinal) {
    
        Query q  = assemble(asFinal);
        if  ( q != null) q.setQueryAskType();
        
        return q;
    }
    
    public Query assemble() {
        
        return assemble(false);
    }
    
    public Query assemble(boolean asFinal) {
        
        query = QueryFactory.make();
        
        // Query body (from triples)
        
        ElementGroup body = new ElementGroup();
        
        for (Triple t : triples) {
            
            if (!asFinal ||
                !(  t.getSubject().isVariable()
                 && t.getPredicate().isVariable()
                 && t.getObject().isVariable())) {
            
                body.addTriplePattern(t);
             }
        }
                
        query.setQueryPattern(body);
        
        // Projection variables 
        
        for (String v : projvars) {
            query.getProject().add(Var.alloc(v));
        }
        
        for (String v : countvars) {
            query.getProject().add(Var.alloc(v+"_count"),query.allocAggregate(new AggCountVar(new ExprVar(Var.alloc(v)))));
        }
        
        // Query type
        
        if (query.getProjectVars().isEmpty()) {
            query.setQueryAskType();
        } 
        else {
            query.setQuerySelectType();
            query.setDistinct(true);
        }
        
        return query;
    }
    
    
    @Override
    public IncrementalQuery clone() {
        
        IncrementalQuery copy = new IncrementalQuery();
        
        for (String v : projvars) {
             copy.projvars.add(v);
        }
        for (String v : countvars) {
             copy.countvars.add(v);
        }
        
        for (Triple t : triples) {
             copy.addTriple(new Triple(t.getSubject(),t.getPredicate(),t.getObject()));
        }
        
        return copy;
    }
    
}
