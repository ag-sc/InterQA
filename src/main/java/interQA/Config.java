package interQA;

import interQA.lexicon.DatasetConnector;
import interQA.lexicon.Lexicon;
import interQA.patterns.QueryPatternFactory;
import interQA.patterns.QueryPatternFactory_DE;
import interQA.patterns.QueryPatternFactory_EN;
import interQA.patterns.QueryPatternFactory_ES;
import interQA.patterns.QueryPatternManager;
import java.util.ArrayList;

import static interQA.Config.ExtractionMode.NaiveExtraction;


/**
 *
 * @author cunger
 */
public class Config {

    
    public enum Usecase  { SPRINGER, DBPEDIA, EXPERIMENT }
    public enum Language { EN, DE, ES }
    public enum ExtractionMode { NaiveExtraction, ExhaustiveExtraction }
    
    Lexicon lexicon;
    QueryPatternFactory patternFactory;
    QueryPatternManager patternManager;
    DatasetConnector dataset;
    ExtractionMode extractionMode = NaiveExtraction;
    boolean useHistoricalCache = false;
    
    
    public void init(Usecase usecase, Language language) {
        
        init(usecase,language,null);
    }

    public void init(Usecase usecase, Language language, ArrayList<String> patternNames) {
        init(usecase, language, patternNames, null);
    }

    /**
     * patternNames can be null. In this case all the patterns registered are used
     * ep can be null.  In this case are used default ep's for each usecase
     * @param usecase
     * @param language
     * @param patternNames
     * @param ep The endpoint
     */
    public void init(Usecase usecase, Language language, ArrayList<String> patternNames, String ep) {

        lexicon = new Lexicon(language);
        patternManager = new QueryPatternManager();
        
        switch (usecase) {

            case SPRINGER: {

                // Load lexicon

                switch (language) {
                    case EN: lexicon.load("./src/main/java/resources/springer_en.ttl"); break;
                    case DE: lexicon.load("./src/main/java/resources/springer_de.ttl"); break;
                    case ES: lexicon.load("./src/main/java/resources/springer_es.ttl"); break;
                }
                lexicon.extractEntries();
                 
                // Set endpoint
                
                dataset = new DatasetConnector(ep == null? "http://es.dbpedia.org/sparql": ep,
                                               language,usecase);

                break;
            }

            case DBPEDIA: {

                // Load lexicon

                switch (language) {
                    case EN: lexicon.load("./src/main/java/resources/dbpedia_en.rdf"); break;
                    case DE: lexicon.load("./src/main/java/resources/dbpedia_de.rdf"); break;
                }
                lexicon.extractEntries();
                                
                // Set endpoint
                
                dataset = new DatasetConnector(ep == null? "http://dbpedia.org/sparql" : ep,
                                               language,usecase);

                break;
            }
            
            case EXPERIMENT: {

                // Load lexicon

                lexicon.load("./src/main/java/resources/dbpedia_movies_en.ttl");
                lexicon.load("./src/main/java/resources/dbpedia_geography_en.ttl");
                lexicon.extractEntries();
                    
                // Set endpoint
                
                //dataset = new DatasetConnector("http://4v.dia.fi.upm.es:8890/sparql",language,usecase);
                dataset = new DatasetConnector(ep == null? "http://dbpedia.org/sparql" : ep,
                                               language,usecase);

                break;
            }
        }

        
        // Load query patterns
        
        switch (language) {
            
            case EN: {
                        patternFactory = new QueryPatternFactory_EN(usecase,lexicon,dataset);
                        break;
            }
            case DE: {
                        patternFactory = new QueryPatternFactory_DE(usecase,lexicon,dataset);
                        break;
            }
            case ES: {
                        patternFactory = new QueryPatternFactory_ES(usecase,lexicon,dataset);
                        break;
            }
        }
        patternManager.addQueryPatterns(patternNames == null ? patternFactory.rollout() : patternFactory.rollout(patternNames)); 
    }
    
    public Lexicon getLexicon() {
        return lexicon;
    }
    
    public DatasetConnector getDatasetConnector() {
        return dataset;
    }
    
    public QueryPatternManager getPatternManager() {
        return patternManager;
    }

    /**
     * Use after init
     * @param extractionMode
     */
    public void setCacheMode(ExtractionMode extractionMode, boolean useHistoricalCache){
        this.extractionMode = extractionMode;
        this.useHistoricalCache = useHistoricalCache;
        dataset.setCacheMode(extractionMode, useHistoricalCache);
    }

    public boolean isUsingHistoricalCache() {
        return useHistoricalCache;
    }
    public ExtractionMode getExtractionMode() {
        return extractionMode;
    }


}
