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
import ru.gispro.petrores.doc.entities.Domain;
import ru.gispro.petrores.doc.entities.Domains;
import ru.gispro.petrores.doc.util.UserSessions;
import ru.gispro.petrores.doc.util.Util;

/**
 *
 * @author fkravchenko
 */
@Path("domains")
@Singleton
@Autowire
public class DomainRESTFacade {
    @PersistenceContext(unitName = "petro21PU")
    protected EntityManager entityManager;

    public DomainRESTFacade() {
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Domains create(Domain entity) {
        try {
            if(entity.getSite().getId()==null)
                entity.setSite(null);
            if(entity.getWell().getId()==null)
                entity.setWell(null);
            if(entity.getTypeOfWork().getId()==null)
                entity.setTypeOfWork(null);
            if(entity.getWorkProcess().getId()==null)
                entity.setWorkProcess(null);
            entity = entityManager.merge(entity);
            entityManager.flush();
            UserSessions.info("ru.gispro.petrores.doc.service.DomainRESTFacade", 
                                UserSessions.getFacadeCallRequestUser(), "CREATE_REFBOOK_ITEM", "Create Domain", entity.getId(),
                                true,  "RefBook item successfully created"); 
            return new Domains(Arrays.asList(entity), 1l);
        }
        catch(RuntimeException e){
            UserSessions.error("ru.gispro.petrores.doc.service.DomainRESTFacade", 
                      UserSessions.getFacadeCallRequestUser(), "CREATE_REFBOOK_ITEM", "Create Domain", null,
                      false,  "RefBook item creation error: " + e.toString(), e); 
            throw e;
        }
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Domains edit(Domain entity) {
        try {
            if(entity.getSite().getId()==null)
                entity.setSite(null);
            if(entity.getWell().getId()==null)
                entity.setWell(null);
            if(entity.getTypeOfWork().getId()==null)
                entity.setTypeOfWork(null);
            if(entity.getWorkProcess().getId()==null)
                entity.setWorkProcess(null);
            entity = entityManager.merge(entity);
            entityManager.flush();
            UserSessions.info("ru.gispro.petrores.doc.service.DomainRESTFacade", 
                                UserSessions.getFacadeCallRequestUser(), "EDIT_REFBOOK_ITEM", "Edit Domain", entity.getId(),
                                true,  "RefBook item successfully changed"); 
            return new Domains(Arrays.asList(entity), 1l);
        }
        catch(RuntimeException e){
            UserSessions.error("ru.gispro.petrores.doc.service.DomainRESTFacade", 
                      UserSessions.getFacadeCallRequestUser(), "EDIT_REFBOOK_ITEM", "Edit Domain", entity.getId(),
                      false,  "Edit RefBook item error: " + e.toString(), e); 
            throw e;
        }
    }

    @DELETE
    @Consumes({"application/xml", "application/json"})
    @Transactional
    public void remove(Domain entity) {
        Long id = entity.getId();
        try {
            entity = entityManager.getReference(Domain.class, entity.getId());
            entityManager.remove(entity);
            UserSessions.info("ru.gispro.petrores.doc.service.DomainRESTFacade", 
                                UserSessions.getFacadeCallRequestUser(), "REMOVE_REFBOOK_ITEM", "Remove Domain", id,
                                true,  "RefBook item successfully removed"); 
        }
        catch(RuntimeException e){
            UserSessions.error("ru.gispro.petrores.doc.service.DomainRESTFacade", 
                      UserSessions.getFacadeCallRequestUser(), "REMOVE_REFBOOK_ITEM", "Remove Domain", id,
                      false,  "RefBook item removing error: " + e.toString(), e); 
             throw e;
        }
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Domain find(@PathParam("id") Long id) {
        return entityManager.find(Domain.class, id);
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    @Transactional
    public String count() {
        try {
            Query query = entityManager.createQuery("SELECT count(o) FROM Domain AS o");
            return query.getSingleResult().toString();
        } finally {
            entityManager.close();
        }
    }

    @GET
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Domains find(@DefaultValue("-1") @QueryParam("limit") Integer maxResults,
            @DefaultValue("-1") @QueryParam("start") Integer firstResult,
            @DefaultValue("") @QueryParam("sort") String sort,
            @DefaultValue("-1") @QueryParam("parent") Long parent) {
        boolean all = maxResults < 0;
        try {
            String orderBy, where;

            if (parent != null && parent > 0) {
                where = " where o.parent.id=" + parent + " ";
            } else {
                where = "";
            }
            
            
            if (sort != null && sort.length() > 0) {
                orderBy = Util.generateOrderByFromExtJsJson(sort);
            }else{
                orderBy = "";
            }
            
            Query query = entityManager.createQuery("SELECT object(o) FROM Domain AS o " + where + orderBy);
            if (!all) {
                query.setMaxResults(maxResults);
                query.setFirstResult(firstResult);
            }
            return new Domains(query.getResultList(),
                    (Long) entityManager.createQuery("SELECT count(o) FROM Domain AS o" + where).getSingleResult());
        } finally {
            entityManager.close();
        }
    }
}
