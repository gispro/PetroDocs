/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.gispro.petrores.doc.entities.File;
import ru.gispro.petrores.doc.util.UserSessions;

/**
 *
 * @author fkravchenko
 */
@Controller
@RequestMapping(value = "/file/{id}/{anyName}")
public class GetFileController {
    @PersistenceContext(unitName = "petro21PU")
    protected EntityManager entityManager;
    private String rootPath = null;
    
    
    @RequestMapping(method = RequestMethod.GET)
    @Transactional
    public void get(@PathVariable("id") Long id, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        File file = null;
        try {
            file = entityManager.getReference(File.class, id);
            resp.setContentType(file.getMimeType());

            java.io.File realFile = new java.io.File(getRootPath(req), file.getPath());
            FileInputStream fis = new FileInputStream(realFile);
            BufferedInputStream bis = new BufferedInputStream(fis);

            BufferedOutputStream bos = new BufferedOutputStream(resp.getOutputStream());
            int byt;
            while((byt = bis.read())>=0){
                bos.write(byt);
            }
            bos.flush();
            UserSessions.info("ru.gispro.petrores.doc.controller.GetFileController", 
              req.getRemoteUser(), "GET_FILE", "Get file", null,
              true,  "Get file \""+ file.getPath()+"\""); 
            bos.close();
            bis.close();
            fis.close();
        }
        catch( Exception exc){
            UserSessions.error("ru.gispro.petrores.doc.controller.GetFileController", 
              req.getRemoteUser(), "GET_FILE", "Get file", null,
              false,  "Get file "+
                        (file == null 
                              ? (" with id \""+ id) 
                              : ("\""+file.getPath()))+
                      "\" error: "+exc.toString(), exc); 

            throw exc;
        }
    }    
    private String getRootPath(HttpServletRequest req){
        if(rootPath==null){
            rootPath = req.getSession().getServletContext().getInitParameter("documentsPath");
            if(rootPath==null)
                rootPath = java.io.File.separator;
            else if(!rootPath.endsWith(java.io.File.separator))
                rootPath = rootPath + java.io.File.separator;
        }
        return rootPath;
    }
}
