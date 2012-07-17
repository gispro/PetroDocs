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
import ru.gispro.petrores.doc.entities.TypeExt;
import ru.gispro.petrores.doc.entities.TypeExts;
import ru.gispro.petrores.doc.util.Util;

/**
 *
 * @author fkravchenko
 */
@Path("typeExts")
@Singleton
@Autowire
public class TypeExtRESTFacade {
    @PersistenceContext(unitName = "petro21PU")
    protected EntityManager entityManager;

    public TypeExtRESTFacade() {
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public TypeExts create(TypeExt entity) {
        entity = entityManager.merge(entity);
        entityManager.flush();
        return new TypeExts(Arrays.asList(entity), 1);
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public TypeExts edit(TypeExt entity) {
        entity = entityManager.merge(entity);
        return new TypeExts(Arrays.asList(entity), 1);
    }

    @DELETE
    @Transactional
    public void remove(TypeExt entity) {
        entity = entityManager.getReference(TypeExt.class, entity.getId());
        entityManager.remove(entity);
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public TypeExt find(@PathParam("id") Long id) {
        return entityManager.find(TypeExt.class, id);
    }

//    @GET
//    @Produces({"application/xml", "application/json"})
//    @Transactional
//    public TypeExts findAll() {
//        return find(true, -1, -1);
//    }

    @GET
    //@Path("{max}/{first}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public TypeExts findRange(
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
            Query query = entityManager.createQuery("SELECT count(o) FROM TypeExt AS o");
            return query.getSingleResult().toString();
        } finally {
            entityManager.close();
        }
    }

    private TypeExts find(int maxResults, int firstResult, String sort) {
        boolean all = maxResults < 0;
        try {
            String orderBy;
            if(sort!=null&&sort.length()>0){
                orderBy = Util.generateOrderByFromExtJsJson(sort);
            }else{
                orderBy = "";
            }
            Query query = entityManager.createQuery("SELECT object(o) FROM TypeExt AS o" + orderBy);
            if (!all) {
                query.setMaxResults(maxResults);
                query.setFirstResult(firstResult);
            }
            return new TypeExts(query.getResultList(), 
                    (Long)entityManager.createQuery("SELECT count(o) FROM TypeExt AS o").getSingleResult());
        } finally {
            entityManager.close();
        }
    }
    
}
