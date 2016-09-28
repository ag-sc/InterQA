package interQA.main;

import interQA.cache.CachesCreator;

import static interQA.Config.Language.EN;
import static interQA.Config.Usecase.EXPERIMENT;

/**
 * Created by Mariano on 28/09/2016.
 */
public class queriesPredictor {
    static public void main(String[] args) {

        if (args.length != 2){
            System.out.println("Missing arguments. Requires: (1) the SPARQL EP, "+
                                                            "(2) output filename for ASK queries (with full path), " +
                                                            "(3) output filename for SELECT queries (with full path).");
            System.exit(1);
        }else {
            //CachesCreator cc = new CachesCreator(EXPERIMENT, EN);
            CachesCreator cc = new CachesCreator(EXPERIMENT, EN, args[0]); //"http://4v.dia.fi.upm.es:8890/sparql"); //We do not use the default ep for this case (dbpedia.org/sparql)
            cc.writePredictedASKqueriesToFile(args[1]);    //"EXPERIMENT.ASKqueries.v2.txt"
            cc.writePredictedSELECTqueriesToFile(args[2]); //"EXPERIMENT.SELECTqueries.v2.txt"
        }

    }
}
