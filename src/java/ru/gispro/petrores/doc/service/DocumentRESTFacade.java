/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.service;

import com.sun.jersey.api.spring.Autowire;
import com.sun.jersey.spi.resource.Singleton;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.gispro.petrores.doc.entities.*;
import ru.gispro.petrores.doc.util.UserSessions;
import ru.gispro.petrores.doc.util.Util;

/**
 *
 * @author fkravchenko
 */
@Path("documents")
@Singleton
@Autowire
public class DocumentRESTFacade{
    @PersistenceContext(unitName = "petro21PU")
    protected EntityManager entityManager;
    private String rootPath = null;

    public DocumentRESTFacade() {
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Documents create(Document entity) {
        entity = entityManager.merge(entity);
        entityManager.flush();
        return new Documents(Arrays.asList(entity), 1l);
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Documents edit(Document entity) {
        entity = entityManager.merge(entity);
        return new Documents(Arrays.asList(entity), 1l);
    }

    @DELETE
    @Consumes({"application/xml", "application/json"})
    @Transactional
    public void remove(Document entity) {
        Long id = entity.getId();
        try {
            entity = entityManager.getReference(Document.class, entity.getId());

            // deleting files
            Collection<File> files = entity.getFiles();
            for(File f: files){
                String path = f.getPath();
                java.io.File file = new java.io.File(getRootPath() + path);
                if(file.exists() && file.isFile())
                    file.delete();
            }

            entityManager.remove(entity);
             UserSessions.info("ru.gispro.petrores.doc.service.DocumentRESTFacade", 
                      UserSessions.getFacadeCallRequestUser(), "REMOVE_DOCUMENT", "Remove document", id,
                      true,  "Document successfully removed"); 
        }
        catch( RuntimeException e){
             UserSessions.error("ru.gispro.petrores.doc.service.DocumentRESTFacade", 
                      UserSessions.getFacadeCallRequestUser(), "REMOVE_DOCUMENT", "Remove document", id,
                      false,  "Document removing error: " + e.toString(), e); 
             throw e;
        }
    }

    private String getRootPath(){
        if(rootPath==null){
            HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
            rootPath = req.getSession().getServletContext().getInitParameter("documentsPath");
            if(rootPath==null)
                rootPath = java.io.File.separator;
            else if(!rootPath.endsWith(java.io.File.separator))
                rootPath = rootPath + java.io.File.separator;
        }
        return rootPath;
    }
    
    
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Documents find(@PathParam("id") Long id) {
        List<Document>l = Arrays.asList(new Document[]{entityManager.find(Document.class, id)});
        return new Documents(l, (long)l.size());
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    @Transactional
    public String count() {
        try {
            Query query = entityManager.createQuery("SELECT count(o) FROM Document AS o");
            return query.getSingleResult().toString();
        } finally {
            entityManager.close();
        }
    }

    @GET
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Documents find(@DefaultValue("-100") @QueryParam("domainId") Integer domainId,
            @DefaultValue("-1") @QueryParam("limit") Integer maxResults,
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
            String where = domainId != -100
                            ? ("where o.domain.id="+domainId+" ")
                            : "";
            Query query = entityManager.createQuery("SELECT object(o) FROM Document AS o " + where+ orderBy);
            if (!all) {
                query.setMaxResults(maxResults);
                query.setFirstResult(firstResult);
            }
            return new Documents(query.getResultList(),
                    (Long) entityManager.createQuery("SELECT count(o) FROM Document AS o "  + where).getSingleResult());
        } finally {
            entityManager.close();
        }
    }
    
    
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @Path("find")
    @Transactional
    public Documents find(@RequestBody DocumentFindCriteria dfc) throws Exception {

        int[]parCnt = new int[]{0};
        
        StringBuilder from = new StringBuilder();
        StringBuilder where = new StringBuilder();
        
        String[]words;
        if(dfc.getWords()!=null && dfc.getWords().trim().length()>0){
            words = dfc.getWords().split(",\\s*");
        }else{
            words = null;
        }
        String[]fileNames;
        if (dfc.getFileNames()!=null && dfc.getFileNames().trim().length()>0){
            fileNames = dfc.getFileNames().split(",\\s*");
        }else{
            fileNames = null;
        }
        
        /*if(dfc.getSimpleSearch()!=null && dfc.getSimpleSearch().trim().length()>0){
            String[]ssWords = dfc.getSimpleSearch().split("\\s+");
            if(words==null){
                words = ssWords;
            }else{
                String[]newWrds = new String[words.length + ssWords.length];
                System.arraycopy(words, 0, newWrds, 0, words.length);
                System.arraycopy(ssWords, 0, newWrds, words.length, ssWords.length);
                words = newWrds;
            }
            
        }*/

        
        //http://social.msdn.microsoft.com/Forums/is/sqlsecurity/thread/8dd8e3c1-e9b7-48ed-880b-c3c8f816db66
        
        if(dfc.getSimpleSearch()!=null && dfc.getSimpleSearch().trim().length()>0){
            
            from.append("\ninner join \"file\" f on d.id = f.documentId");
            from.append("\ninner join callSolr('").
                    append(java.net.URLEncoder.encode(dfc.getSimpleSearch(), "UTF-8")).
                    append("') sc on sc.fileId=f.path  or SUBSTRING(sc.fileId, 5, LEN(sc.fileId))=f.path");
        }
        
        if(words!=null && words.length>0){
            from.append("\ninner join documentWord dw on dw.documentId=d.id");
            where.append("\nand (");
            for(int i=0;i<words.length;i++){
                if(i>0)
                    where.append(" or ");
                where.append("dw.word=?");
            }
            where.append(")");
        }
        if(fileNames!=null && fileNames.length>0){
            from.append("\ninner join \"file\" fff on fff.documentId=d.id");
            where.append("\nand (");
            for(int i=0;i<fileNames.length;i++){
                if(i>0)
                    where.append(" or ");
                where.append("fff.fileName=?"); // TODO
            }
            where.append(")");
        }

        Author[]authors;
        if(dfc.getAuthor()!=null){
            authors = dfc.getAuthor();
            ArrayList<Author>a = new ArrayList<Author>(authors.length);
            for(Author au : authors){
                if(au.getId()!=null){
                    a.add(au);
                }
            }
            authors = new Author[0];
            authors = a.toArray(authors);
        }else{
            authors = null;
        }
        if(authors!=null && authors.length>0){
            
            from.append("\ninner join documentAuthor da on da.documentId=d.id");
            where.append("\nand (");
            for(int i=0;i<authors.length;i++){
                if(i>0)
                    where.append(" or ");
                where.append("da.authorId=?");
            }
            where.append(")");
        }
        
        Long[]domainIds = new Long[0];
        if(dfc.getDomain()!=null && dfc.getDomain().getId()!=null){
            Domain dom = dfc.getDomain();
            dom = entityManager.getReference(Domain.class, dom.getId());
            ArrayList<Long> domIds = new ArrayList<Long>();
            domIds.add(dom.getId());
            Collection<Domain>subs =  dom.getAllSubDomains();
            for(Domain sub: subs){
                domIds.add(sub.getId());
            }
            domainIds = domIds.toArray(domainIds);
        }else{
            domainIds = null;
        }

        GeoObject[]geoObjs;
        if(dfc.getGeoObjects()!=null){
            geoObjs = dfc.getGeoObjects();
            ArrayList<GeoObject>a = new ArrayList<GeoObject>(geoObjs.length);
            for(GeoObject au : geoObjs){
                if(au.getTableName()!=null){
                    a.add(au);
                }
            }
            geoObjs = new GeoObject[0];
            geoObjs = a.toArray(geoObjs);
        }else{
            geoObjs = null;
        }
        if(geoObjs!=null && geoObjs.length>0){
            
            from.append("\ninner join documentGeoObject dgo on dgo.documentId=d.id");
            from.append("\ninner join geoObject go on dgo.geoObjectId=go.id");
            where.append("\nand (");
            for(int i=0;i<geoObjs.length;i++){
                if(i>0)
                    where.append(" or ");
                where.append("(go.idInTable=? and go.tableName=?)");
            }
            where.append(")");
        }
        
        addCriteriaFromArray("domainId", where, domainIds, false);
        addCriteriaFromTo("year", where, dfc.getYearFrom(), dfc.getYearTo());
        addEq("number", where, dfc.getNumber(), false);
        addEq("archiveNumber", where, dfc.getArchiveNumber(), false);
        addFullText("title", where, dfc.getTitle());
        addFullText("fullTitle", where, dfc.getFullTitle());
        addFullText("comment", where, dfc.getComment());
        addFullText("originationDetails", where, dfc.getOriginationDetails());
        addFullText("limitationDetails", where, dfc.getLimitationDetails());
        addCriteriaFromTo("originationDate", where, dfc.getOriginationDateFrom(), dfc.getOriginationDateTo());
        addCriteriaFromTo("approvalDate", where, dfc.getApprovalDateFrom(), dfc.getApprovalDateTo());
        addCriteriaFromTo("registrationDate", where, dfc.getRegistrationDateFrom(), dfc.getRegistrationDateTo());
        addCriteriaFromTo("placementDate", where, dfc.getPlacementDateFrom(), dfc.getPlacementDateTo());
        addCriteriaFromTo("updateDate", where, dfc.getUpdateDateFrom(), dfc.getUpdateDateTo());
        dfc.setPageCount((Integer[])addCriteriaFromArray("pageCount", where, dfc.getPageCount(), false));
        dfc.setScale( (Integer[]) addCriteriaFromArray("scale", where, dfc.getScale(), false) );
        dfc.setResolution( (Integer[]) addCriteriaFromArray("resolution", where, dfc.getResolution(), false) );
        dfc.setPageCount( (Integer[]) addCriteriaFromArray("pageCount", where, dfc.getPageCount(), false) );
        addEq("projectionCode", where, dfc.getProjectionCode(), false);
        
        
        
        //addFullText("d.*", where, dfc.getSimpleSearch());
        
        
        ///////////////////dfc.setSuperType( (SuperType[]) addCriteriaFromArray("type.superType", where, dfc.getSuperType(), true) );
        //addEq("filenames", sql, dfc.getFileNames());
        
        
        
        dfc.setType( (Type[]) addCriteriaFromArray("type", where, dfc.getType(), true) );
        dfc.setStage( (Stage[]) addCriteriaFromArray("stage", where, dfc.getStage(), true) );
        dfc.setSite( (Site[]) addCriteriaFromArray("site", where, dfc.getSite(), true) );
        dfc.setGeometryType( (GeoType[])  addCriteriaFromArray("geometryType", where, dfc.getGeometryType(), true) );
        dfc.setPlacer( (Author[]) addCriteriaFromArray("placer", where, dfc.getPlacer(), true) );
        dfc.setPeriodicity( (Periodicity[]) addCriteriaFromArray("periodicity", where, dfc.getPeriodicity(), true) );
        dfc.setClassification( (Classification[]) addCriteriaFromArray("classification", where, dfc.getClassification(), true) );
        dfc.setTypeOfWork( (TypeOfWork[]) addCriteriaFromArray("typeOfWork", where, dfc.getTypeOfWork(), true) );
        dfc.setWorkProcess( (WorkProcess[]) addCriteriaFromArray("workProcess", where, dfc.getWorkProcess(), true) );
        
        if(where.length()>4)
            where.replace(0, 4, "\n");
        where.insert(0, "select * from Document d "+from.toString()+ (where.length()>4?" where":""));
        
        System.out.println(where);
        
        Query q = entityManager.createNativeQuery(where.toString(), Document.class);
        
        addParameterFromArray(q, "word", words, false, parCnt);
        addParameterFromArray(q, "fileNames", fileNames, false, parCnt);
        addParameterFromArray(q, "authorId", authors, true, parCnt);
        //addParameterFromArray(q, "geoObjects", dfc.getGeoObjects(), true, parCnt);
        if(geoObjs!=null && geoObjs.length>0){
            for(GeoObject go: geoObjs){
                q.setParameter(++parCnt[0], go.getIdInTable());
                q.setParameter(++parCnt[0], go.getTableName());
            }
        }
        addParameterFromArray(q, "domainId", domainIds, false, parCnt);
        addParameterFromTo(q, "year", dfc.getYearFrom(), dfc.getYearTo(), parCnt);
        addParameter(q, "number", dfc.getNumber(), false, parCnt);
        addParameter(q, "archiveNumber", dfc.getArchiveNumber(), false, parCnt);
        addParameterFullText(q, "title", dfc.getTitle(), parCnt);
        addParameterFullText(q, "fullTitle", dfc.getFullTitle(), parCnt);
        addParameterFullText(q, "comment", dfc.getComment(), parCnt);
        addParameterFullText(q, "originationDetails", dfc.getOriginationDetails(), parCnt);
        addParameterFullText(q, "limitationDetails", dfc.getLimitationDetails(), parCnt);
        addParameterFromTo(q, "originationDate", dfc.getOriginationDateFrom(), dfc.getOriginationDateTo(), parCnt);
        addParameterFromTo(q, "approvalDate", dfc.getApprovalDateFrom(), dfc.getApprovalDateTo(), parCnt);
        addParameterFromTo(q, "registrationDate", dfc.getRegistrationDateFrom(), dfc.getRegistrationDateTo(), parCnt);
        addParameterFromTo(q, "placementDate", dfc.getPlacementDateFrom(), dfc.getPlacementDateTo(), parCnt);
        addParameterFromTo(q, "updateDate", dfc.getUpdateDateFrom(), dfc.getUpdateDateTo(), parCnt);
        addParameterFromArray(q, "pageCount", dfc.getPageCount(), false, parCnt);
        addParameterFromArray(q, "scale", dfc.getScale(), false, parCnt);
        addParameterFromArray(q, "resolution", dfc.getResolution(), false, parCnt);
        addParameter(q, "projectionCode", dfc.getProjectionCode(), false, parCnt);
        
        ///addParameterFromArray(q, "type.superType", dfc.getSuperType(), parCnt);
        //addParameterFromArray(q, "fileNames", dfc.getFileNames(), false, parCnt);
        //addParameter(q, "fileNames", dfc.getFileNames());
        
        
        
        
        addParameterFromArray(q, "type", dfc.getType(), true, parCnt);
        addParameterFromArray(q, "stage", dfc.getStage(), true, parCnt);
        addParameterFromArray(q, "site", dfc.getSite(), true, parCnt);
        addParameterFromArray(q, "geometryType", dfc.getGeometryType(), true, parCnt);
        addParameterFromArray(q, "placer", dfc.getPlacer(), true, parCnt);
        addParameterFromArray(q, "periodicity", dfc.getPeriodicity(), true, parCnt);
        addParameterFromArray(q, "classification", dfc.getClassification(), true, parCnt);
        addParameterFromArray(q, "typeOfWork", dfc.getTypeOfWork(), true, parCnt);
        addParameterFromArray(q, "workProcess", dfc.getWorkProcess(), true, parCnt);

        //addParameterFullText(q, "*", dfc.getSimpleSearch(), parCnt);
        
        if(parCnt[0]==0 && (dfc.getSimpleSearch()==null || dfc.getSimpleSearch().trim().length()==0)){
            return new Documents(Arrays.asList(new Document[0]), 0l);
        }
        
        List<Document> docs = q.getResultList();
        Documents ret = new Documents(docs, (long)docs.size());//100l);
        
        return ret;
    }
    
    private Object[] addCriteriaFromArray(
            String field, 
            StringBuilder sql,
            Object[]arr,
            boolean ormObj) throws Exception{
        if(arr!=null && arr.length>0){
            ArrayList arl = new ArrayList();
            
            //Method m = null;
            //if(ormObj)
            //    m = arr.getClass().getComponentType().getMethod("getId");
            
            for(Object item: arr){
                if(checkEmpty(item, ormObj)){
                        arl.add(item);
                }
            }
            arr = Arrays.copyOf(arr, arl.size());
            arr = arl.toArray(arr);
            
            //String replaced = field.replace('.', '_');
            
            if(arr.length>0){
                sql.append("\nand (");
                for(int i=0;i<arr.length;i++){
                    if(i!=0)
                        sql.append(" or ");
                    sql.append("d.");
                    sql.append(field);
                    if(ormObj)
                        sql.append("Id");
                    sql.append(" = ?");
                    //sql.append(i);
                }
                sql.append(")");
            }
        }
        return arr;
    }

    private void addParameterFromArray(
            Query q,
            String field, 
            Object[]arr,
            boolean ormObj,
            int[]parCnt) throws Exception{
        
        Method m = null;
        if(arr!=null && ormObj)
            m = arr.getClass().getComponentType().getMethod("getId");
        
        if(arr!=null && arr.length>0){
            for(int i=0;i<arr.length;i++){
                if(ormObj){
                    q.setParameter(++parCnt[0], m.invoke(arr[i]) );
                }
                else
                    if(checkEmpty(arr[i], ormObj))
                        q.setParameter(++parCnt[0], arr[i]);
            }
        }
    }
    
    private void addParameter(
            Query q,
            String field, 
            Object item,
            boolean ormObj,
            int[]parCnt) throws Exception{
        if(item!=null){
            if(ormObj){
                Method m = item.getClass().getMethod("getId");
                item = m.invoke(item);
            }
            
            if(checkEmpty(item, ormObj))
                q.setParameter(++parCnt[0], item);
        }
    }
    
    private void addParameterFullText(
            Query q,
            String field, 
            String item,
            int[]parCnt) throws Exception{
        if(item!=null){
            if(checkEmpty(item, false))
                q.setParameter(++parCnt[0], "formsof(thesaurus, \"" + item + "\"");
        }
    }
    
   private void addCriteriaFromTo(
            String field, 
            StringBuilder sql,
            Object from,
            Object to) throws Exception{
        
        if(checkEmpty(from, false)){
            if(checkEmpty(to, false)){
                sql.
                        append("\nand d.").
                        append(field).
                        append(" between ? and ?");
            }else{
                sql.
                        append("\nand d.").
                        append(field).
                        append(" >= ?");
            }
        }else{
            if(checkEmpty(to, false)){
                sql.
                        append("\nand d.").
                        append(field).
                        append(" <= ?");
            }
        }
    }
    
    
    private void addParameterFromTo(
            Query q,
            String field, 
            Object from,
            Object to,
            int[]parCnt) throws Exception{

        
        if(checkEmpty(from, false)){
            if(checkEmpty(to, false)){
                q.setParameter(++parCnt[0], from);
                q.setParameter(++parCnt[0], to);
            }else{
                q.setParameter(++parCnt[0], from);
            }
        }else{
            if(checkEmpty(to, false)){
                q.setParameter(++parCnt[0], to);
            }
        }
    }

    private void addEq(
            String field, 
            StringBuilder sql,
            Object what, 
            boolean ormObj) throws Exception{

        if(checkEmpty(what, ormObj)){
                sql.
                        append("\nand d.").
                        append(field);
                if(ormObj)
                    sql.append("Id");
                        
                sql.append(" = ?");
        }
    }
    
    private void addFullText(
            String field, 
            StringBuilder sql,
            Object what) throws Exception{

        if(checkEmpty(what, false)){
                sql.append("\nand freetext(").
                        append(field).
                        append(", ?)");
                        
        }
    }
    
    private boolean checkEmpty(Object item, boolean ormObj) throws Exception{
        
        if(item==null)
            return false;
        if(!(item instanceof String)){
            if(ormObj){
                Method m = item.getClass().getMethod("getId");
                item = m.invoke(item);
                return item!=null;
            }
            if(item instanceof Number){
                return ((Number)item).longValue()!=0;
            }
            return true;
        }
        return ((String)item).trim().length()>0;
    }

    
}
