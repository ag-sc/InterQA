package interQA;

import com.google.gson.Gson;
import interQA.lexicon.InstanceSource;
import interQA.lexicon.Lexicon;
import interQA.patterns.QueryPattern1_1;
import interQA.patterns.QueryPattern9_1;
import interQA.patterns.QueryPattern9_2;
import interQA.patterns.QueryPatternManager;

import java.io.IOException;

import java.io.*;
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
        response.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();
        String command = request.getParameter("command"); ///ServletInterQA?command=whatever
        List<String> options = qm.getUIoptions();
        if (options == null){
            out.println("No options");
            return;
        }
        switch (command){
            case "getOptions":  //command=getOptions  server returns a list of option
                //Warning! set response header/status BEFORE sending any content to the client
                response.setStatus(response.SC_OK); //Code 200
                out.write(gson.toJson(options));
                break;
            case "selected":    //command=selected&selection=text   server is informed. Nothing in returned
                response.setStatus(response.SC_OK);
                String text = request.getParameter("selection");
                List<String> availableQPNames = qm.userSentence(text);
                out.write(gson.toJson(availableQPNames));
                break;
            case "getQueries":    //command=getQueries server returns a list of SPARQL queries
                response.setStatus(response.SC_OK);
                out.write(gson.toJson(qm.buildSPARQLqueries()));
                break;
            default:           // unsupported command
                response.setStatus(response.SC_BAD_REQUEST); //code 400
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
        // Initialization code... loading local files
        //String fullPath = getServletContext().getRealPath("/") + "WEB-INF" + java.io.File.separator;
        //String fileName1 = fullPath + "test1.rdf";
        //String fileName2 = fullPath + "test2.rdf";

        // Load lexicon
        Lexicon lexicon = new Lexicon();
        lexicon.load("resources/dbpedia_en.rdf");
        InstanceSource instances = new InstanceSource("http://dbpedia.org/sparql");

        // Load query patterns
        qm = new QueryPatternManager();
        qm.addQueryPattern(new QueryPattern1_1(lexicon));
        qm.addQueryPattern(new QueryPattern9_1(lexicon,instances));
        qm.addQueryPattern(new QueryPattern9_2(lexicon,instances));
    }
}
