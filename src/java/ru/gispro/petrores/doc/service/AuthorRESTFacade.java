/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.service;

import com.sun.jersey.api.spring.Autowire;
import com.sun.jersey.spi.resource.Singleton;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.transaction.annotation.Transactional;
import ru.gispro.petrores.doc.entities.Author;
import ru.gispro.petrores.doc.entities.Authors;
import ru.gispro.petrores.doc.util.Util;

/**
 *
 * @author fkravchenko
 */
@Path("authors")
@Singleton
@Autowire
public class AuthorRESTFacade {
    @PersistenceContext(unitName = "petro21PU")
    protected EntityManager entityManager;

    public AuthorRESTFacade() {
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Authors create(Author entity) {
        if(entity.getOrganization().getId()==null)
            entity.setOrganization(null);
        entity = entityManager.merge(entity);
        entityManager.flush();
        return new Authors(Arrays.asList(entity), 1l);
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Authors edit(Author entity) {
        
        if(entity.getLogin()!=null){
            Authors foundByLogin = this.find(entity.getLogin());
            List<Author>list = foundByLogin.getAuthors();
            if(list.size()>0){
                if(list.size()>1){
                    System.out.println("Warning: duplicate login " + entity.getLogin());
                }
                for(Author author: list){
                    if(author.getId()==entity.getId()){
        entity = entityManager.merge(entity);
                    }else{
                        if(list.size()==1){
                            // means that trying to duplicate the login
                            throw new RuntimeException("Duplicate login " + entity.getLogin());
                            
                        }else{
                            System.out.println("Warning: cleaning duplicate login " + 
                                    entity.getLogin() + 
                                    " in Author id " + 
                                    author.getId());
                            author.setLogin(null);
                        }
                    }
                }
            }
        }else{
            entity = entityManager.merge(entity);
        }
        entityManager.flush();
        return new Authors(Arrays.asList(entity), 1l);
    }

    @DELETE
    @Consumes({"application/xml", "application/json"})
    @Transactional
    public void remove(Author entity) {
        entity = entityManager.getReference(Author.class, entity.getId());
        entityManager.remove(entity);
    }

    @GET
    @Path("{id:\\d+}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Author find(@PathParam("id") Long id) {
        return entityManager.find(Author.class, id);
    }

    @GET
    @Path("{login}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Authors find(@PathParam("login") String login){
        
        //resp.setHeader("Content-Type", "application/octet-stream");// "application/json");//"text/html; charset=UTF-8"); // "application/json");
        
        Query q = entityManager.createNamedQuery("Author.findByLogin");
        q.setParameter("login", login);
        List<Author> list = q.getResultList();
        return new Authors(list, (long)list.size());
        //return (list.isEmpty()?null:list.get(0));

        /*if(!list.isEmpty()){ // :)
            ObjectMapper mapper = new ObjectMapper();
            JsonGenerator generator = mapper.getJsonFactory().createJsonGenerator(resp.getOutputStream(), JsonEncoding.UTF8);
            ObjectNode json = mapper.createObjectNode();
            json.put("success", true);
            json.putPOJO("data", list.get(0));
            mapper.writeTree(generator, json);
            generator.flush();
        }else{
            ObjectMapper mapper = new ObjectMapper();
            JsonGenerator generator = mapper.getJsonFactory().createJsonGenerator(resp.getOutputStream(), JsonEncoding.UTF8);
            ObjectNode json = mapper.createObjectNode();
            json.put("success", false);
            mapper.writeTree(generator, json);
            generator.flush();
        }*/
        
        
        
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    @Transactional
    public String count() {
        try {
            Query query = entityManager.createQuery("SELECT count(o) FROM Author AS o");
            return query.getSingleResult().toString();
        } finally {
            entityManager.close();
        }
    }

    @GET
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Authors find(@DefaultValue("-1") @QueryParam("limit") Integer maxResults,
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
            Query query = entityManager.createQuery("SELECT object(o) FROM Author AS o " + orderBy);
            if (!all) {
                query.setMaxResults(maxResults);
                query.setFirstResult(firstResult);
            }
            return new Authors(query.getResultList(),
                    (Long) entityManager.createQuery("SELECT count(o) FROM Author AS o").getSingleResult());
        } finally {
            entityManager.close();
        }
    }
}
