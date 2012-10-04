/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.controller;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.gispro.petrores.doc.util.UserSessions;

/**
 *
 * @author abusurin
 */
@Controller
@RequestMapping(value = "/log/{category}/{cls}")
public class LogController {
    @RequestMapping(method = RequestMethod.GET)
    public void get(@PathVariable("category") String category, @PathVariable("cls") String cls, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String m = "Log Successfull";
        String mess = req.getParameter("mess"),
                opCode = req.getParameter("opCode"),
                opName = req.getParameter("opName"),
                docID = req.getParameter("docID"),
                sOK = req.getParameter("ok");
        try {
            UserSessions.log(category, cls, req.getRemoteUser(), opCode, opName, docID, "OK".equalsIgnoreCase(sOK), mess, null);
        }
        catch(Exception e){
            m = "Logging error: " + e.toString(); 
        }
        PrintWriter out = resp.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet NewServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Message \""+mess+"\" logged" + req.getContextPath() + " ... "+m+"</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {            
            out.close();
        }    }
}