package interQA.main;

import org.apache.jena.ext.com.google.common.collect.ArrayListMultimap;
import org.apache.jena.query.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.jena.sparql.resultset.ResultsFormat;


/**
 * Created by Mariano on 31/08/2016.
 */
public class URILabelCache implements Serializable{

    private TreeMap<String, String> cache = null;
    private String lang = null;

    /**
     * For a given language like "es", but also include label with lang ""
     * @param language
     */
    public URILabelCache(String language){
        cache = new TreeMap<>();
        lang = language;
    }
    public int getSize(){
        return cache.size();
    }

    /**
     * Looks for the specified uri in the cache. If it is not there, returns null
     * @param uri
     * @return
     */
    public String getLabel (String uri){

        return(cache.containsKey(uri)? cache.get(uri) : null);

    }

    public ArrayList<String> getLabels (String[] uris){
        ArrayList<String> list = new ArrayList<>();
        for (String uri: uris){
            list.add(this.getLabel(uri));
        }
        return(list);
    }

    /**
     * Reads the file assuming: (1) the first row contains the names of the variables (unused),
     * (2) each row has the form <URI>\t"label"\t"lang"
     * @param fileName
     */
    public void readTSVDataFile(String fileName){

        BufferedReader TSVFile = null;

        try {
            TSVFile = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String dataRow = null;
        String[] dataArray = null;
        String URI = null;
        String label   = null;
        String labelok = null;
        String lg      = null;
        int n = 0;
        //Pattern p = Pattern.compile("\"([^\"]*)\"\t\"([^\"]*)\"");//Matches ^"anything"\t"anything"$
        try {
            dataRow = TSVFile.readLine(); //With the first row I do nothing
            dataRow = TSVFile.readLine();
            while (dataRow != null){
                n++;
                dataArray = dataRow.split("\t"); //Theory says that there is a non regex implementation for length 1 param
                URI = (dataArray[0]).substring(1, dataArray[0].length() - 1); //Removes initial "<" and trailing ">"
                if (dataArray[2].equals("\"\"") == false){ //Something like "en"
                   lg = (dataArray[2]).substring(1, dataArray[2].length() - 1); //Removes initial and trailing quotes
                   label = (dataArray[1]).substring(0, dataArray[1].length() - (1 + lg.length())); //Remove @xx
                   labelok = label.substring(1, label.length() - 1); //Removes initial and trailing quotes
                }else{ //lang = ""
                   lg = "";
                   labelok = (dataArray[1]).substring(1, dataArray[1].length() - 1); //Removes initial and trailing quotes
                }

                if (lg.equals(lang) || lg.equals("")) {
                    cache.put(URI, labelok);
                }
                dataRow = TSVFile.readLine();
            }
            TSVFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print("Read " + n + " lines.");
    }

    /**
     * Reads the file assuming it is a valid XML file (readable by Jena as a XML ResultSet)
     * Assumes: (1) the first row contains the names of the variables (unused),
     * (2) each row has the form URI\t"label"@lang\t"lang" (yes, with quotes).
     * This can be obtained with this SPARQL query:
     *  select distinct ?res ?label ?lang where { ?res <http://www.w3.org/2000/01/rdf-schema#label> ?label BIND(lang(?label) as ?lang)}
     * @param fileName
     */
    public void readXMLDataFile(String fileName){

        FileInputStream fis = null;
        ResultSet res = null;

        //Creates a Jena ResultSet from the XML file
        try {
            fis = new FileInputStream(fileName);
            res = ResultSetFactory.fromXML(fis);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ResultSetRewindable resok = ResultSetFactory.copyResults(res);
        resok.reset();

        List<String> vars = resok.getResultVars();
        //We do not assume the names, but we assume that the variables are ordered as:
        // [0] --> resource (IRI)
        // [1] --> label (String)
        // [2] --> lang (String)
        String varuri   = vars.get(0);
        String varlabel = vars.get(1);
        String varlang  = vars.get(2);
        String uri   = null;
        String label = null;
        String l     = null;
        QuerySolution qs = null;
        int n = 0;
        int k = 0;
        int p = 0;
        while (resok.hasNext()){
            n++;
            qs = resok.next();
            uri =   qs.getResource(varuri).toString();
            label = qs.getLiteral(varlabel).toString();
            l = qs.getLiteral(varlang).toString();
            if (l.equals(lang)) { //If the label lang in the file is the one we are interested in
                cache.put(uri,
                         label.substring(0, label.length() - (1 + l.length()))); //removes the @xx
                k++;
            } else{
                if (l.equals("")) { //In this case (no lang tag) we also are interested
                    cache.put(uri,
                              label);
                    p++;
                }
            }
        }
        System.out.print("...read " + n + " lines, with "+ k + " \""+ lang + "\" labels and " + p + " \"\" lang labels,...");
    }


    public static void fromXMLFileToTSVFile(String xmlFileName, String tsvFileName) {

        FileInputStream fis = null;
        ResultSet res = null;

        //Creates a Jena ResultSet from the XML file
        try {
            fis = new FileInputStream(xmlFileName);
            res = ResultSetFactory.fromXML(fis);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Saves the ResultSet as TSV
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(tsvFileName);
            ResultSetFormatter.output(fos, res, ResultsFormat.FMT_RS_TSV);
        }catch (FileNotFoundException fnf) {
            fnf.printStackTrace();
        }
    }

    public void serializeToFile(String fileName){
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(fileName);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(cache);
            oos.close();
        }catch (FileNotFoundException fnf){ //Can not open the file
            fnf.printStackTrace();
        }catch (IOException ioe){ //creating, writingTo or closing the oos
            ioe.printStackTrace();
        }
    }

    /**
     * Reads the serialized file. Removes the previous (if any) data and places the file data
     * @param fileName
     */
    public void readSerializationFile(String fileName){
        FileInputStream fis = null;
        ObjectInputStream ois = null;

        try {
            fis = new FileInputStream(fileName);
            ois = new ObjectInputStream(fis);
            cache = (TreeMap<String, String>) ois.readObject();
        } catch (FileNotFoundException fnfe) {
         fnfe.printStackTrace();
        } catch (IOException ioe){
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe){
            cnfe.printStackTrace();
        } finally {
            try {
                ois.close();
                fis.close();
            }catch (IOException ioe){
                System.out.println("Sorry, I can not close the cache file. Stack trace:");
                ioe.printStackTrace();
            }

        }
    }

    static public void main3(String[] args){
        URILabelCache ulc = new URILabelCache("en");

        //Time required to load the TSV file
        System.out.print("Loading the tsv file...");
        long start = System.currentTimeMillis();
        ulc.readTSVDataFile("URILabel.DBpedia.2011604.tsv");
        System.out.println("done (" + (System.currentTimeMillis() - start) + ") millis.");

        //Time required to serialize the cache
        System.out.print("Serializing cache with " + ulc.getSize() + " elements...");
        start = System.currentTimeMillis();
        ulc.serializeToFile("URILabel.DBpedia.2011604.tsv.ser");
        System.out.println("done (" + (System.currentTimeMillis() - start) + ") millis.");

        //Time required to read the serialized cache
        System.out.print("Reading the serialized cache...");
        start = System.currentTimeMillis();
        ulc.readSerializationFile("URILabel.DBpedia.2011604.tsv.ser");
        System.out.println("done (" + (System.currentTimeMillis() - start) + ") millis.");

        //Time required to read the XML file
        System.out.print("Reading the XML file...");
        start = System.currentTimeMillis();
        ulc.readXMLDataFile("URILabel.DBpedia.2011604.xml");
        System.out.println("done (" + (System.currentTimeMillis() - start) + ") millis.");
    }

    static public void main1(String[] args){
        URILabelCache.fromXMLFileToTSVFile("URILabel.DBpedia.2011604.xml", "URILabel.DBpedia.2011604.tsv");
    }

    static public void main2(String[] args){
        URILabelCache ulc = new URILabelCache("en"); //Reads labels with lang "en" or ""

        ulc.readXMLDataFile("URILabel.DBpedia.2011604.xml");

        String res = ulc.getLabel("http://dbpedia.org/resource/Madrid");
        System.out.println(res);

        String[] reslist = {"http://dbpedia.org/resource/Madrid",
                            "http://dbpedia.org/resource/Barcelona"
                           };
        ArrayList<String> list = ulc.getLabels(reslist);
        System.out.println(list);

    }

    static public void main(String[] args){
        URILabelCache ulc = new URILabelCache("en");

        //Time required to load the XML file
        System.out.print("Loading the xml file...");
        long start = System.currentTimeMillis();
        ulc.readXMLDataFile("URILabel.DBpedia.2011604.xml");
        System.out.println("done (" + (System.currentTimeMillis() - start) + ") millis.");

        //Time required to load the TSV file
        System.out.print("Loading the tsv file...");
        start = System.currentTimeMillis();
        //ulc.readTSVDataFile("URILabel.DBpedia.2011604.xml", "es");
        System.out.println("done (" + (System.currentTimeMillis() - start) + ") millis.");


    }

}
