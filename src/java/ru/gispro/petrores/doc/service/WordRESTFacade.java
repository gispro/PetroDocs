/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.service;

import com.sun.jersey.api.spring.Autowire;
import com.sun.jersey.spi.resource.Singleton;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.*;
import org.springframework.transaction.annotation.Transactional;
import ru.gispro.petrores.doc.entities.Word;
import ru.gispro.petrores.doc.entities.Words;
import ru.gispro.petrores.doc.util.Util;

/**
 *
 * @author fkravchenko
 */
@Path("words")
@Singleton
@Autowire
public class WordRESTFacade {
    @PersistenceContext(unitName = "petro21PU")
    protected EntityManager entityManager;

    public WordRESTFacade() {
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Words create(Word entity) {
        entity = entityManager.merge(entity);
        entityManager.flush();
        return new Words(Arrays.asList(entity), 1l);
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Words edit(Word entity) {
        entity = entityManager.merge(entity);
        return new Words(Arrays.asList(entity), 1l);
    }

    @DELETE
    @Consumes({"application/xml", "application/json"})
    @Transactional
    public void remove(Word entity) {
        entity = entityManager.getReference(Word.class, entity.getWord());
        entityManager.remove(entity);
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Word find(@PathParam("id") String id) {
        return entityManager.find(Word.class, id);
    }

    /*@GET
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Words findByStart(@QueryParam("wordStart") String wordStart) {
            Query query = entityManager.createQuery("SELECT object(o) FROM Word AS o "
                    + "where o.word like :wordStart order by o.word ");
            //Query queryCount = entityManager.createQuery("SELECT object(o) FROM Word AS o "
            //        + "where o.word like :wordStart");
            query.setParameter("wordStart", wordStart + "%");
            //queryCount.setParameter("wordStart", wordStart + "%");

            List<Word> l = query.getResultList();
            
            return new Words(l, (long)l.size());
    }*/

    @GET
    @Path("count")
    @Produces("text/plain")
    @Transactional
    public String count() {
        try {
            Query query = entityManager.createQuery("SELECT count(o) FROM Word AS o");
            return query.getSingleResult().toString();
        } finally {
            entityManager.close();
        }
    }

    @GET
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Words find(@DefaultValue("-1") @QueryParam("limit") Integer maxResults,
            @DefaultValue("-1") @QueryParam("start") Integer firstResult,
            @DefaultValue("") @QueryParam("sort") String sort,
            @DefaultValue("") @QueryParam("wordStart") String wordStart) {
        boolean all = maxResults < 0;
        try {
            String orderBy;
            String where;
            if (sort != null && sort.length() > 0) {
                orderBy = Util.generateOrderByFromExtJsJson(sort);
            } else {
                orderBy = " order by o.word ";
            }
            if(wordStart!=null && wordStart.length() > 0){
                where = " where o.word like :wordStart ";
            }else{
                where = "";
            }
            
            Query query = entityManager.createQuery("SELECT object(o) FROM Word AS o " + where + orderBy);
            Query queryCount = entityManager.createQuery("SELECT count(o) FROM Word AS o " + where);
            if(where.length()>0){
                query.setParameter("wordStart", wordStart + "%");
                queryCount.setParameter("wordStart", wordStart + "%");
            }
            if (!all) {
                query.setMaxResults(maxResults);
                query.setFirstResult(firstResult);
            }
            return new Words(query.getResultList(),
                    (Long) queryCount.getSingleResult());
        } finally {
            entityManager.close();
        }
    }
}
