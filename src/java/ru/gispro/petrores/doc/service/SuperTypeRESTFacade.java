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
import ru.gispro.petrores.doc.entities.SuperType;
import ru.gispro.petrores.doc.entities.SuperTypes;
import ru.gispro.petrores.doc.util.UserSessions;
import ru.gispro.petrores.doc.util.Util;

/**
 *
 * @author fkravchenko
 */
@Path("superTypes")
@Singleton
@Autowire
public class SuperTypeRESTFacade {
    @PersistenceContext(unitName = "petro21PU")
    protected EntityManager entityManager;

    public SuperTypeRESTFacade() {
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public SuperTypes create(SuperType entity) {
        try {
            entity = entityManager.merge(entity);
            entityManager.flush();
            UserSessions.info("ru.gispro.petrores.doc.service.SuperTypeRESTFacade", 
                                UserSessions.getFacadeCallRequestUser(), "CREATE_REFBOOK_ITEM", "Create General Type", entity.getId(),
                                true,  "RefBook item successfully created"); 
            return new SuperTypes(Arrays.asList(entity), 1);
        }
        catch(RuntimeException e){
            UserSessions.error("ru.gispro.petrores.doc.service.SuperTypeRESTFacade", 
                      UserSessions.getFacadeCallRequestUser(), "CREATE_REFBOOK_ITEM", "Create General Type", null,
                      false,  "RefBook item creation error: " + e.toString(), e); 
            throw e;
        }
        
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Transactional
    public void edit(SuperType entity) {
        try {
            entityManager.merge(entity);
            UserSessions.info("ru.gispro.petrores.doc.service.SuperTypeRESTFacade", 
                                UserSessions.getFacadeCallRequestUser(), "EDIT_REFBOOK_ITEM", "Edit General Type", entity.getId(),
                                true,  "RefBook item successfully changed"); 
        }
        catch(RuntimeException e){
            UserSessions.error("ru.gispro.petrores.doc.service.SuperTypeRESTFacade", 
                      UserSessions.getFacadeCallRequestUser(), "EDIT_REFBOOK_ITEM", "Edit General Type", entity.getId(),
                      false,  "Edit RefBook item error: " + e.toString(), e); 
            throw e;
        }
    }

    @DELETE
    @Transactional
    @Consumes({"application/xml", "application/json"})
    public void remove(SuperType entity) {
        Integer id = entity.getId();
        try {
            entity = entityManager.getReference(SuperType.class, entity.getId());
            entityManager.remove(entity);
            UserSessions.info("ru.gispro.petrores.doc.service.SuperTypeRESTFacade", 
                                UserSessions.getFacadeCallRequestUser(), "REMOVE_REFBOOK_ITEM", "Remove General Type", id,
                                true,  "RefBook item successfully removed"); 
        }
        catch(RuntimeException e){
            UserSessions.error("ru.gispro.petrores.doc.service.SuperTypeRESTFacade", 
                      UserSessions.getFacadeCallRequestUser(), "REMOVE_REFBOOK_ITEM", "Remove General Type", id,
                      false,  "RefBook item removing error: " + e.toString(), e); 
             throw e;
        }
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public SuperType find(@PathParam("id") Integer id) {
        return entityManager.find(SuperType.class, id);
    }

//    @GET
//    @Produces({"application/xml", "application/json"})
//    @Transactional
//    public List<SuperType> findAll() {
//        return find(true, -1, -1);
//    }

    @GET
    //@Path("{max}/{first}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public SuperTypes findRange(
            @DefaultValue("-1") @QueryParam("limit") Integer max, 
            @DefaultValue("-1") @QueryParam("start") Integer first,
            @DefaultValue("") @QueryParam("sort") String sort
            ) {
        return find(max, first, sort);
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    @Transactional
    public String count() {
        try {
            Query query = entityManager.createQuery("SELECT count(o) FROM SuperType AS o");
            return query.getSingleResult().toString();
        } finally {
            entityManager.close();
        }
    }

    private SuperTypes find(int maxResults, int firstResult, String sort) {
        boolean all = maxResults < 0;
        try {
            String orderBy;
            if(sort!=null&&sort.length()>0){
                orderBy = Util.generateOrderByFromExtJsJson(sort);
            }else{
                orderBy = "";
            }
            Query query = entityManager.createQuery("SELECT object(o) FROM SuperType AS o" + orderBy);
            if (!all) {
                query.setMaxResults(maxResults);
                query.setFirstResult(firstResult);
            }
            return new SuperTypes(query.getResultList(), 
                    (Long)entityManager.createQuery("SELECT count(o) FROM SuperType AS o").getSingleResult());
        } finally {
            entityManager.close();
        }
    }
    
}
