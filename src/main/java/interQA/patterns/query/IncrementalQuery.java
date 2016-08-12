package interQA.patterns.query;

import interQA.lexicon.Vocabulary;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    public Query assembleAsAsk(Vocabulary vocab, boolean onlyInstantiatedTriples) {
    
        Query q = assemble(vocab,onlyInstantiatedTriples);
        
        if (q != null) q.setQueryAskType();
        
        return q;
    }
    
    public Query assemble(Vocabulary vocab, boolean onlyInstantiatedTriples) {
        
        query = QueryFactory.make();
        
        // Add all triples to query body
        
        ElementGroup newbody = new ElementGroup();
        Set<String> occuringVars = new HashSet<>();
        
        for (Triple t : triples) {
            
            // skip t if it consists of only variables (modulo rdf:type)
            if ( onlyInstantiatedTriples
             &&  t.getSubject().isVariable()
             &&  t.getObject().isVariable()
             && (t.getPredicate().isVariable() || 
                  (t.getPredicate().isURI()) &&
                   t.getPredicate().getURI().equals(vocab.sortal_predicate)) ) {
                continue;
            }
            
            newbody.addTriplePattern(t);
                
            if (t.getSubject().isVariable())   occuringVars.add(t.getSubject().getName());
            if (t.getPredicate().isVariable()) occuringVars.add(t.getPredicate().getName());
            if (t.getObject().isVariable())    occuringVars.add(t.getObject().getName());
        }
        if (!body.isEmpty()) {
            newbody.getElements().addAll(body.getElements());
        }
        
        if (newbody.isEmpty()) return null;
        
        query.setQueryPattern(newbody);
        
        // Add projection variables 
        
        for (String v : projvars) {
            if (occuringVars.contains(v)) {
                query.getProject().add(Var.alloc(v));
            }
        }
        
        for (String v : countvars) {
            if (occuringVars.contains(v)) {
                query.getProject().add(Var.alloc(v+"_count"),query.allocAggregate(new AggCountVar(new ExprVar(Var.alloc(v)))));
            }
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
        
        String s = q.toString().replaceAll("\\n"," ").replaceAll("\\s+"," ").trim();
        
        // remove "-01-01" from xsd:gYear
        Map<String,String> replacements = new HashMap<>();
        Pattern p = Pattern.compile(".*(\"\\d{4})(-01-01)((\\+\\d{2}:\\d{2})?\"^^<http:\\/\\/www.w3.org\\/2001\\/XMLSchema#gYear>).*");
        Matcher m = p.matcher(s);
        while  (m.find()) {
            replacements.put(m.group(),m.group(1)+m.group(3));
        }
        for (String k : replacements.keySet()) {
             s = s.replaceAll(k,replacements.get(k));
        }
                
        return s;
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
