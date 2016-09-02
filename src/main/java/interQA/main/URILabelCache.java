package interQA.main;

import org.apache.jena.query.*;

import java.io.*;
import java.util.*;

import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.resultset.ResultsFormat;


/**
 * Created by Mariano on 31/08/2016.
 */
public class URILabelCache implements Serializable{

    private TreeMap<String, String> cache = null;
    private String labelslang = null;

    /**
     * For a given language like "es", but also include label with labelslang ""
     * @param language
     */
    public URILabelCache(String language){
        cache = new TreeMap<>();
        labelslang = language;
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
        ArrayList<String> list = new ArrayList<>(uris.length); //Provide the initial size to speed up
        for (String uri: uris){
            list.add(this.getLabel(uri));
        }
        return(list);
    }

    /**
     * Given a number and a seed, returns that number of randomly selected URIs in the cache.
     * If the cache has NOT that amount of URIs, will return null.
     * @param n
     * @return
     */
    public String[] getRandomURIs(int n, int seed){
        if (cache.size() < n){
            //We do not have such a number of elements in the cache
            return null;
        }
        Random rand = new Random(seed);
        ArrayList<String> res = new ArrayList<String>(n);
        ArrayList<String> keysAsArray = new ArrayList<String>(cache.keySet());
        int idx = 0;
        String uri = null;
        for (int i = 0; i < n; i++) {
            idx = rand.nextInt(cache.size()); //ints between 0 (inclusive) and arg (exclusive)
            uri = keysAsArray.get(idx);
            res.add(uri);
        }
        String[] resok = new String[res.size()];

        return (res.toArray(resok));
    }

    /**
     * Reads the file assuming: (1) the first row contains the names of the variables (unused),
     * (2) each row has the form <URI>\t"label"\t"labelslang"
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
                }else{ //labelslang = ""
                   lg = "";
                   labelok = (dataArray[1]).substring(1, dataArray[1].length() - 1); //Removes initial and trailing quotes
                }

                if (lg.equals(labelslang) || lg.equals("")) {
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
     * (2) each row has the form URI\t"label"@labelslang\t"labelslang" (yes, with quotes).
     * This can be obtained with this SPARQL query:
     *  select distinct ?res ?label ?labelslang where { ?res <http://www.w3.org/2000/01/rdf-schema#label> ?label BIND(labelslang(?label) as ?labelslang)}
     * @param fileName
     */
    public void readXMLDataFile(String fileName){
        readSpecificDataFile(fileName, ResultsFormat.FMT_RS_XML);
    }

    /**
     * Here we do NOT use Jena. Reads the filing assuming that
     * each row has an specific regex:
     * It is the format of the downlodable gz2 DBpedia files
     * @param fileName
     */
    public void readTTLDataFile(String fileName){
        //readSpecificDataFile(fileName, ResultsFormat.FMT_RDF_TTL); //There is also a FMT_RDF_TURTLE
        //TTL is NOT a Resultset format (is NOT a RS format). RS formats are FMT_RS_XML, FMT_RS_CSV, FMT_RS_TSV....

    }

    /**
     * Force brute: JENA reads the ttl and creates a MODEL. Then, fills the cache.
     * Horrible in terms of memory. Provisional solution. Can be used for testing in future implementations (parsing)
     * @param fileName
     */
    public void readTTLDataFileForceBrute(String fileName) {
        Model model = ModelFactory.createDefaultModel();
        model.read(fileName);
        System.out.println("... model created with "+ model.size()+ " statements. Creating cache...");

        StmtIterator iter = model.listStatements();
        Statement subj = null;
        Resource  res = null;
        String uri = null;
        RDFNode node = null;
        Literal lit = null;
        String lang = null;
        String label = null;
        while (iter.hasNext()){
            subj = iter.next();
            res = subj.getSubject();
            node = subj.getObject();
            lit = node.asLiteral();
            label = (String)(lit.getValue());
            lang = lit.getLanguage(); //when no lang, this returns ""
            uri = res.getURI();
            if (uri != null && label!= null && (lang.equals(labelslang) ||lang.equals(""))) {
                cache.put(uri, label);
            }
        }
    }

