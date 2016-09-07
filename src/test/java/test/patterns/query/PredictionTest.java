package test.patterns.query;

import interQA.Config;
import interQA.patterns.templates.*;

/**
 *
 * @author cunger
 */
public class PredictionTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Config config = new Config();
        config.init(Config.Usecase.EXPERIMENT,
                    Config.Language.EN,
                    null); //All the patterns defined in the usecase
               
        // C
        
        QueryPattern c = new C(config.getLexicon(),config.getDatasetConnector());
        
        System.out.println("\n---------- C ----------\n");
        for (String q : c.predictASKqueries()) { 
             System.out.println(" " + q);
        }
        
        // C_P
        
        QueryPattern cp = new C_P(config.getLexicon(),config.getDatasetConnector());
        
        System.out.println("\n---------- C_P ----------\n");
        for (String q : cp.predictASKqueries()) { 
             System.out.println(" " + q);
        }
        
        // C_P_P
        
        QueryPattern cpp = new C_P_P(config.getLexicon(),config.getDatasetConnector());
        
        System.out.println("\n---------- C_P_P ----------\n");
        for (String q : cpp.predictASKqueries()) { 
             System.out.println(" " + q);
        }
        
        // P_P_C
        
        QueryPattern ppc = new P_P_C(config.getLexicon(),config.getDatasetConnector());
        
        System.out.println("\n---------- P_P_C ----------\n");
        for (String q : ppc.predictASKqueries()) { 
             System.out.println(" " + q);
        }
        
        // C_P_I
        
        QueryPattern cpi = new C_P_I(config.getLexicon(),config.getDatasetConnector());
        
        System.out.println("\n---------- C_P_I ----------\n");
        for (String q : cpi.predictASKqueries()) { 
             System.out.println(" " + q);
        }
        System.out.println("");
        for (String q : cpi.predictSELECTqueries()) { 
             System.out.println(" " + q);
        }
        
        // P_I
        
        QueryPattern pi = new P_I(config.getLexicon(),config.getDatasetConnector());
        
        System.out.println("\n---------- P_I ----------\n");
        for (String q : pi.predictASKqueries()) { 
             System.out.println(" " + q);
        }
        System.out.println("");
        for (String q : pi.predictSELECTqueries()) { 
             System.out.println(" " + q);
        }
        
        // P_P_I
        
        QueryPattern ppi = new P_P_I(config.getLexicon(),config.getDatasetConnector());
        
        System.out.println("\n---------- P_P_I ----------\n");
        for (String q : ppi.predictASKqueries()) { 
             System.out.println(" " + q);
        }
        System.out.println("");
        for (String q : ppi.predictSELECTqueries()) { 
             System.out.println(" " + q);
        }
        
        // C_P_P_I
        
        QueryPattern cppi = new C_P_P_I(config.getLexicon(),config.getDatasetConnector());
        
        System.out.println("\n---------- C_P_P_I ----------\n");
        for (String q : cppi.predictASKqueries()) { 
             System.out.println(" " + q);
        }
        System.out.println("");
        for (String q : cppi.predictSELECTqueries()) { 
             System.out.println(" " + q);
        }
        
        // C_I_P
        
        QueryPattern cip = new C_I_P(config.getLexicon(),config.getDatasetConnector());
        
        System.out.println("\n---------- C_I_P ----------\n");
        for (String q : cip.predictASKqueries()) { 
             System.out.println(" " + q);
        }
        System.out.println("");
        for (String q : cip.predictSELECTqueries()) { 
             System.out.println(" " + q);
        }
        
        // C_P_I_P_I
        
        QueryPattern cpipi = new C_P_I_P_I(config.getLexicon(),config.getDatasetConnector());
        
        System.out.println("\n---------- C_P_I_P_I ----------\n");
        for (String q : cpipi.predictASKqueries()) { 
             System.out.println(" " + q);
        }
        System.out.println("");
        for (String q : cpipi.predictSELECTqueries()) { 
             System.out.println(" " + q);
        }
        
    }
    
}
