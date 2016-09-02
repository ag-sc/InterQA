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
public class DatasetConnectorLabelTestDBPediaEN extends TestCase{
    
    DatasetConnector t = null;
    
    public void setUp() throws Exception {
        
        t = new DatasetConnector("http://dbpedia.org/sparql",Language.EN,Usecase.DBPEDIA);
    
    }
    
//     public void testLabel() throws Exception{
// 
//     String LabelOptionalOutput = t.label("P", "l").toString() ;
//     assertEquals("{ OPTIONAL\n" +
//                  "    { ?P  <http://www.w3.org/2000/01/rdf-schema#label>  ?l }\n" +
//                  "}",LabelOptionalOutput);
//     
//    }
    
}
