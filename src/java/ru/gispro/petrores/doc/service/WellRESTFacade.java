/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.service;

import com.sun.jersey.api.spring.Autowire;
import com.sun.jersey.spi.resource.Singleton;
import java.util.Arrays;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.*;
import org.springframework.transaction.annotation.Transactional;
import ru.gispro.petrores.doc.entities.Well;
import ru.gispro.petrores.doc.entities.Wells;
import ru.gispro.petrores.doc.util.UserSessions;
import ru.gispro.petrores.doc.util.Util;

/**
 *
 * @author fkravchenko
 */
@Path("wells")
@Singleton
@Autowire
public class WellRESTFacade {

    @PersistenceContext(unitName = "petro21PU")
    protected EntityManager entityManager;

    public WellRESTFacade() {
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Wells create(Well entity) {
        try {
            entity = entityManager.merge(entity);
            entityManager.flush();
            UserSessions.info("ru.gispro.petrores.doc.service.WellRESTFacade", 
                                UserSessions.getFacadeCallRequestUser(), "CREATE_REFBOOK_ITEM", "Create Well", entity.getId(),
                                true,  "RefBook item successfully created"); 
            return new Wells(Arrays.asList(entity), 1l);
        }
        catch(RuntimeException e){
            UserSessions.error("ru.gispro.petrores.doc.service.WellRESTFacade", 
                      UserSessions.getFacadeCallRequestUser(), "CREATE_REFBOOK_ITEM", "Create Well", null,
                      false,  "RefBook item creation error: " + e.toString(), e); 
            throw e;
        }
        
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Wells edit(Well entity) {
        try {
            entity = entityManager.merge(entity);
            UserSessions.info("ru.gispro.petrores.doc.service.WellRESTFacade", 
                                UserSessions.getFacadeCallRequestUser(), "EDIT_REFBOOK_ITEM", "Edit Well", entity.getId(),
                                true,  "RefBook item successfully changed"); 
            return new Wells(Arrays.asList(entity), 1l);
        }
        catch(RuntimeException e){
            UserSessions.error("ru.gispro.petrores.doc.service.WellRESTFacade", 
                      UserSessions.getFacadeCallRequestUser(), "EDIT_REFBOOK_ITEM", "Edit Well", entity.getId(),
                      false,  "Edit RefBook item error: " + e.toString(), e); 
            throw e;
        }
    }

    @DELETE
    @Consumes({"application/xml", "application/json"})
    @Transactional
    public void remove(Well entity) {
        Long id = entity.getId();
        try {
            entity = entityManager.getReference(Well.class, entity.getId());
            entityManager.remove(entity);
            UserSessions.info("ru.gispro.petrores.doc.service.WellRESTFacade", 
                                UserSessions.getFacadeCallRequestUser(), "REMOVE_REFBOOK_ITEM", "Remove Well", id,
                                true,  "RefBook item successfully removed"); 
        }
        catch(RuntimeException e){
            UserSessions.error("ru.gispro.petrores.doc.service.WellRESTFacade", 
                      UserSessions.getFacadeCallRequestUser(), "REMOVE_REFBOOK_ITEM", "Remove Well", id,
                      false,  "RefBook item removing error: " + e.toString(), e); 
             throw e;
        }
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Well find(@PathParam("id") Long id) {
        return entityManager.find(Well.class, id);
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    @Transactional
    public String count() {
        try {
            Query query = entityManager.createQuery("SELECT count(o) FROM Well AS o");
            return query.getSingleResult().toString();
        } finally {
            entityManager.close();
        }
    }

    @GET
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Wells find(@DefaultValue("-1") @QueryParam("limit") Integer maxResults,
            @DefaultValue("-1") @QueryParam("start") Integer firstResult,
            @DefaultValue("") @QueryParam("sort") String sort) {
        boolean all = maxResults < 0;
        try {
            String orderBy;
            if (sort != null && sort.length() > 0) {
                orderBy = Util.generateOrderByFromExtJsJson(sort);
            } else {
                orderBy = "";
            }
            Query query = entityManager.createQuery("SELECT object(o) FROM Well AS o " + orderBy);
            if (!all) {
                query.setMaxResults(maxResults);
                query.setFirstResult(firstResult);
            }
            return new Wells(query.getResultList(),
                    (Long) entityManager.createQuery("SELECT count(o) FROM Well AS o").getSingleResult());
        } finally {
            entityManager.close();
        }
    }
}
