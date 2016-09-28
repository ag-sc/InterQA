package interQA.cache;

import interQA.Config;
import interQA.patterns.QueryPatternManager;

import java.io.*;

import static interQA.Config.ExtractionMode.ExhaustiveExtraction;
import static interQA.Config.Language.EN;
import static interQA.Config.Usecase.EXPERIMENT;

/**
 * Created by Mariano on 04/09/2016.
 */
public class CachesCreator {
    Config config = null;

    Config getConfig(){return config;}

    private CachesCreator(Config.Usecase usecase, Config.Language lang){
        new CachesCreator(usecase, lang, null);
    }
    public CachesCreator(Config.Usecase usecase, Config.Language lang, String ep){
        config = new Config();
        config.init(usecase, lang,
                    null, //All the patterns defined in the usecase
                    ep);
        config.setCacheMode(ExhaustiveExtraction,
                            true); //Uses (reads/writes) historical cache
    }
    public void writePredictedSELECTqueriesToFile(String fileName){
        QueryPatternManager qm = config.getPatternManager();
        String[] sels = qm.getPredictedSELECTqueries();
        writeQueriesToFile(sels, fileName);
    }

    public void writePredictedASKqueriesToFile(String fileName){
        String[] asks = null;
        try {
            QueryPatternManager qm = config.getPatternManager();
            asks = qm.getPredictedASKqueries();
        }catch (Throwable e){
            e.printStackTrace();
            throw (e); //I can not solve it
        }finally {
            //Save results before die
            writeQueriesToFile(asks, fileName);
            System.out.println("Error computing ASK queries. Data previous to the error has been saved.");
        }
        writeQueriesToFile(asks, fileName);
    }
    private void writeQueriesToFile(String[] queries, String fileName){
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            for (String q : queries) {
                fos.write((q + "\n").getBytes());
            }
        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
    public void writesCacheFilefromSELECTqueries(String queriesFileName, String ep){
        writesCacheFilefromSELECTqueries(queriesFileName, ep, null);
    }

    /**
     * For the DBpedia version 2015-
     * @param queriesFileName
     * @param ep
     * @param serFileNamePrefix
     */
    public void writesCacheFilefromSELECTqueries(String queriesFileName, String ep, String serFileNamePrefix){
        try {
            BufferedReader fileQueries = new BufferedReader(new FileReader(queriesFileName));

            int i = 0;
            String selQuery = null;
            JenaExecutorCacheSelect selExec = config.getDatasetConnector().getJenaExecutorCacheSelect();

            while ((selQuery = fileQueries.readLine())!=null) {
                System.out.println("Getting results for query #" + i++ + ": " + selQuery);
                try {
                    selExec.executeWithCache(ep, selQuery);
                }catch (Throwable e){
                    System.out.println("Error in Sel query: " + selQuery);
                    //Save in case of error (including out ot memory)
                    selExec.saveCacheToDisk(serFileNamePrefix!= null? serFileNamePrefix : "error", ep);
                    e.printStackTrace();
                }
//                if (selExec.lastExecutionCameFromCache() == false){ //Only saves the serialization after real extractions
//                    System.out.print("Saving cache to disk...");
//                    selExec.saveCacheToDisk(serFileNamePrefix!= null? serFileNamePrefix : "test", ep);
//                    System.out.println("...done!");
//                }
            }
            //Saves at the end, otherwise it becomes crazy slowly
            selExec.saveCacheToDisk(serFileNamePrefix!= null? serFileNamePrefix : "test", ep);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }


    }

    public void writesCacheFilefromASKqueries(String queriesFileName, String ep){
        try {
            BufferedReader fileQueries = new BufferedReader(new FileReader(queriesFileName));

            int i = 0;
            String selQuery = null;
            JenaExecutorCacheAsk askExec = config.getDatasetConnector().getJenaExecutorCacheAsk();
            while ((selQuery = fileQueries.readLine())!=null) {
                System.out.println("Getting results for query #" + i++ + ": " + selQuery);
                try {
                    askExec.executeWithCache(ep, selQuery);
                }catch (Exception e){
                    System.out.println("Error in Sel query: " + selQuery);
                    e.printStackTrace();
                }
                System.out.print("Saving cache to disk...");
                askExec.saveCacheToDisk("test", ep);
                System.out.println("...done!");
            }

        }catch (IOException ioe){
            ioe.printStackTrace();
        }

    }

    static public void main(String[] args) {
        //CachesCreator cc = new CachesCreator(EXPERIMENT, EN);
        CachesCreator cc = new CachesCreator(EXPERIMENT, EN,
                                             "http://4v.dia.fi.upm.es:8890/sparql"); //We do not use the default ep for this case (dbpedia.org/sparql)
        cc.writePredictedASKqueriesToFile("EXPERIMENT.ASKqueries.v2.txt");
        cc.writePredictedSELECTqueriesToFile("EXPERIMENT.SELECTqueries.v2.txt");

    }

    static public void main2(String[] args) {
        CachesCreator cc = new CachesCreator(EXPERIMENT, EN);

        JenaExecutorCacheSelect jeSel = cc.getConfig().getDatasetConnector().getJenaExecutorCacheSelect();
        //If you have a partial result, reload it here
        jeSel.readCacheFromDiskSpecificFile("part14v.dia.fi.upm.es.cacheSelect.ser");

        //Attention! it will read a ser file named "like the EP", if it does not exists, will create a new one
        //For this example the file shoud be 4v.dia.fi.upm.es.cacheSelect.ser
        //It will also read the urilabel cache file dbpedia.urilabels.cache.ser
        cc.writesCacheFilefromSELECTqueries("EXPERIMENT.SELECTqueries.txt",
                                            "http://4v.dia.fi.upm.es:8890/sparql",
                                            "part2");
    }
    static public void main3(String[] args) {
        System.out.print("Reading the serialized cache...");
        long start = System.currentTimeMillis();

        CachesCreator cc = new CachesCreator(EXPERIMENT, EN);
        JenaExecutorCacheSelect jeSel = cc.getConfig().getDatasetConnector().getJenaExecutorCacheSelect();
        jeSel.readCacheFromDiskSpecificFile("4v.dia.fi.upm.es.cacheSelect.ser");
        try {
            //Saves the file in case of Out of Memory
            cc.writePredictedASKqueriesToFile("EXPERIMENT.ASKqueries.txt");
        }catch (Throwable e){ //Catches also Out of memory error
            e.printStackTrace();
        }finally {
            //Saves the select cache in case of Out of Memory
            jeSel.saveCacheToDisk("error", "http://4v.dia.fi.upm.es:8890/sparql");
            System.out.println("done (" + (System.currentTimeMillis() - start)/1000 + ") seconds.");
        }
        //Saves the select cache if all right
        jeSel.saveCacheToDisk("error", "http://4v.dia.fi.upm.es:8890/sparql");

        System.out.println("done (" + (System.currentTimeMillis() - start)/1000 + ") seconds.");

    }

    static public void main4(String[] args) {
        CachesCreator cc = new CachesCreator(EXPERIMENT, EN);

        JenaExecutorCacheAsk jeAsk = cc.getConfig().getDatasetConnector().getJenaExecutorCacheAsk();
        //jeAsk.readCacheFromDiskSpecificFile("part1.4v.dia.fi.upm.es.cacheAsk.ser");
        //Attention! it will read a ser file named "like the EP", if it does not exists, will create a new one
        //For this example the file shoud be 4v.dia.fi.upm.es.cacheSelect.ser
//        cc.writesCacheFilefromASKqueries("EXPERIMENT.ASKqueries.txt",
//                                            "http://4v.dia.fi.upm.es:8890/sparql",
//                                            "part22");
    }
}
