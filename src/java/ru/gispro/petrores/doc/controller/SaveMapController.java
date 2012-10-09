/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.controller;

import java.io.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import ru.gispro.petrores.doc.util.UserSessions;

/**
 *
 * @author fkravchenko
 */
@Controller
@RequestMapping(value = "/maps")
public class SaveMapController {
    
    @RequestMapping(value = "/{name:.*}", method = RequestMethod.GET)
    public void get(@PathVariable("name") String name, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        resp.setContentType("application/xml");
        name = new String(name.getBytes("ISO-8859-1"), "UTF-8");
        
        
        String startPath = req.getSession().getServletContext().getInitParameter("mapsPath");
        if(!startPath.startsWith("/")){
            startPath = req.getSession().getServletContext().getRealPath("/" + startPath);
        }
        
        java.io.File realFile = new java.io.File(startPath, name);
        FileInputStream fis = new FileInputStream(realFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        
        BufferedOutputStream bos = new BufferedOutputStream(resp.getOutputStream());
        int byt;
        while((byt = bis.read())>=0){
            bos.write(byt);
        }
        bos.flush();
        bos.close();
        bis.close();
        fis.close();
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public void getAll(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        
        String startPath = req.getSession().getServletContext().getInitParameter("mapsPath");
        if(!startPath.startsWith("/")){
            startPath = req.getSession().getServletContext().getRealPath("/" + startPath);
        }
        
        java.io.File realFolder = new java.io.File(startPath);
        File[]files = realFolder.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.getName().toUpperCase().endsWith(".JSON");
            }
        });
        
        PrintWriter out = resp.getWriter();
        for(File file : files){
            out.println(file.getName());
        }
    }
    
    @RequestMapping(value = "/{name:.*}", method = RequestMethod.PUT)
    public void put(@PathVariable("name") String name, HttpServletRequest req, HttpServletResponse resp) throws Exception {
     /*   MDC.put("user", req.getRemoteUser());
        MDC.put("OP_CODE", "SAVE_PROFILE");
        MDC.put("OP_NAME", "Save profile");
        MDC.put("DOC_ID", "-");
        MDC.put("OP_STATUS", "Success");
        Logger lgr = Logger.getLogger("ru.gispro.petrores.doc.controller.SaveMapController");
*/
        try {
            resp.setContentType("application/xml");
            
            name = new String(name.getBytes("ISO-8859-1"), "UTF-8");

            String startPath = req.getSession().getServletContext().getInitParameter("mapsPath");
            if(!startPath.startsWith("/")){
                startPath = req.getSession().getServletContext().getRealPath("/" + startPath);
            }

            java.io.File realFile = new java.io.File(startPath, name);
            if(realFile.exists())
                realFile.delete();

            FileOutputStream fos = new FileOutputStream(realFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            BufferedInputStream bis = new BufferedInputStream(req.getInputStream());
            int byt;
            while((byt = bis.read())>=0){
                bos.write(byt);
            }
            bos.flush();
            bos.close();
            bis.close();
            fos.close();
            UserSessions.info("ru.gispro.petrores.doc.controller.SaveMapController", 
                          req.getRemoteUser(), "SAVE_PROFILE", "Save profile", null,
                          true,  "Profile saved in " + name); 
        }
        catch(Exception e){
            UserSessions.error("ru.gispro.petrores.doc.controller.SaveMapController", 
                          req.getRemoteUser(), "SAVE_PROFILE", "Save profile", null,
                          true,  "Save profile in "+name+" error: "+e.toString(), e); 
            throw e;
        }
    }
}
