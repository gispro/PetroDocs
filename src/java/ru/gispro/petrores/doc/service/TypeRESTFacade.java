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
import ru.gispro.petrores.doc.entities.Type;
import ru.gispro.petrores.doc.entities.Types;
import ru.gispro.petrores.doc.util.Util;

/**
 *
 * @author fkravchenko
 */
@Path("types")
@Singleton
@Autowire
public class TypeRESTFacade {
    @PersistenceContext(unitName = "petro21PU")
    protected EntityManager entityManager;

    public TypeRESTFacade() {
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Types create(Type entity) {
        entity = entityManager.merge(entity);
        entityManager.flush();
        return new Types(Arrays.asList(entity), 1);// entity;
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Types edit(Type entity) {
        entity = entityManager.merge(entity);
        entityManager.flush();
        return new Types(Arrays.asList(entity), 1);
    }

    @DELETE
    //@Path("{id}")
    @Consumes({"application/xml", "application/json"})
    @Transactional
    public void remove(Type entity){//@PathParam("id") Long id) {
        entity = entityManager.getReference(Type.class, entity.getId());
        entityManager.remove(entity);
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Type find(@PathParam("id") Long id) {
        return entityManager.find(Type.class, id);
    }

//    @GET
//    @Produces({"application/xml", "application/json"})
//    @Transactional
//    public Types findAll() {
//        return find(true, -1, -1);
//    }

    @GET
    //@Path("{max}/{first}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Types findRange(
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
            Query query = entityManager.createQuery("SELECT count(o) FROM Type AS o");
            return query.getSingleResult().toString();
        } finally {
            entityManager.close();
        }
    }

    private Types find(int maxResults, int firstResult, String sort) {
        boolean all = maxResults < 0;
        try {
            String orderBy;
            if(sort!=null&&sort.length()>0){
                orderBy = Util.generateOrderByFromExtJsJson(sort);
            }else{
                orderBy = "";
            }
            Query query = entityManager.createQuery("SELECT object(o) FROM Type AS o " + orderBy);
            if (!all) {
                query.setMaxResults(maxResults);
                query.setFirstResult(firstResult);
            }
            return new Types(query.getResultList(), 
                    (Long)entityManager.createQuery("SELECT count(o) FROM Type AS o").getSingleResult());
        } finally {
            entityManager.close();
        }
    }    
    
}
