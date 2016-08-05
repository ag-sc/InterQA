package interQA.patterns.query;

import java.util.ArrayList;
import java.util.List;
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
    
    List<String> projvars;
    List<String> countvars;
    
    List<Triple> triples;
    ElementGroup body;
    
    
    public IncrementalQuery() {
                
        projvars  = new ArrayList<>();
        countvars = new ArrayList<>();
        triples   = new ArrayList<>();
        body      = new ElementGroup();
    }
    
    
    public ElementGroup getBody() {
        
        return body;
    }
    
    public List<Triple> getTriples() {
        
        return triples;
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
        
        // Add all triples to query body
        
        ElementGroup newbody = new ElementGroup();
                
        for (Triple t : triples) {
                        
            if (!asFinal ||
                !(   t.getSubject().isVariable()
                 && (t.getPredicate().isVariable() || t.getPredicate().toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"))
                 &&  t.getObject().isVariable())) {
            
                newbody.addTriplePattern(t);
             }
        }
        
        if (!body.isEmpty()) newbody.getElements().addAll(body.getElements());
        
        if (newbody.isEmpty()) return null;
        
        query.setQueryPattern(newbody);
        
        // Add projection variables 
        
        for (String v : projvars) {
            query.getProject().add(Var.alloc(v));
        }
        
        for (String v : countvars) {
            query.getProject().add(Var.alloc(v+"_count"),query.allocAggregate(new AggCountVar(new ExprVar(Var.alloc(v)))));
        }
        
        // Set query type
        
        if (query.getProjectVars().isEmpty()) {
            query.setQueryAskType();
        } 
        else {
            query.setQuerySelectType();
            query.setDistinct(true);
        }
        
        return query;
    }
    
    public String prettyPrint(Query q) {
        
        return q.toString().replaceAll("\\n"," ").replaceAll("\\s+"," ").trim();
    }
    
    
    @Override
    public IncrementalQuery clone() {
        
        // TODO Note that this ignores the body. 
        // Currently this is fine because it is only used for adding the label stuff.
        
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
