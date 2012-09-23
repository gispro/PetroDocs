/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.service;

import com.sun.jersey.api.spring.Autowire;
import com.sun.jersey.spi.resource.Singleton;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.*;
import org.springframework.transaction.annotation.Transactional;
import ru.gispro.petrores.doc.entities.File;
import ru.gispro.petrores.doc.entities.Files;
import ru.gispro.petrores.doc.util.UserSessions;
import ru.gispro.petrores.doc.util.Util;

/**
 *
 * @author fkravchenko
 */
@Path("files")
@Singleton
@Autowire
public class FileRESTFacade {
    @PersistenceContext(unitName = "petro21PU")
    protected EntityManager entityManager;

    public FileRESTFacade() {
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Files create(File entity) {
        try {
            entity = entityManager.merge(entity);
            entityManager.flush();
            UserSessions.info("ru.gispro.petrores.doc.service.FileRESTFacade", 
                                UserSessions.getFacadeCallRequestUser(), "CREATE_REFBOOK_ITEM", "Create File Type", entity.getId(),
                                true,  "RefBook item successfully created"); 
            return new Files(Arrays.asList(entity), 1l);
        }
        catch(RuntimeException e){
            UserSessions.error("ru.gispro.petrores.doc.service.FileRESTFacade", 
                      UserSessions.getFacadeCallRequestUser(), "CREATE_REFBOOK_ITEM", "Create File Type", null,
                      false,  "RefBook item creation error: " + e.toString(), e); 
            throw e;
        }
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Files edit(File entity) {
        try {
            entity = entityManager.merge(entity);
            UserSessions.info("ru.gispro.petrores.doc.service.FileRESTFacade", 
                                UserSessions.getFacadeCallRequestUser(), "EDIT_REFBOOK_ITEM", "Edit File Type", entity.getId(),
                                true,  "RefBook item successfully changed"); 
            return new Files(Arrays.asList(entity), 1l);
        }
        catch(RuntimeException e){
            UserSessions.error("ru.gispro.petrores.doc.service.FileRESTFacade", 
                      UserSessions.getFacadeCallRequestUser(), "EDIT_REFBOOK_ITEM", "Edit File Type", entity.getId(),
                      false,  "Edit RefBook item error: " + e.toString(), e); 
            throw e;
        }
    }

    @DELETE
    @Consumes({"application/xml", "application/json"})
    @Transactional
    public void remove(File entity) {
        Long id = entity.getId();
        try {
            entity = entityManager.getReference(File.class, entity.getId());
            entityManager.remove(entity);
            UserSessions.info("ru.gispro.petrores.doc.service.FileRESTFacade", 
                                UserSessions.getFacadeCallRequestUser(), "REMOVE_REFBOOK_ITEM", "Remove File Type", id,
                                true,  "RefBook item successfully removed"); 
        }
        catch(RuntimeException e){
            UserSessions.error("ru.gispro.petrores.doc.service.FileRESTFacade", 
                      UserSessions.getFacadeCallRequestUser(), "REMOVE_REFBOOK_ITEM", "Remove File Type", id,
                      false,  "RefBook item removing error: " + e.toString(), e); 
             throw e;
        }
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public File find(@PathParam("id") Long id) {
        return entityManager.find(File.class, id);
    }

    /*@GET
    //@Path("{path}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public File findByPath(@QueryParam("path") String path) {
        Query query = entityManager.createQuery("SELECT o FROM File AS o WHERE o.path = :path");
        query.setParameter("path", path);
        return (File) query.getSingleResult();
        //return entityManager.find(File.class, id);
    }*/

    @GET
    @Path("count")
    @Produces("text/plain")
    @Transactional
    public String count() {
        try {
            Query query = entityManager.createQuery("SELECT count(o) FROM File AS o");
            return query.getSingleResult().toString();
        } finally {
            entityManager.close();
        }
    }

    @GET
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Files find(@DefaultValue("-1") @QueryParam("limit") Integer maxResults,
            @DefaultValue("-1") @QueryParam("start") Integer firstResult,
            @DefaultValue("") @QueryParam("sort") String sort,
            @QueryParam("path") String path) {
        
        if(path!=null){
            Query query = entityManager.createQuery("SELECT o FROM File AS o WHERE o.path = :path");
            query.setParameter("path", path);
            List<File>res = query.getResultList();
            //File ret = (File) query.getSingleResult();
            return new Files(res, (long)res.size());
        }
        
        boolean all = maxResults < 0;
        try {
            String orderBy;
            if (sort != null && sort.length() > 0) {
                orderBy = Util.generateOrderByFromExtJsJson(sort);
            } else {
                orderBy = "";
            }
            Query query = entityManager.createQuery("SELECT object(o) FROM File AS o " + orderBy);
            if (!all) {
                query.setMaxResults(maxResults);
                query.setFirstResult(firstResult);
            }
            return new Files(query.getResultList(),
                    (Long) entityManager.createQuery("SELECT count(o) FROM File AS o").getSingleResult());
        } finally {
            entityManager.close();
        }
    }
}
