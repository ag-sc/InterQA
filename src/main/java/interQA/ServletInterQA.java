
package interQA;

import interQA.patterns.QueryPatternManager;
import interQA.patterns.Give_me_all_C_that_P_I_P_I;
import interQA.patterns.Give_me_all_C_that_P_I;
import interQA.patterns.Give_me_all_C_that_are_P_I;
import interQA.patterns.Which_C_P_I_P_I;
import com.google.gson.Gson;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.Lexicon;
import java.io.IOException;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;

public class ServletInterQA extends HttpServlet {

    QueryPatternManager qm = null;
    Gson gson = new Gson();

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {
        //Warning! set response header/statuts BEFORE sending any content to the client
        response.setContentType("application/json"); //We always return JSON
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Access-Control-Allow-Origin", "*"); // "*" May be too much. Anyone could use this as a service.
        PrintWriter out = response.getWriter();
        //log("The URL is " + request.getQueryString());
        //log("The page encoding is "+ request.getCharacterEncoding());
        String command = request.getParameter("command"); ///ServletInterQA?command=whatever
        List<String> options = qm.getUIoptions();
        if (options == null){
            out.write("No options");
            return;
        }
        switch (command){
            //Warning! set response header/status BEFORE sending any content to the client
            case "getOptions":  //command=getOptions  server returns a list of option
                response.setStatus(response.SC_OK); //Code 200
                //log("getOptions command with options: " + options);
                out.write(gson.toJson(options));
                break;
            case "selected":    //command=selected&selection=text   server is informed. Nothing in returned
                response.setStatus(response.SC_OK);
                String encoded = request.getParameter("selection"); //The value was sent as URLable
                //log("decoded as ISO-8859-1): " + URLDecoder.decode(encoded, "ISO-8859-1"));
                String text = new String(encoded.getBytes("iso-8859-1"), "UTF-8"); //Valid only for Tomcat default conf?
                List<String> availableQPNames = qm.userSentence(text);
                //log("selected command with text: " + text + " and with availableQPNames: " + availableQPNames);
                out.write(gson.toJson(availableQPNames));
                break;
            case "getQueries":    //command=getQueries server returns a list of SPARQL queries
                response.setStatus(response.SC_OK);
                List<String> queries = qm.buildSPARQLqueries();
                //log("getQueries command with queries: " + queries);
                out.write(gson.toJson(queries));
                break;
            default:           // unsupported command
                response.setStatus(response.SC_BAD_REQUEST); //code 400
                //log("unsupported command. Error! ");
                break;
        }
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response)
            throws ServletException, IOException {
        //Same behavior than doGet. This is better than overriding method service()
        doGet(request, response);
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log("Query pattern load started at " + LocalDateTime.now()); //LocalDateTime requires Java 8

        // Load lexicon
//        Lexicon lexicon = new Lexicon("en");
//        lexicon.load("resources/dbpedia_en.rdf");
//        InstanceSource instances = new InstanceSource("http://dbpedia.org/sparql","en");

        // Load query patterns

        qm = new QueryPatternManager();
//        qm.addQueryPattern(new QueryPattern1_1(lexicon,instances));
//        qm.addQueryPattern(new QueryPattern9_1(lexicon,instances));
//        qm.addQueryPattern(new QueryPattern9_2(lexicon,instances));


        List<String> LabelProps = new ArrayList<>();
        LabelProps.add("http://lod.springer.com/data/ontology/property/confName");
        LabelProps.add("http://lod.springer.com/data/ontology/property/confAcronym");

        // Load lexicon
        Lexicon lexicon = new Lexicon("en");
        lexicon.load("resources/springer_en.ttl");
        InstanceSource instances = new InstanceSource("http://es.dbpedia.org/sparql","en");
        

        // Load query patterns
        //QueryPatternManager qm = new QueryPatternManager();

        
        qm.addQueryPattern(new Which_C_P_I_P_I(lexicon,instances));
        qm.addQueryPattern(new Give_me_all_C_that_are_P_I(lexicon,instances));
        qm.addQueryPattern(new Give_me_all_C_that_P_I(lexicon,instances));
        qm.addQueryPattern(new Give_me_all_C_that_P_I_P_I(lexicon,instances));

        log("Query pattern load finished at " + LocalDateTime.now());  //LocalDateTime requires Java 8
    }
}
