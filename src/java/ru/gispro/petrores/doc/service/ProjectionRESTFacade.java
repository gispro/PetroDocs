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
import ru.gispro.petrores.doc.entities.Projection;
import ru.gispro.petrores.doc.entities.Projections;
import ru.gispro.petrores.doc.util.UserSessions;
import ru.gispro.petrores.doc.util.Util;

/**
 *
 * @author fkravchenko
 */
@Path("projections")
@Singleton
@Autowire
public class ProjectionRESTFacade {
    @PersistenceContext(unitName = "petro21PU")
    protected EntityManager entityManager;

    public ProjectionRESTFacade() {
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Projections create(Projection entity) {
        try {
            entity = entityManager.merge(entity);
            entityManager.flush();
            UserSessions.info("ru.gispro.petrores.doc.service.ProjectionRESTFacade", 
                                UserSessions.getFacadeCallRequestUser(), "CREATE_REFBOOK_ITEM", "Create Projection", entity.getCode(),
                                true,  "RefBook item successfully created"); 
            return new Projections(Arrays.asList(entity), 1l);
        }
        catch(RuntimeException e){
            UserSessions.error("ru.gispro.petrores.doc.service.ProjectionRESTFacade", 
                      UserSessions.getFacadeCallRequestUser(), "CREATE_REFBOOK_ITEM", "Create Projection", null,
                      false,  "RefBook item creation error: " + e.toString(), e); 
            throw e;
        }
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Projections edit(Projection entity) {
        try {
            entity = entityManager.merge(entity);
            UserSessions.info("ru.gispro.petrores.doc.service.ProjectionRESTFacade", 
                                UserSessions.getFacadeCallRequestUser(), "EDIT_REFBOOK_ITEM", "Edit Projection", entity.getCode(),
                                true,  "RefBook item successfully changed"); 
            return new Projections(Arrays.asList(entity), 1l);
        }
        catch(RuntimeException e){
            UserSessions.error("ru.gispro.petrores.doc.service.ProjectionRESTFacade", 
                      UserSessions.getFacadeCallRequestUser(), "EDIT_REFBOOK_ITEM", "Edit Projection", entity.getCode(),
                      false,  "Edit RefBook item error: " + e.toString(), e); 
            throw e;
        }
    }

    @DELETE
    @Consumes({"application/xml", "application/json"})
    @Transactional
    public void remove(Projection entity) {
        String code = entity.getCode();
        try {
            entity = entityManager.getReference(Projection.class, entity.getCode());
            entityManager.remove(entity);
            UserSessions.info("ru.gispro.petrores.doc.service.ProjectionRESTFacade", 
                                UserSessions.getFacadeCallRequestUser(), "REMOVE_REFBOOK_ITEM", "Remove Projection", code,
                                true,  "RefBook item successfully removed"); 
        }
        catch(RuntimeException e){
            UserSessions.error("ru.gispro.petrores.doc.service.ProjectionRESTFacade", 
                      UserSessions.getFacadeCallRequestUser(), "REMOVE_REFBOOK_ITEM", "Remove Projection", code,
                      false,  "RefBook item removing error: " + e.toString(), e); 
             throw e;
        }
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Projection find(@PathParam("id") Integer id) {
        return entityManager.find(Projection.class, id);
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    @Transactional
    public String count() {
        try {
            Query query = entityManager.createQuery("SELECT count(o) FROM Projection AS o");
            return query.getSingleResult().toString();
        } finally {
            entityManager.close();
        }
    }

    @GET
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Projections find(@DefaultValue("-1") @QueryParam("limit") Integer maxResults,
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
            Query query = entityManager.createQuery("SELECT object(o) FROM Projection AS o " + orderBy);
            if (!all) {
                query.setMaxResults(maxResults);
                query.setFirstResult(firstResult);
            }
            return new Projections(query.getResultList(),
                    (Long) entityManager.createQuery("SELECT count(o) FROM Projection AS o").getSingleResult());
        } finally {
            entityManager.close();
        }
    }
}
