/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.lexicon;
import interQA.Config.Language;
import interQA.Config.Usecase;
import interQA.lexicon.DatasetConnector;
import junit.framework.TestCase;

/**
 *
 * @author mirtik
 */
public class DatasetConnectorLabelTestSpringerEN extends TestCase{
   
    DatasetConnector t = null;
    
    public void setUp() throws Exception {
        
        t = new DatasetConnector("http://es.dbpedia.org/sparql",Language.EN,Usecase.SPRINGER);
    
    }
    
    
    
    
 public void testLabel() throws Exception{
 
     String LabelOptionalOutput = t.label("P", "l").toString() ;
     assertEquals("{ OPTIONAL\n" +
"    {   { ?P  <http://lod.springer.com/data/ontology/property/confName>  ?l }\n" +
"      UNION\n" +
"        { ?P  <http://lod.springer.com/data/ontology/property/confAcronym>  ?l }}\n" +
"  OPTIONAL\n" +
"    { ?P  <http://lod.springer.com/data/ontology/property/confYear>  ?y }\n" +
"}",LabelOptionalOutput);
     
 }
     
    
     
     
 
 

 
}
