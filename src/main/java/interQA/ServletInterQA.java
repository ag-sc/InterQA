
package interQA;

import interQA.cache.JenaExecutorCacheSelect;
import interQA.patterns.QueryPatternManager;
import com.google.gson.Gson;
import interQA.lexicon.DatasetConnector;
import interQA.lexicon.Lexicon;
import interQA.Config.Language;
import interQA.Config.Usecase;
import interQA.patterns.QueryPatternFactory_EN;
import java.io.IOException;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;

import static interQA.Config.ExtractionMode.ExhaustiveExtraction;

public class ServletInterQA extends HttpServlet {

    QueryPatternManager qm = null;
    Gson gson = new Gson();

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {
        //Warning! set response header/status BEFORE sending any content to the client
        response.setContentType("application/json"); //We always return JSON
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Access-Control-Allow-Origin", "*"); // "*" May be too much. Anyone could use this as a service.
        PrintWriter out = response.getWriter();
        log("The URL is " + request.getQueryString());
        log("The page encoding is "+ request.getCharacterEncoding());
        String command = request.getParameter("command"); ///ServletInterQA?command=whatever

        switch (command){
            //Warning! set response header/status BEFORE sending any content to the client
            case "getOptions":  //command=getOptions  server returns a list of option
                response.setStatus(response.SC_OK); //Code 200
                List<String> options = qm.getUIoptions();
                log("getOptions command with options: " + options);
                if (options != null && options.isEmpty()){
                    log("No options. The client will receive an array[0], which has length = 0.");
                }
                out.write(gson.toJson(options));
                break;
            case "selected":    //command=selected&selection=text   server is informed. Nothing in returned
                response.setStatus(response.SC_OK);
                String encoded = request.getParameter("selection"); //The value was sent as URLable
                //log("decoded as ISO-8859-1): " + URLDecoder.decode(encoded, "ISO-8859-1"));
                String text = new String(encoded.getBytes("iso-8859-1"), "UTF-8"); //Valid only for Tomcat default conf?
                List<String> availableQPNames = qm.getActivePatternsBasedOnUserInput(text);
                log("selected command with text: " + text + " and with availableQPNames: " + availableQPNames);
                out.write(gson.toJson(availableQPNames));
                break;
            case "getQueries":    //command=getQueries server returns a list of SPARQL queries
                response.setStatus(response.SC_OK);
                List<String> queries = qm.buildSPARQLqueries();
                log("getQueries command with queries: " + queries);
                out.write(gson.toJson(queries));
                break;
            default:           // unsupported command
                response.setStatus(response.SC_BAD_REQUEST); //code 400
                log("unsupported command. Error! ");
                break;
        }
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response)
            throws ServletException, IOException {
        //Same behavior than doGet. This is better than overriding method service()
        doGet(request, response);
    }

    public void init(ServletConfig servconfig) throws ServletException {

        super.init(servconfig);
        
        log("Loading started at " + LocalDateTime.now()); //LocalDateTime requires Java 8

        Config config = new Config();
        config.init(Usecase.EXPERIMENT, Language.EN, null); //This load the cache urilabels dbpedia.urilabels.cache.ser (without org!!)
        config.setCacheMode(ExhaustiveExtraction, //Exahustive extraction
                            true);                //Uses the historical cache
        JenaExecutorCacheSelect jeSel = config.getDatasetConnector().getJenaExecutorCacheSelect();
        jeSel.readCacheFromDiskSpecificFile("dbpedia.org.cacheSelect.ser");

        qm = config.getPatternManager();
        qm.getActivePatternsBasedOnUserInput(""); //This initializes the active patterns

        log("Loading finished at " + LocalDateTime.now());  //LocalDateTime requires Java 8
    }
}