    /**
     * Read a file in the specified format, creates a Jena ResultSet and fills the cache with the data
     * @param fileName
     */
    public void readSpecificDataFile(String fileName, ResultsFormat format ){

        FileInputStream fis = null;
        ResultSet res = null;

        //Creates a Jena ResultSet from the Turtle file
        try {
            fis = new FileInputStream(fileName);
            res = ResultSetFactory.load(fis, format); //There is also  ResultsFormat.FMT_RDF_TURTLE

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("...file read...");
        ResultSetRewindable resok = ResultSetFactory.copyResults(res);
        System.out.println("...stable Jena resultset created...storing in cache...");
        resok.reset();

        List<String> vars = resok.getResultVars();
        //We do not assume the names, but we assume that the variables are ordered as:
        // [0] --> resource (IRI)
        // [1] --> label (String)
        // [2] --> labelslang (String)
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
            if (l.equals(labelslang)) { //If the label labelslang in the file is the one we are interested in
                cache.put(uri,
                        label.substring(0, label.length() - (1 + l.length()))); //removes the @xx
                k++;
            } else{
                if (l.equals("")) { //In this case (no labelslang tag) we also are interested
                    cache.put(uri,
                            label);
                    p++;
                }
            }
        }
        System.out.print("...read " + n + " lines, with "+ k + " \""+ labelslang + "\" labels and " + p + " \"\" labelslang labels,...");
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

    static public void main0(String[] args) {
        URILabelCache ulc = new URILabelCache("en");

        //Time required to load the TTL file
        System.out.print("Loading the ttl file...");
        long start = System.currentTimeMillis();
        ulc.readTTLDataFile("urilabels.ttl");
        System.out.println("done (" + (System.currentTimeMillis() - start) + ") millis.");
    }

    static public void main1(String[] args){
        URILabelCache ulc = new URILabelCache("en");

        //Time required to load the TTL file
        System.out.print("Loading the ttl file...");
        long start = System.currentTimeMillis();
        ulc.readTTLDataFileForceBrute("urilabels.ttl");
        System.out.println("done (" + (System.currentTimeMillis() - start) + ") millis.");

        //Time required to serialize the cache
        System.out.print("Serializing cache with " + ulc.getSize() + " elements...");
        start = System.currentTimeMillis();
        ulc.serializeToFile("urilabels.cache.ser");
        System.out.println("done (" + (System.currentTimeMillis() - start) + ") millis.");

        //Time required to read the serialized cache
        System.out.print("Reading the serialized cache...");
        start = System.currentTimeMillis();
        ulc.readSerializationFile("urilabels.cache.ser");
        System.out.println("done (" + (System.currentTimeMillis() - start) + ") millis.");

    }

    static public void main2(String[] args){
        URILabelCache.fromXMLFileToTSVFile("urilabels.xml", "urilabels.tsv");
    }

    static public void main(String[] args){
        URILabelCache ulc = new URILabelCache("en"); //Reads labels with labelslang "en" or ""

        ulc.readSerializationFile("urilabels.cache.ser");

        String res = ulc.getLabel("http://dbpedia.org/resource/Madrid");
        System.out.println(res);

        String[] reslist = {"http://dbpedia.org/resource/Madrid",
                            "http://dbpedia.org/resource/Barcelona"
                           };
        ArrayList<String> list = ulc.getLabels(reslist);
        System.out.println(list);

    }

    static public void main4(String[] args){
        URILabelCache ulc = new URILabelCache("en");

        //Time required to load the TTL file
        System.out.print("Loading the ttl file...");
        long start = System.currentTimeMillis();
        ulc.readTTLDataFile("urilabels.ttl");
        System.out.println("done (" + (System.currentTimeMillis() - start) + ") millis.");

        //Time required to load the XML file
        System.out.print("Loading the xml file...");
        start = System.currentTimeMillis();
        ulc.readXMLDataFile("urilabels.xml");
        System.out.println("done (" + (System.currentTimeMillis() - start) + ") millis.");

        //Time required to load the TSV file
        System.out.print("Loading the tsv file...");
        start = System.currentTimeMillis();
        ulc.readTSVDataFile("urilabels.tsv");
        System.out.println("done (" + (System.currentTimeMillis() - start) + ") millis.");


    }
    static public void main5(String[] args) {
        URILabelCache ulc = new URILabelCache("es");
        long start = 0;

        //Time required to read the serialized cache
        System.out.print("Reading the serialized cache...");
        start = System.currentTimeMillis();
        ulc.readSerializationFile("urilabels.cache.ser");
        System.out.println("done (" + (System.currentTimeMillis() - start) + ") millis.");

        //Select many random elements from the cache
        int num = 500000;
        String[] uris = ulc.getRandomURIs(num,  //number of URIs
                                          666);   //random seed

        //Time required to retrieve the URIs
        System.out.print("Retrieving "+ num + "...");
        start = System.currentTimeMillis();
        ArrayList<String> labels = ulc.getLabels(uris);
        System.out.println("done (" + (System.currentTimeMillis() - start) + ") millis.");

    }
    static public void main6(String[] args) {
        URILabelCache ulc = new URILabelCache("en");
        long start = 0;

        //Read the serialized cache
        System.out.print("Reading the serialized cache...");
        start = System.currentTimeMillis();
        ulc.readSerializationFile("urilabels.cache.ser");
        System.out.println("done!. " + ulc.getSize() + " uris+labels loaded in " + (System.currentTimeMillis() - start) + " millis.");

        int ms = 5000;
        int sec = ms/1000;
        System.out.print("Now you have "+ sec + " seconds to check the memory used...");
        //To have time to to check the RAM
        try {
            Thread.sleep(ms);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        System.out.print("...done?");
    }
    static public void main7(String[] args) {
        URILabelCache ulc = new URILabelCache("en");
        ulc.readTTLDataFileForceBrute("urilabels.ttl");
    }

}
