package interQA.main;

import interQA.Config;
import interQA.cache.JenaExecutorCacheAsk;
import interQA.cache.JenaExecutorCacheSelect;
import interQA.lexicon.DatasetConnector;
import interQA.patterns.QueryPatternManager;
import interQA.patterns.templates.QueryPattern;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static interQA.Config.ExtractionMode.ExhaustiveExtraction;
import static interQA.Config.Language.EN;
import static interQA.Config.Usecase.EXPERIMENT;
import static interQA.Config.Usecase.SPRINGER;

/**
 * Created by Mariano on 04/09/2016.
 */
public class CachesCreator {
    Config config = null;

    Config getConfig(){return config;}

    private CachesCreator(Config.Usecase usecase, Config.Language lang){
        config = new Config();
        config.init(usecase, lang, null); //All the patterns defined in the usecase
        config.setCacheMode(ExhaustiveExtraction,
                            true); //Uses (reads/writes) historical cache
    }
    public void writePredictedSELECTqueriesToFile(String fileName){
        QueryPatternManager qm = config.getPatternManager();
        String[] sels = qm.getPredictedSELECTqueries();
        writeQueriesToFile(sels, fileName);
    }

    public void writePredictedASKqueriesToFile(String fileName){
        QueryPatternManager qm = config.getPatternManager();
        String[] asks = qm.getPredictedASKqueries();
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
                }catch (Exception e){
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
                    System.out.println("Errror in Sel query: " + selQuery);
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

    static public void main1(String[] args) {
        CachesCreator cc = new CachesCreator(EXPERIMENT, EN);
        cc.writePredictedSELECTqueriesToFile("EXPERIMENT.SELECTqueries.txt");
    }

    static public void main2(String[] args) {
        CachesCreator cc = new CachesCreator(EXPERIMENT, EN);

        JenaExecutorCacheSelect jeSel = cc.getConfig().getDatasetConnector().getJenaExecutorCacheSelect();
        jeSel.readCacheFromDiskSpecificFile("part1.4v.dia.fi.upm.es.cacheSelect.ser");
        //Attention! it will read a ser file named "like the EP", if it does not exists, will create a new one
        //For this example the file shoud be 4v.dia.fi.upm.es.cacheSelect.ser
        cc.writesCacheFilefromSELECTqueries("EXPERIMENT.SELECTqueries.txt",
                                            "http://4v.dia.fi.upm.es:8890/sparql",
                                            "part22");
    }
    static public void main(String[] args) {
        CachesCreator cc = new CachesCreator(EXPERIMENT, EN);
        cc.writePredictedASKqueriesToFile("EXPERIMENT.ASKqueries.txt");
    }

    static public void main4(String[] args) {
        CachesCreator cc = new CachesCreator(EXPERIMENT, EN);

        JenaExecutorCacheSelect jeSel = cc.getConfig().getDatasetConnector().getJenaExecutorCacheSelect();
        jeSel.readCacheFromDiskSpecificFile("part1.4v.dia.fi.upm.es.cacheSelect.ser");
        //Attention! it will read a ser file named "like the EP", if it does not exists, will create a new one
        //For this example the file shoud be 4v.dia.fi.upm.es.cacheSelect.ser
        cc.writesCacheFilefromSELECTqueries("EXPERIMENT.SELECTqueries.txt",
                                            "http://4v.dia.fi.upm.es:8890/sparql",
                                            "part22");
    }
}
