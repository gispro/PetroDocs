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
import ru.gispro.petrores.doc.entities.GeoType;
import ru.gispro.petrores.doc.entities.GeoTypes;
import ru.gispro.petrores.doc.util.Util;

/**
 *
 * @author fkravchenko
 */
@Path("geoTypes")
@Singleton
@Autowire
public class GeoTypeRESTFacade {
    @PersistenceContext(unitName = "petro21PU")
    protected EntityManager entityManager;

    public GeoTypeRESTFacade() {
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public GeoTypes create(GeoType entity) {
        entity = entityManager.merge(entity);
        entityManager.flush();
        return new GeoTypes(Arrays.asList(entity), 1l);
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public GeoTypes edit(GeoType entity) {
        entity = entityManager.merge(entity);
        return new GeoTypes(Arrays.asList(entity), 1l);
    }

    @DELETE
    @Consumes({"application/xml", "application/json"})
    @Transactional
    public void remove(GeoType entity) {
        entity = entityManager.getReference(GeoType.class, entity.getId());
        entityManager.remove(entity);
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public GeoType find(@PathParam("id") Integer id) {
        return entityManager.find(GeoType.class, id);
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    @Transactional
    public String count() {
        try {
            Query query = entityManager.createQuery("SELECT count(o) FROM GeoType AS o");
            return query.getSingleResult().toString();
        } finally {
            entityManager.close();
        }
    }

    @GET
    @Produces({"application/xml", "application/json"})
    @Transactional
    public GeoTypes find(@DefaultValue("-1") @QueryParam("limit") Integer maxResults,
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
            Query query = entityManager.createQuery("SELECT object(o) FROM GeoType AS o " + orderBy);
            if (!all) {
                query.setMaxResults(maxResults);
                query.setFirstResult(firstResult);
            }
            return new GeoTypes(query.getResultList(),
                    (Long) entityManager.createQuery("SELECT count(o) FROM GeoType AS o").getSingleResult());
        } finally {
            entityManager.close();
        }
    }
}
