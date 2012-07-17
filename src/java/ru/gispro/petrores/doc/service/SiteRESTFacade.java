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
import ru.gispro.petrores.doc.entities.Site;
import ru.gispro.petrores.doc.entities.Sites;
import ru.gispro.petrores.doc.util.Util;

/**
 *
 * @author fkravchenko
 */
@Path("sites")
@Singleton
@Autowire
public class SiteRESTFacade {
    @PersistenceContext(unitName = "petro21PU")
    protected EntityManager entityManager;

    public SiteRESTFacade() {
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Sites create(Site entity) {
        Site ret = entityManager.merge(entity);
        entityManager.flush();
        return new Sites(Arrays.asList(new Site[]{ret}), 1l);// Response.created(URI.create(entity.getId().toString())).build();
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Transactional
    public void edit(Site entity) {
        entityManager.merge(entity);
    }

    @DELETE
    //@Path("{id}")
    @Consumes({"application/xml", "application/json"})
    @Transactional
    public void remove(Site entity){// @PathParam("id") Long id) {
        entity = entityManager.getReference(Site.class, entity.getId());
        entityManager.remove(entity);
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Site find(@PathParam("id") Long id) {
        return entityManager.find(Site.class, id);
    }

//    @GET
//    @Produces({"application/xml", "application/json"})
//    @Transactional
//    public Sites findAll() {
//        return find(true, -1, -1);
//    }

    @GET
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Sites findRange(
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
            Query query = entityManager.createQuery("SELECT count(o) FROM Site AS o");
            return query.getSingleResult().toString();
        } finally {
            entityManager.close();
        }
    }

    private Sites find(int maxResults, int firstResult, String sort) {
        boolean all = maxResults < 0;
        try {
            String orderBy;
            if(sort!=null&&sort.length()>0){
                orderBy = Util.generateOrderByFromExtJsJson(sort);
            }else{
                orderBy = "";
            }
            Query query = entityManager.createQuery("SELECT object(o) FROM Site AS o " + orderBy);
            if (!all) {
                query.setMaxResults(maxResults);
                query.setFirstResult(firstResult);
            }
            return new Sites(query.getResultList(), 
                    (Long)entityManager.createQuery("SELECT count(o) FROM Site AS o").getSingleResult());
        } finally {
            entityManager.close();
        }
    }
    
}
